package cs321.search;

import cs321.common.ParseArgumentException;

public class SSHSearchDatabaseArguments {

    private final String databaseFile;
    private final String searchKey;
    private final String tableName;

    public SSHSearchDatabaseArguments(String databaseFile, String searchKey, String tableName) {
        this.databaseFile = databaseFile;
        this.searchKey = searchKey;
        this.tableName = tableName;
    }

    public String getDatabaseFile() { return databaseFile; }
    public String getSearchKey() { return searchKey; }
    public String getTableName() { return tableName; }

    public static SSHSearchDatabaseArguments parse(String[] args) throws ParseArgumentException {
        String dbFile = null;
        String searchKey = null;
        String table = null;

        if (args == null || args.length == 0)
            throw new ParseArgumentException("No arguments provided.");

        for (String a : args) {
            if (!a.startsWith("--")) continue;

            String[] parts = a.substring(2).split("=", 2);
            String k = parts[0].toLowerCase();
            String v = parts.length > 1 ? parts[1] : "";

            switch (k) {
                case "database-file":
                    dbFile = v;
                    break;

                case "search-key":
                    searchKey = v;
                    break;

                case "table":
                    table = v;
                    break;

                default:
                    // ignore unknown flags
            }
        }

        if (dbFile == null)
            throw new ParseArgumentException("Missing required argument: --database-file");
        if (searchKey == null)
            throw new ParseArgumentException("Missing required argument: --search-key");
        if (table == null)
            throw new ParseArgumentException("Missing required argument: --table");

        return new SSHSearchDatabaseArguments(dbFile, searchKey, table);
    }
}
