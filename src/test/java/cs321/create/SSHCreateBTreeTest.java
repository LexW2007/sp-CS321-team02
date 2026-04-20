package cs321.create;

import cs321.btree.BTree;
import cs321.btree.SSHSearchDatabase;
import cs321.common.ParseArgumentException;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Focused tests for the checkpoint-2 SSHCreateBTree flow.
 */
public class SSHCreateBTreeTest {
    private static final String TEST_SSH_FILE = "Test_SSH_log.txt";
    private static final String BTREE_FILE = "Test_SSH_log.txt.ssh.btree.accepted-ip.0";
    private static final String DUMP_FILE = "dump-accepted-ip.0.txt";
    private static final String DATABASE_FILE = "SSHLogDB.db";

    @After
    public void cleanup() {
        deleteIfExists(TEST_SSH_FILE);
        deleteIfExists(BTREE_FILE);
        deleteIfExists(DUMP_FILE);
        deleteIfExists(DATABASE_FILE);
    }

    @Test
    public void testResolveDegreeZeroUsesOptimalBlockSizedDegree() throws Exception {
        assertEquals(43, BTree.resolveDegree(0));
    }

    @Test
    public void testExtractKeyMappings() throws Exception {
        assertEquals(
            "Accepted-119.137.62.142",
            SSHFileReader.extractKey("12/10 09:32:20 Accepted fztu 119.137.62.142", "accepted-ip")
        );
        assertEquals(
            "Invalid-07:28",
            SSHFileReader.extractKey("12/10 07:28:03 Invalid pgadmin 112.95.230.3", "invalid-time")
        );
        assertEquals(
            "Address-82.209.247.10",
            SSHFileReader.extractKey("12/11 05:59:02 Address 82.209.247.10 mail.trionis.ru", "reverseaddress-ip")
        );
        assertEquals(
            "webmaster-173.234.31.186",
            SSHFileReader.extractKey("12/10 06:55:48 Failed webmaster 173.234.31.186", "user-ip")
        );
        assertEquals(
            "reverse-173.234.31.186",
            SSHFileReader.extractKey("12/10 06:55:46 reverse ns.marryaldkfaczcz.com 173.234.31.186", "reverseaddress-ip")
        );
        assertEquals(
            null,
            SSHFileReader.extractKey("12/11 05:59:02 Address 82.209.247.10 mail.trionis.ru", "user-ip")
        );
    }

    @Test
    public void testParseArgumentsRequiresCacheSizeWhenCacheEnabled() throws Exception {
        try {
            SSHCreateBTree.parseArguments(new String[] {
                "--cache=1",
                "--degree=0",
                "--sshFile=" + TEST_SSH_FILE,
                "--type=accepted-ip",
                "--database=yes"
            });
            fail("Expected parseArguments to require --cache-size when cache is enabled");
        } catch (ParseArgumentException expected) {
            assertTrue(expected.getMessage().contains("--cache-size"));
        }
    }

    @Test
    public void testCreateBTreeWritesDumpAndDatabase() throws Exception {
        writeSampleSshFile();

        SSHCreateBTree.Arguments arguments = SSHCreateBTree.parseArguments(new String[] {
            "--cache=1",
            "--degree=0",
            "--sshFile=" + TEST_SSH_FILE,
            "--type=accepted-ip",
            "--cache-size=100",
            "--database=yes",
            "--debug=1"
        });

        SSHCreateBTree.createBTree(arguments);

        assertTrue(new File(BTREE_FILE).exists());
        assertTrue(new File(DUMP_FILE).exists());
        assertEquals("Accepted-119.137.62.142 2", readFirstLine(DUMP_FILE));

        SSHSearchDatabase database = new SSHSearchDatabase(DATABASE_FILE);
        assertEquals(2, database.getCount("acceptedip", "Accepted-119.137.62.142"));
    }

    private void writeSampleSshFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(TEST_SSH_FILE)) {
            writer.println("12/10 06:55:46 reverse ns.marryaldkfaczcz.com 173.234.31.186");
            writer.println("12/10 06:55:46 Invalid webmaster 173.234.31.186");
            writer.println("12/10 06:55:48 Failed webmaster 173.234.31.186");
            writer.println("12/10 09:32:20 Accepted fztu 119.137.62.142");
            writer.println("12/10 09:32:21 Accepted fztu 119.137.62.142");
        }
    }

    private String readFirstLine(String path) throws IOException {
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(path));
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    private void deleteIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
