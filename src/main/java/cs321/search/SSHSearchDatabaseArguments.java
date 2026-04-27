package cs321.search;

import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates the arguments for the SSHSearchDatabase program.
 * @author Lex Watts, Maclean Dunkin
 */
public class SSHSearchDatabaseArguments {
    // The supported tree types and their corresponding key formats are as follows:
    private static final Set<String> VALID_TYPES = new HashSet<String>(Arrays.asList(
        "accepted-ip",
        "accepted-time",
        "invalid-ip",
        "invalid-time",
        "failed-ip",
        "failed-time",
        "reverseaddress-ip",
        "reverseaddress-time",
        "user-ip",
        "test"
    ));

    private static final Set<Integer> VALID_TOP_FREQUENCIES = new HashSet<Integer>(Arrays.asList(10, 25, 50));

    private final String databaseFile;
    private final String type;
    private final int topFrequency;

    /** 
     * Constructor for database search arguments.
     * @param databaseFile
     * @param type
     * @param topFrequency
     * @throws ParseArgumentException if any of the arguments are invalid`
     */
    public SSHSearchDatabaseArguments(String databaseFile, String type, int topFrequency) {
        this.databaseFile = databaseFile;
        this.type = type;
        this.topFrequency = topFrequency;
    }

    // Getters for the arguments
    public String getDatabaseFile() { return databaseFile; }
    public String getType() { return type; }
    public int getTopFrequency() { return topFrequency; }
    public String getTableName() { return type.replace("-", ""); }

    /**
     * Parses the command-line arguments and returns an SSHSearchDatabaseArguments object.
     * @param args
     * @return
     * @throws ParseArgumentException
     */
    public static SSHSearchDatabaseArguments parse(String[] args) throws ParseArgumentException {
        String databaseFile = null;
        String type = null;
        Integer topFrequency = null;
        Set<String> seenArguments = new HashSet<String>();

        if (args == null || args.length == 0)
            throw new ParseArgumentException("No arguments provided.");

        for (String argument : args) {
            if (!argument.startsWith("--") || !argument.contains("=")) {
                throw new ParseArgumentException("Expected arguments in the form --name=value");
            }

            String[] parts = argument.substring(2).split("=", 2);
            String key = parts[0];
            String value = parts.length > 1 ? parts[1] : "";

            if (!seenArguments.add(key)) {
                throw new ParseArgumentException("Duplicate argument provided: --" + key);
            }

            switch (key) {
                case "database":
                    databaseFile = value;
                    break;
                case "type":
                    if (!VALID_TYPES.contains(value)) {
                        throw new ParseArgumentException("Unsupported tree type: " + value);
                    }
                    type = value;
                    break;
                case "top-frequency":
                    topFrequency = ParseArgumentUtils.convertStringToInt(value);
                    if (!VALID_TOP_FREQUENCIES.contains(topFrequency)) {
                        throw new ParseArgumentException("--top-frequency must be one of 10, 25, or 50");
                    }
                    break;
                default:
                    throw new ParseArgumentException("Unknown argument: --" + key);
            }
        }

        if (databaseFile == null || databaseFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --database");
        }
        if (type == null) {
            throw new ParseArgumentException("Missing required argument: --type");
        }
        if (topFrequency == null) {
            throw new ParseArgumentException("Missing required argument: --top-frequency");
        }

        return new SSHSearchDatabaseArguments(databaseFile, type, topFrequency);
    }
}
