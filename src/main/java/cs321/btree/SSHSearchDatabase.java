package cs321.btree;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Class for managing a simple SQLite database to store SSH keys and their counts.
 * This is a utility class for the SSHSearchBTree program.
 * Used in Checkpoint 1 with fake SSH key data.
 * @author Lex Watts
 */
public class SSHSearchDatabase {

    private String dbName;

    public SSHSearchDatabase(String dbName) {
        this.dbName = dbName;
    }

    /** Connect to SQLite 
     * @throws SQLException if a database access error occurs
     * @return Connection object to the SQLite database
    */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    /** Create a table for SSH keys
     * If the table already exists, it is not recreated.
     * @param tableName Name of the table to create.
     * @throws SQLException if a database access error occurs
     */
    public void createTable(String tableName) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "key TEXT PRIMARY KEY, " +
                     "count INTEGER" +
                     ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Drop a table if it already exists so repeated create runs replace, rather than
     * accumulate into, an old checkpoint database.
     *
     * @param tableName name of the table to drop
     * @throws SQLException if a database access error occurs
     */
    public void dropTable(String tableName) throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + tableName + ";";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /** Insert or update a key into the database
     * @param tableName Name of the table to insert into.
     * @param key The SSH key to insert or update.
     * @param count The count to insert (or increment if key already exists).
     * @throws SQLException if a database access error occurs
     */
    public void insertKey(String tableName, String key, long count) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?) " +
                     "ON CONFLICT(key) DO UPDATE SET count = count + excluded.count;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setLong(2, count);
            pstmt.executeUpdate();
        }
    }

    /**
     * Replace the contents of a table with the given entries in a single transaction.
     *
     * @param tableName name of the table to populate
     * @param entries keys and counts to write
     * @throws SQLException if a database access error occurs
     */
    public void replaceTableContents(String tableName, List<TreeObject> entries) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?);";

        dropTable(tableName);
        createTable(tableName);

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (TreeObject entry : entries) {
                pstmt.setString(1, entry.getKey());
                pstmt.setLong(2, entry.getCount());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
        }
    }

    /** Retrieve all keys from the database in sorted order
     * @param tableName Name of the table to query.
     * @return List of keys in sorted order.
     * @throws SQLException if a database access error occurs
     */
    public List<String> getAllKeys(String tableName) throws SQLException {
        List<String> keys = new ArrayList<>();

        String sql = "SELECT key FROM " + tableName + " ORDER BY key ASC;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                keys.add(rs.getString("key"));
            }
        }

        return keys;
    }

    /** Retrieve count for a specific key 
     * @param tableName Name of the table to query.
     * @param key The SSH key to look up.
     * @return The count associated with the key, or 0 if the key does not exist.
     * @throws SQLException if a database access error occurs
    */
    public long getCount(String tableName, String key) throws SQLException {
        String sql = "SELECT count FROM " + tableName + " WHERE key = ?;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        }

        return 0;
    }

    /** Main method for testing the SSHSearchDatabase class
     * Uses fake SSh key data to demonstrate functionality.
     * @throws Exception if a database access error occurs
    */
    public static void main(String[] args) throws Exception {
        SSHSearchDatabase db = new SSHSearchDatabase("test.db");

        db.createTable("sshkeys");

        db.insertKey("sshkeys", "alpha", 1);
        db.insertKey("sshkeys", "beta", 1);
        db.insertKey("sshkeys", "alpha", 1); // duplicate → increments count

        System.out.println("Keys: " + db.getAllKeys("sshkeys"));
        System.out.println("alpha count = " + db.getCount("sshkeys", "alpha"));
    }
}