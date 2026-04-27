package cs321.btree;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A database for storing and retrieving search results.
 * @author Lex Watts, Damian Skeen
 */
public class SSHSearchDatabase {

    private final String dbName;
    private final Connection connection;

    /**
     * Constructor for the SSHSearchDatabase class.
     * @param dbName The name of the database file.
     * @throws SQLException If there is an error connecting to the database.
     */
    public SSHSearchDatabase(String dbName) throws SQLException {
        this.dbName = dbName;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    /**
     * Creates a table in the database.
     * @param tableName The name of the table to create.
     * @throws SQLException If there is an error executing the SQL statement.
     */
    public void createTable(String tableName) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "key TEXT PRIMARY KEY, " +
                     "count INTEGER" +
                     ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Inserts a key into the database or updates its count if it already exists.
     * @param tableName The name of the table to insert into.
     * @param key The key to insert.
     * @param count The count to insert or update.
     * @throws SQLException If there is an error executing the SQL statement.
     */
    public void insertKey(String tableName, String key, long count) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?) " +
                     "ON CONFLICT(key) DO UPDATE SET count = count + excluded.count;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setLong(2, count);
            pstmt.executeUpdate();
        }
    }

    /**
     * Closes the database connection.
     * @throws SQLException If there is an error closing the database connection.
     */
    public void close() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Retrieves all keys from the specified table.
     * @param tableName The name of the table to query.
     * @return A list of all keys in the table.
     * @throws SQLException If there is an error executing the SQL statement.
     */
    public List<String> getAllKeys(String tableName) throws SQLException {
        List<String> keys = new ArrayList<>();

        String sql = "SELECT key FROM " + tableName + " ORDER BY key ASC;";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                keys.add(rs.getString("key"));
            }
        }

        return keys;
    }

    /** 
     * Retrieves the count for a specific key in the specified table.
     * @param tableName The name of the table to query.
     * @param key The key to look up.
     * @throws SQLException If there is an error executing the SQL statement.
     * @return The count for the specified key, or 0 if the key does not exist in the table.
     */
    public long getCount(String tableName, String key) throws SQLException {
        String sql = "SELECT count FROM " + tableName + " WHERE key = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        }

        return 0;
    }

    /** Replaces the contents of the specified table with the given list of TreeObject instances.
     * @param tableName The name of the table to replace.
     * @param sortedObjects The list of TreeObject instances to insert.
     * @throws SQLException If there is an error executing the SQL statement.
     */
    public void replaceTableContents(String tableName, List<TreeObject> sortedObjects) throws SQLException {
        createTable(tableName);

        try {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM " + tableName + ";");
            }

            String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?) " +
                         "ON CONFLICT(key) DO UPDATE SET count = excluded.count;";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (TreeObject obj : sortedObjects) {
                    pstmt.setString(1, obj.getKey());
                    pstmt.setLong(2, obj.getCount());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                // ignore
            }
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
