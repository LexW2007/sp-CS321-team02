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

    public void insertKey(String tableName, String key, int count) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (key, count) VALUES (?, ?) " +
                     "ON CONFLICT(key) DO UPDATE SET count = count + excluded.count;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setInt(2, count);
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
}
