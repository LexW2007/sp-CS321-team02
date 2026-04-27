package cs321.search;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Driver program for searching a persisted SSH B-Tree with a query file.
 */
public class SSHSearchBTree {
    private static final Set<Integer> VALID_TOP_FREQUENCIES = new HashSet<Integer>();

    static {
        VALID_TOP_FREQUENCIES.add(10);
        VALID_TOP_FREQUENCIES.add(25);
        VALID_TOP_FREQUENCIES.add(50);
    }

    public static void main(String[] args) {
        try {
            Arguments parsedArguments = parseArguments(args);
            List<TreeObject> results = searchBTree(parsedArguments);
            for (TreeObject result : results) {
                System.out.println(result.getKey() + " " + result.getCount());
            }
        } catch (ParseArgumentException exception) {
            printUsageAndExit(exception.getMessage());
        } catch (BTreeException | IOException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    public static Arguments parseArguments(String[] args) throws ParseArgumentException {
        Arguments parsedArguments = new Arguments();
        Set<String> seenArguments = new HashSet<String>();

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
                case "btree-file":
                    parsedArguments.btreeFile = value;
                    break;
                case "query-file":
                    parsedArguments.queryFile = value;
                    break;
                case "top-frequency":
                    parsedArguments.topFrequency = ParseArgumentUtils.convertStringToInt(value);
                    if (!VALID_TOP_FREQUENCIES.contains(parsedArguments.topFrequency)) {
                        throw new ParseArgumentException("--top-frequency must be one of 10, 25, or 50");
                    }
                    parsedArguments.topFrequencyProvided = true;
                    break;
                case "cache-size":
                    parsedArguments.cacheSize = ParseArgumentUtils.convertStringToInt(value);
                    ParseArgumentUtils.verifyRanges(parsedArguments.cacheSize, 100, 10000);
                    parsedArguments.cacheSizeProvided = true;
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
        if (parsedArguments.btreeFile == null || parsedArguments.btreeFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --btree-file");
        }
        if (parsedArguments.queryFile == null || parsedArguments.queryFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --query-file");
        }
        if (parsedArguments.useCache && !parsedArguments.cacheSizeProvided) {
            throw new ParseArgumentException("--cache-size is required when --cache=1");
        }

        verifyFileExists(parsedArguments.btreeFile, "BTree file");
        verifyFileExists(parsedArguments.queryFile, "Query file");

        return parsedArguments;
    }

    public static List<TreeObject> searchBTree(Arguments arguments) throws BTreeException, IOException {
        BTree tree = new BTree(arguments.btreeFile, arguments.useCache, arguments.cacheSize);
        try {
            List<TreeObject> results = new ArrayList<TreeObject>();
            for (String query : readQueries(arguments.queryFile)) {
                TreeObject found = tree.search(query);
                if (found != null) {
                    results.add(new TreeObject(found.getKey(), found.getCount()));
                } else if (arguments.debug == 1) {
                    System.err.println(query + " was not found");
                }
            }

            if (arguments.topFrequencyProvided) {
                return topByFrequency(results, arguments.topFrequency);
            }

            return results;
        } finally {
            tree.close();
        }
    }

    private static List<String> readQueries(String queryFile) throws IOException {
        ArrayList<String> queries = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(queryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String query = line.trim();
                if (!query.isEmpty()) {
                    queries.add(query);
                }
            }
        }
        return queries;
    }

    private static List<TreeObject> topByFrequency(List<TreeObject> results, int limit) {
        ArrayList<TreeObject> sorted = new ArrayList<TreeObject>(results);
        Collections.sort(sorted, new Comparator<TreeObject>() {
            @Override
            public int compare(TreeObject left, TreeObject right) {
                int countComparison = Long.compare(right.getCount(), left.getCount());
                if (countComparison != 0) {
                    return countComparison;
                }
                return left.getKey().compareTo(right.getKey());
            }
        });

        if (sorted.size() <= limit) {
            return sorted;
        }
        return new ArrayList<TreeObject>(sorted.subList(0, limit));
    }

    private static void verifyFileExists(String path, String label) throws ParseArgumentException {
        File file = new File(path);
        if (!file.isFile()) {
            throw new ParseArgumentException(label + " was not found: " + path);
        }
    }

    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.err.println("Usage: java -jar build/libs/SSHSearchBTree.jar --cache=<0|1> --degree=<n> " +
            "--btree-file=<btree-file> --query-file=<query-file> [--top-frequency=<10|25|50>] " +
            "[--cache-size=<n>] [--debug=<0|1>]");
        System.exit(1);
    }

    public static class Arguments {
        private boolean useCache;
        private boolean cacheProvided;
        private int requestedDegree;
        private boolean degreeProvided;
        private String btreeFile;
        private String queryFile;
        private int topFrequency;
        private boolean topFrequencyProvided;
        private int cacheSize;
        private boolean cacheSizeProvided;
        private int debug;
        private boolean debugProvided;
    }
}
