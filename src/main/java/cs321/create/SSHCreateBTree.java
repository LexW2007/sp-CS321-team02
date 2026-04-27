package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.SSHSearchDatabase;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Driver program for creating a B-Tree from the wrangled SSH log file.
 * @author Lex Watts, Damian Skeen, Maclean Dunkin
 */
public class SSHCreateBTree {
    // Valid tree types for the create flow.
    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
        "accepted-ip",
        "accepted-time",
        "invalid-ip",
        "invalid-time",
        "failed-ip",
        "failed-time",
        "reverseaddress-ip",
        "reverseaddress-time",
        "user-ip"
    ));

    /** Main method for the SSHCreateBTree program. */
    public static void main(String[] args) throws Exception {
        try {
            Arguments parsedArguments = parseArguments(args);
            createBTree(parsedArguments);
        } catch (ParseArgumentException exception) {
            printUsageAndExit(exception.getMessage());
        } catch (BTreeException | IOException | SQLException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    /**
     * Process command line arguments.
     * @param args command line arguments passed to the main method
     * @return validated arguments for the create flow
     * @throws ParseArgumentException when the command line is invalid
     */
    public static Arguments parseArguments(String[] args) throws ParseArgumentException {
        Arguments parsedArguments = new Arguments();
        Set<String> seenArguments = new HashSet<>();

        for (String argument : args) {
            if (!argument.startsWith("--") || !argument.contains("=")) {
                throw new ParseArgumentException("Expected arguments in the form --name=value");
            }

            int separatorIndex = argument.indexOf('=');
            String key = argument.substring(2, separatorIndex);
            String value = argument.substring(separatorIndex + 1);

            if (!seenArguments.add(key)) {
                throw new ParseArgumentException("Duplicate argument provided: --" + key);
            }

            switch (key) {
                case "cache":
                    int cacheSetting = ParseArgumentUtils.convertStringToInt(value);
                    ParseArgumentUtils.verifyRanges(cacheSetting, 0, 1);
                    parsedArguments.useCache = cacheSetting == 1;
                    parsedArguments.cacheProvided = true;
                    break;
                case "degree":
                    parsedArguments.requestedDegree = ParseArgumentUtils.convertStringToInt(value);
                    if (parsedArguments.requestedDegree < 0) {
                        throw new ParseArgumentException("Degree must be 0 or a positive integer");
                    }
                    parsedArguments.degreeProvided = true;
                    break;
                case "sshFile":
                    parsedArguments.sshFile = value;
                    break;
                case "type":
                    if (!VALID_TYPES.contains(value)) {
                        throw new ParseArgumentException("Unsupported tree type: " + value);
                    }
                    parsedArguments.type = value;
                    break;
                case "cache-size":
                    parsedArguments.cacheSize = ParseArgumentUtils.convertStringToInt(value);
                    ParseArgumentUtils.verifyRanges(parsedArguments.cacheSize, 100, 10000);
                    parsedArguments.cacheSizeProvided = true;
                    break;
                case "database":
                    if (!value.equals("yes") && !value.equals("no")) {
                        throw new ParseArgumentException("--database must be either yes or no");
                    }
                    parsedArguments.writeDatabase = value.equals("yes");
                    parsedArguments.databaseProvided = true;
                    break;
                case "debug":
                    parsedArguments.debug = ParseArgumentUtils.convertStringToInt(value);
                    ParseArgumentUtils.verifyRanges(parsedArguments.debug, 0, 1);
                    parsedArguments.debugProvided = true;
                    break;
                default:
                    throw new ParseArgumentException("Unknown argument: --" + key);
            }
        }

        if (!parsedArguments.cacheProvided) {
            throw new ParseArgumentException("Missing required argument: --cache");
        }
        if (!parsedArguments.degreeProvided) {
            throw new ParseArgumentException("Missing required argument: --degree");
        }
        if (parsedArguments.sshFile == null || parsedArguments.sshFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --sshFile");
        }
        if (parsedArguments.type == null) {
            throw new ParseArgumentException("Missing required argument: --type");
        }
        if (!parsedArguments.databaseProvided) {
            throw new ParseArgumentException("Missing required argument: --database");
        }
        if (parsedArguments.useCache && !parsedArguments.cacheSizeProvided) {
            throw new ParseArgumentException("--cache-size is required when --cache=1");
        }

        File sshFile = new File(parsedArguments.sshFile);
        if (!sshFile.isFile()) {
            throw new ParseArgumentException("SSH input file was not found: " + parsedArguments.sshFile);
        }

        return parsedArguments;
    }

    /**
     * Create the requested B-Tree and any requested checkpoint artifacts.
     *
     * @param arguments validated create arguments
     * @throws IOException if file I/O fails
     * @throws ParseArgumentException if the SSH log contents are invalid
     * @throws BTreeException if the tree cannot be created
     * @throws SQLException if database output fails
     */
    public static void createBTree(Arguments arguments)
        throws IOException, ParseArgumentException, BTreeException, SQLException {
        int actualDegree = BTree.resolveDegree(arguments.requestedDegree);
        String btreeFilename = buildBTreeFilename(arguments.sshFile, arguments.type, arguments.requestedDegree);

        deleteIfExists(btreeFilename);

        BTree tree = new BTree(actualDegree, btreeFilename, arguments.isUseCache(), arguments.getCacheSize());
        try {
            List<String> keys = SSHFileReader.readKeys(arguments.sshFile, arguments.type);
            for (String key : keys) {
                tree.insert(new TreeObject(key));
            }

            List<TreeObject> sortedObjects = tree.getSortedObjects();
            if (arguments.debug == 1) {
                writeDumpFile(sortedObjects, arguments.type, arguments.requestedDegree);
            }

            if (arguments.writeDatabase) {
                writeDatabase(sortedObjects, arguments.type);
            }

            if (arguments.useCache) {
                System.out.println(tree.getCacheStats());
            }
        } finally {
            tree.close();
        }
    }

    /** Writes the sorted objects to a dump file. 
     * @param sortedObjects the list of TreeObject instances to write to the dump file
     * @param type the tree type, used to construct the dump file name
     * @param requestedDegree the requested degree, used to construct the dump file name
     * @throws IOException if there is an error writing the dump file
     */
    private static void writeDumpFile(List<TreeObject> sortedObjects, String type, int requestedDegree)
        throws IOException {
        String dumpFilename = "dump-" + type + "." + requestedDegree + ".txt";

        try (PrintWriter writer = new PrintWriter(dumpFilename)) {
            for (TreeObject object : sortedObjects) {
                writer.println(object.getKey() + " " + object.getCount());
            }
        }
    }

    /**
     * Writes the sorted objects to the database, replacing any existing contents for the tree type.
     * @param sortedObjects the list of TreeObject instances to write to the database
     * @param type the tree type, used to construct the database table name
     * @throws SQLException if there is an error writing to the database
     */
    private static void writeDatabase(List<TreeObject> sortedObjects, String type) throws SQLException {
        SSHSearchDatabase database = new SSHSearchDatabase("SSHLogDB.db");
        try {
            database.replaceTableContents(type.replace("-", ""), sortedObjects);
        } finally {
            database.close();
        }
    }

    /** 
     * Deletes the file at the specified path if it exists, throwing an exception if the file cannot be deleted.
     * @param path the path of the file to delete if it exists
     * @throws IOException if the file exists but cannot be deleted
     */
    private static void deleteIfExists(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && !file.delete()) {
            throw new IOException("Unable to replace existing file: " + path);
        }
    }

    /** 
     * Builds the filename for the B-Tree file based on the SSH file, tree type, and requested degree.
     * @param sshFile the path to the SSH file
     * @param type the tree type
     * @param requestedDegree the requested degree
     * @return the constructed B-Tree filename
     */
    private static String buildBTreeFilename(String sshFile, String type, int requestedDegree) {
        return new File(sshFile).getName() + ".ssh.btree." + type + "." + requestedDegree;
    }

    /**
     * Print usage message and exit.
     * @param errorMessage the error message for proper usage
     */
    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.err.println("Usage: java -jar build/libs/SSHCreateBTree.jar --cache=<0|1> --degree=<n> " +
            "--sshFile=<ssh-file> --type=<tree-type> [--cache-size=<n>] --database=<yes|no> [--debug=<0|1>]");
        System.exit(1);
    }

    /**
     * Value object for the create program's arguments.
     */
    public static class Arguments {
        private boolean useCache;
        private boolean cacheProvided;
        private int requestedDegree;
        private boolean degreeProvided;
        private String sshFile;
        private String type;
        private int cacheSize;
        private boolean cacheSizeProvided;
        private boolean writeDatabase;
        private boolean databaseProvided;
        private int debug;
        private boolean debugProvided;

        public boolean isUseCache() {
            return useCache;
        }

        public int getRequestedDegree() {
            return requestedDegree;
        }

        public String getSshFile() {
            return sshFile;
        }

        public String getType() {
            return type;
        }

        public int getCacheSize() {
            return cacheSize;
        }

        public boolean isWriteDatabase() {
            return writeDatabase;
        }

        public int getDebug() {
            return debug;
        }
    }
}
