package cs321.search;

import cs321.common.ParseArgumentException;
import java.sql.*;

public class SSHSearchDatabase {

    private Connection connection;

    public SSHSearchDatabase(String dbFile) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
    }

    public Integer searchKey(String table, String key) throws SQLException {
        String sql = "SELECT count FROM " + table + " WHERE key = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            SSHSearchDatabaseArguments parsed = SSHSearchDatabaseArguments.parse(args);

            SSHSearchDatabase db = new SSHSearchDatabase(parsed.getDatabaseFile());
            Integer count = db.searchKey(parsed.getTableName(), parsed.getSearchKey());

            if (count != null) {
                System.out.println(parsed.getSearchKey() + " " + count);
            } else {
                System.out.println("NOT FOUND");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
