package cs321.search;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Focused tests for the checkpoint-3 SSHSearchDatabase flow.
 * @author Lex Watts, Maclean Dunkin
 */
public class  SSHSearchDatabaseTest
{
    private static final String TEST_DATABASE = "Test_SearchDatabase.db";

    /**
     * Cleans up the test database after each test to ensure a clean state for tests.
     */
    @After
    public void cleanup() {
        File file = new File(TEST_DATABASE);
        if (file.exists()) {
            file.delete();
        }
    }

    /** 
     * Creates a test database with the correct search for the top results
     */
    @Test
    public void testCreateTestDatabaseAndSearchTopResults() throws Exception {
        SSHSearchDatabase database = new SSHSearchDatabase(TEST_DATABASE);
        try {
            database.createTestDatabase();
            List<SSHSearchDatabase.Result> results = database.searchTop("acceptedip", 3);

            assertEquals(3, results.size());
            assertEquals("Accepted-111.222.107.90", results.get(0).getKey());
            assertEquals(25, results.get(0).getCount());
            assertEquals("Accepted-119.137.63.195", results.get(1).getKey());
            assertEquals(14, results.get(1).getCount());
            assertEquals("Accepted-119.137.62.123", results.get(2).getKey());
            assertEquals(9, results.get(2).getCount());
        } finally {
            database.close();
        }
    }
}
