package cs321.btree;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SSHSearchDatabase {

    private final String dbName;
    private final Connection connection;

    public SSHSearchDatabase(String dbName) throws SQLException {
        this.dbName = dbName;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        initTables();
    }

        private void initTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS acceptedip (" +
                     "key TEXT PRIMARY KEY, " +
                     "count INTEGER NOT NULL" +
                     ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void createTable(String tableName) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "key TEXT PRIMARY KEY, " +
                     "count INTEGER" +
                     ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertKey(String tableName, String key, long count) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?) " +
                     "ON CONFLICT(key) DO UPDATE SET count = count + excluded.count;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setLong(2, count);
            pstmt.executeUpdate();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

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

    public void replaceTableContents(String replace, List<TreeObject> sortedObjects) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String sql = "INSERT INTO " + replace + " (key, count) VALUES (?, ?) " +
                         "ON CONFLICT(key) DO UPDATE SET count = excluded.count;";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (TreeObject obj : sortedObjects) {
                    pstmt.setString(1, obj.getKey());
                    pstmt.setLong(2, obj.getCount());
                    pstmt.addBatch();
                    System.out.println("INSERTING: " + obj.getKey() + " freq=" + obj.getCount());
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
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
