package cs321.create;

import cs321.common.ParseArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extracts keys from the wrangled SSH log file used to build the B-Tree.
 * @author Damian Skeen, Maclean Dunkin
 */
public class SSHFileReader {
    // The supported tree types and their corresponding key formats are as follows:
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

    // Prevent instantiation of this utility class
    private SSHFileReader() {
    }

    /**
     * Reads a wrangled SSH log and extracts the ordered keys for a single tree type.
     * @param sshFile path to the wrangled log file
     * @param type tree type to extract
     * @return list of extracted keys
     * @throws IOException if the file cannot be read
     * @throws ParseArgumentException if the type or log format is invalid
     */
    public static List<String> readKeys(String sshFile, String type) throws IOException, ParseArgumentException {
        validateType(type);

        ArrayList<String> keys = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(sshFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String extractedKey = extractKey(line, type);
                if (extractedKey != null) {
                    keys.add(extractedKey);
                }
            }
        }

        return keys;
    }

    /**
     * Extracts the key for one specific tree type from a wrangled log line.
     * @param line wrangled SSH log line
     * @param type requested tree type
     * @return extracted key, or null when the line does not match the requested type
     * @throws ParseArgumentException if the line or type is invalid
     */
    public static String extractKey(String line, String type) throws ParseArgumentException {
        validateType(type);

        SSHLogEntry entry = parseEntry(line);
        switch (type) {
            case "accepted-ip":
                return entry.action.equals("Accepted") ? "Accepted-" + entry.address : null;
            case "accepted-time":
                return entry.action.equals("Accepted") ? "Accepted-" + entry.getHourMinute() : null;
            case "invalid-ip":
                return entry.action.equals("Invalid") ? "Invalid-" + entry.address : null;
            case "invalid-time":
                return entry.action.equals("Invalid") ? "Invalid-" + entry.getHourMinute() : null;
            case "failed-ip":
                return entry.action.equals("Failed") ? "Failed-" + entry.address : null;
            case "failed-time":
                return entry.action.equals("Failed") ? "Failed-" + entry.getHourMinute() : null;
            case "reverseaddress-ip":
                return isReverseAddressAction(entry.action) ? entry.action + "-" + entry.address : null;
            case "reverseaddress-time":
                return isReverseAddressAction(entry.action) ? entry.action + "-" + entry.getHourMinute() : null;
            case "user-ip":
                return isUserAction(entry.action) ? entry.subject + "-" + entry.address : null;
            default:
                throw new ParseArgumentException("Unsupported tree type: " + type);
        }
    }

    /**
     * Validates the tree type.
     * @param type the tree type to validate
     * @throws ParseArgumentException if the type is invalid
     */
    private static void validateType(String type) throws ParseArgumentException {
        if (!VALID_TYPES.contains(type)) {
            throw new ParseArgumentException("Unsupported tree type: " + type);
        }
    }

    /**
     * Checks if the action is a reverse address action.
     * @param action the action to check
     * @return true if the action is a reverse address action, false otherwise
     */
    private static boolean isReverseAddressAction(String action) {
        return action.equals("reverse") || action.equals("Address");
    }

    /**
     * Checks if the action is a user-related action.
     * @param action the action to check
     * @return true if the action is a user-related action, false otherwise
     */
    private static boolean isUserAction(String action) {
        return action.equals("Accepted") || action.equals("Invalid") || action.equals("Failed");
    }

    /**
     * Parses a wrangled SSH log line into its component parts.
     * @param line the log line to parse
     * @return the parsed log entry
     * @throws ParseArgumentException if the line is invalid
     */
    private static SSHLogEntry parseEntry(String line) throws ParseArgumentException {
        String trimmedLine = line == null ? "" : line.trim();
        if (trimmedLine.isEmpty()) {
            throw new ParseArgumentException("Encountered an empty SSH log line");
        }

        String[] tokens = trimmedLine.split("\\s+");
        if (tokens.length != 5) {
            throw new ParseArgumentException("Expected 5 columns in wrangled SSH log line: " + trimmedLine);
        }

        if (tokens[2].equals("Address")) {
            return new SSHLogEntry(tokens[0], tokens[1], tokens[2], tokens[4], tokens[3]);
        }

        return new SSHLogEntry(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]);
    }

    /**
     * Represents a parsed entry from the wrangled SSH log.
     */
    private static final class SSHLogEntry {
        private final String date;
        private final String time;
        private final String action;
        private final String subject;
        private final String address;
        // constructor for the SSHLogEntry class
        private SSHLogEntry(String date, String time, String action, String subject, String address) {
            this.date = date;
            this.time = time;
            this.action = action;
            this.subject = subject;
            this.address = address;
        }
        // helper method to get a time value from the SSHLogEntry in HH:MM format
        private String getHourMinute() throws ParseArgumentException {
            String[] timeParts = time.split(":");
            if (timeParts.length < 2) {
                throw new ParseArgumentException("Expected HH:MM:SS time but received: " + date + " " + time);
            }
            return timeParts[0] + ":" + timeParts[1];
        }
    }
}
