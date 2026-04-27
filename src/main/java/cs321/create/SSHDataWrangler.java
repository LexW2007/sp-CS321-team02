package cs321.create;

import cs321.common.ParseArgumentException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The driver class for wrangling a raw SSH log file into a useful form.
 *
 * @author
 */
public class SSHDataWrangler {
    private static final Pattern SYSLOG_PREFIX = Pattern.compile(
        "^(\\w{3})\\s+(\\d{1,2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\S+\\s+sshd\\[\\d+\\]:\\s+(.+)$"
    );
    private static final Pattern DEMO_PREFIX = Pattern.compile(
        "^(\\d{1,2}/\\d{1,2})\\s+(\\d{1,2}:\\d{2})(am|pm)\\s+SSHD\\s+Lab-id:\\[[a-z]+\\]\\s+---\\s+(.+)$",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern ACCEPTED = Pattern.compile("^Accepted password for (\\S+) from \\[?([\\d.]+)\\]?");
    private static final Pattern INVALID = Pattern.compile("^Invalid user (\\S+) from \\[?([\\d.]+)\\]?");
    private static final Pattern FAILED_INVALID = Pattern.compile("^Failed password for invalid user (\\S+) from \\[?([\\d.]+)\\]?");
    private static final Pattern FAILED_USER = Pattern.compile("^Failed password for (\\S+) from \\[?([\\d.]+)\\]?");
    private static final Pattern REVERSE = Pattern.compile(
        "^reverse mapping checking getaddrinfo for .+ \\[?([\\d.]+)\\]? failed"
    );
    private static final Pattern ADDRESS = Pattern.compile("^Address \\[?([\\d.]+)\\]? maps to .+");

    /**
     * Main driver of program.
     * @param args
     */
    public static void main(String[] args) throws Exception {
        try {
            Arguments arguments = parseArguments(args);
            wrangle(arguments.rawSshFile, arguments.sshFile);
        } catch (ParseArgumentException | IOException exception) {
            printUsageAndExit(exception.getMessage());
        }
    }


    /**
     * Process command line arguments.
     * @param args  The command line arguments passed to the main method.
     */
    public static Arguments parseArguments(String[] args) throws ParseArgumentException {
        Arguments arguments = new Arguments();
        Set<String> seenArguments = new HashSet<String>();

        if (args == null || args.length == 0) {
            throw new ParseArgumentException("No arguments provided.");
        }

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
                case "rawSshFile":
                    arguments.rawSshFile = value;
                    break;
                case "sshFile":
                    arguments.sshFile = value;
                    break;
                default:
                    throw new ParseArgumentException("Unknown argument: --" + key);
            }
        }

        if (arguments.rawSshFile == null || arguments.rawSshFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --rawSshFile");
        }
        if (arguments.sshFile == null || arguments.sshFile.trim().isEmpty()) {
            throw new ParseArgumentException("Missing required argument: --sshFile");
        }
        if (!new File(arguments.rawSshFile).isFile()) {
            throw new ParseArgumentException("Raw SSH input file was not found: " + arguments.rawSshFile);
        }

        return arguments;
    }

    public static void wrangle(String rawSshFile, String sshFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(rawSshFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(sshFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String wrangledLine = wrangleLine(line);
                if (wrangledLine != null) {
                    writer.write(wrangledLine);
                    writer.newLine();
                }
            }
        }
    }

    public static String wrangleLine(String rawLine) {
        ParsedPrefix parsedPrefix = parsePrefix(rawLine);
        if (parsedPrefix == null) {
            return null;
        }

        String activity = parseActivity(parsedPrefix.message);
        if (activity == null) {
            return null;
        }

        return parsedPrefix.date + " " + parsedPrefix.time + " " + activity;
    }

    private static ParsedPrefix parsePrefix(String rawLine) {
        String line = rawLine == null ? "" : rawLine.trim();

        Matcher syslogMatcher = SYSLOG_PREFIX.matcher(line);
        if (syslogMatcher.matches()) {
            return new ParsedPrefix(
                monthNumber(syslogMatcher.group(1)) + "/" + syslogMatcher.group(2),
                syslogMatcher.group(3),
                syslogMatcher.group(4)
            );
        }

        Matcher demoMatcher = DEMO_PREFIX.matcher(line);
        if (demoMatcher.matches()) {
            return new ParsedPrefix(
                demoMatcher.group(1),
                convertDemoTime(demoMatcher.group(2), demoMatcher.group(3)),
                demoMatcher.group(4)
            );
        }

        return null;
    }

    private static String parseActivity(String message) {
        Matcher matcher = ACCEPTED.matcher(message);
        if (matcher.find()) {
            return "Accepted " + matcher.group(1) + " " + matcher.group(2);
        }

        matcher = INVALID.matcher(message);
        if (matcher.find()) {
            return "Invalid " + matcher.group(1) + " " + matcher.group(2);
        }

        matcher = FAILED_INVALID.matcher(message);
        if (matcher.find()) {
            return "Failed " + matcher.group(1) + " " + matcher.group(2);
        }

        matcher = FAILED_USER.matcher(message);
        if (matcher.find()) {
            return "Failed " + matcher.group(1) + " " + matcher.group(2);
        }

        matcher = REVERSE.matcher(message);
        if (matcher.find()) {
            return "reverse " + matcher.group(1);
        }

        matcher = ADDRESS.matcher(message);
        if (matcher.find()) {
            return "Address " + matcher.group(1);
        }

        return null;
    }

    private static String monthNumber(String month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month)) {
                return String.valueOf(i + 1);
            }
        }

        return month;
    }

    private static String convertDemoTime(String time, String period) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        if (period.equalsIgnoreCase("pm") && hour < 12) {
            hour += 12;
        } else if (period.equalsIgnoreCase("am") && hour == 12) {
            hour = 0;
        }
        return String.format("%02d:%s:00", hour, parts[1]);
    }


    /**
     * Print usage message and exit.
     * @param errorMessage the error message for proper usage
     */
    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.err.println("Usage: java -jar build/libs/SSHDataWrangler.jar " +
            "--rawSshFile=<raw-ssh-file> --sshFile=<wrangled-ssh-file>");
        System.exit(1);
    }

    public static class Arguments {
        private String rawSshFile;
        private String sshFile;

        public String getRawSshFile() {
            return rawSshFile;
        }

        public String getSshFile() {
            return sshFile;
        }
    }

    private static final class ParsedPrefix {
        private final String date;
        private final String time;
        private final String message;

        private ParsedPrefix(String date, String time, String message) {
            this.date = date;
            this.time = time;
            this.message = message;
        }
    }
}
