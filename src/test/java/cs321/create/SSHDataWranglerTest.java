package cs321.create;

import cs321.common.ParseArgumentException;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SSHDataWranglerTest {
    private static final String RAW_FILE = "Test_SSH_raw_log.txt";
    private static final String WRANGLED_FILE = "Test_SSH_wrangled_log.txt";

    @After
    public void cleanup() {
        deleteIfExists(RAW_FILE);
        deleteIfExists(WRANGLED_FILE);
    }

    @Test
    public void testParseArgumentsRequiresRawAndOutputFiles() throws Exception {
        try {
            SSHDataWrangler.parseArguments(new String[] {
                "--rawSshFile=" + RAW_FILE
            });
            fail("Expected parseArguments to require --sshFile");
        } catch (ParseArgumentException expected) {
            assertTrue(expected.getMessage().contains("--sshFile"));
        }
    }

    @Test
    public void testWrangleLineHandlesDocumentedRawFormats() {
        assertEquals(
            "12/12 18:46:17 Accepted suyuxin 218.18.43.243",
            SSHDataWrangler.wrangleLine(
                "Dec 12 18:46:17 LabSZ sshd[31166]: Accepted password for suyuxin from 218.18.43.243 port 9480 ssh2"
            )
        );
        assertEquals(
            "12/12 18:58:24 Invalid zouzhi 115.71.16.143",
            SSHDataWrangler.wrangleLine("Dec 12 18:58:24 LabSZ sshd[31243]: Invalid user zouzhi from 115.71.16.143")
        );
        assertEquals(
            "12/12 18:58:26 Failed zouzhi 115.71.16.143",
            SSHDataWrangler.wrangleLine(
                "Dec 12 18:58:26 LabSZ sshd[31243]: Failed password for invalid user zouzhi from 115.71.16.143 port 38790 ssh2"
            )
        );
        assertEquals(
            "12/12 19:31:09 reverse 190.174.14.217",
            SSHDataWrangler.wrangleLine(
                "Dec 12 19:31:09 LabSZ sshd[31479]: reverse mapping checking getaddrinfo for 190-174-14-217.speedy.com.ar [190.174.14.217] failed - POSSIBLE BREAK-IN ATTEMPT!"
            )
        );
        assertEquals(
            "12/12 21:02:41 Address 123.16.30.186",
            SSHDataWrangler.wrangleLine(
                "Dec 12 21:02:41 LabSZ sshd[31596]: Address 123.16.30.186 maps to static.vnpt.vn, but this does not map back to the address - POSSIBLE BREAK-IN ATTEMPT!"
            )
        );
    }

    @Test
    public void testWrangleFileWritesOnlyRelevantLines() throws Exception {
        try (PrintWriter writer = new PrintWriter(RAW_FILE)) {
            writer.println("Dec 12 18:46:17 LabSZ sshd[31166]: Accepted password for suyuxin from 218.18.43.243 port 9480 ssh2");
            writer.println("Dec 12 18:58:24 LabSZ sshd[31243]: Invalid user zouzhi from 115.71.16.143");
            writer.println("Dec 12 18:58:26 LabSZ sshd[31243]: Connection closed by authenticating user root 1.2.3.4 port 22");
            writer.println("12/12 6:46pm SSHD Lab-id:[abaff] --- Accepted password for suyuxin from [218.18.43.243]");
        }

        SSHDataWrangler.wrangle(RAW_FILE, WRANGLED_FILE);

        List<String> lines = readLines(WRANGLED_FILE);
        assertEquals(3, lines.size());
        assertEquals("12/12 18:46:17 Accepted suyuxin 218.18.43.243", lines.get(0));
        assertEquals("12/12 18:58:24 Invalid zouzhi 115.71.16.143", lines.get(1));
        assertEquals("12/12 18:46:00 Accepted suyuxin 218.18.43.243", lines.get(2));
    }

    private List<String> readLines(String path) throws Exception {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private void deleteIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
