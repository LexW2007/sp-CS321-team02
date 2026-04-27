package cs321.search;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SSHSearchDatabase {

    private final Connection connection;

    public SSHSearchDatabase(String dbFile) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
    }

    public List<Result> searchTop(String table, int topFrequency) throws SQLException {
        String sql = "SELECT key, count FROM " + table + " ORDER BY count DESC, key ASC LIMIT ?";
        ArrayList<Result> results = new ArrayList<Result>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, topFrequency);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(new Result(rs.getString("key"), rs.getLong("count")));
            }
        }

        return results;
    }

    public void createTestDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS acceptedip;");
            statement.executeUpdate("CREATE TABLE acceptedip (key TEXT PRIMARY KEY, count INTEGER NOT NULL);");
        }

        String sql = "INSERT INTO acceptedip (key, count) VALUES (?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Result result : testRows()) {
                statement.setString(1, result.key);
                statement.setLong(2, result.count);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) {
        SSHSearchDatabase db = null;
        try {
            SSHSearchDatabaseArguments parsed = SSHSearchDatabaseArguments.parse(args);
            db = new SSHSearchDatabase(parsed.getDatabaseFile());

            if (parsed.getType().equals("test")) {
                db.createTestDatabase();
                return;
            }

            for (Result result : db.searchTop(parsed.getTableName(), parsed.getTopFrequency())) {
                System.out.println(result.key + " " + result.count);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (SQLException exception) {
                    System.err.println("Error closing database: " + exception.getMessage());
                }
            }
        }
    }

    private static List<Result> testRows() {
        ArrayList<Result> rows = new ArrayList<Result>();
        rows.add(new Result("Accepted-111.222.107.90", 25));
        rows.add(new Result("Accepted-112.96.173.55", 3));
        rows.add(new Result("Accepted-112.96.33.40", 3));
        rows.add(new Result("Accepted-113.116.236.34", 6));
        rows.add(new Result("Accepted-113.118.187.34", 2));
        rows.add(new Result("Accepted-113.99.127.215", 2));
        rows.add(new Result("Accepted-119.137.60.156", 1));
        rows.add(new Result("Accepted-119.137.62.123", 9));
        rows.add(new Result("Accepted-119.137.62.142", 1));
        rows.add(new Result("Accepted-119.137.63.195", 14));
        rows.add(new Result("Accepted-123.255.103.142", 5));
        rows.add(new Result("Accepted-123.255.103.215", 5));
        rows.add(new Result("Accepted-137.189.204.138", 1));
        rows.add(new Result("Accepted-137.189.204.155", 1));
        rows.add(new Result("Accepted-137.189.204.220", 1));
        rows.add(new Result("Accepted-137.189.204.236", 1));
        rows.add(new Result("Accepted-137.189.204.246", 1));
        rows.add(new Result("Accepted-137.189.204.253", 3));
        rows.add(new Result("Accepted-137.189.205.44", 2));
        rows.add(new Result("Accepted-137.189.206.152", 1));
        rows.add(new Result("Accepted-137.189.206.243", 1));
        rows.add(new Result("Accepted-137.189.207.18", 1));
        rows.add(new Result("Accepted-137.189.207.28", 1));
        rows.add(new Result("Accepted-137.189.240.159", 1));
        rows.add(new Result("Accepted-137.189.241.19", 2));
        return rows;
    }

    public static class Result {
        private final String key;
        private final long count;

        public Result(String key, long count) {
            this.key = key;
            this.count = count;
        }

        public String getKey() {
            return key;
        }

        public long getCount() {
            return count;
        }
    }
}
