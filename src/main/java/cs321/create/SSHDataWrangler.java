package cs321.create;

import cs321.common.ParseArgumentException;


/**
 * The driver class for wrangling a raw SSH log file into a useful form.
 *
 * @author 
 */
public class SSHDataWrangler {

	private final static String rawSSHFile = null;
	private final static String SSHFile = null;

    /**
     * Main driver of program.
     * @param args
     */
    public static void main(String[] args) throws Exception 
	{
		System.out.println("Hello world from cs321.create.SSHDataWrangler.main");
        parseArguments(args);
        // other code    
	}


    /**
     * Process command line arguments.
     * @param args  The command line arguments passed to the main method.
     */
    public static void parseArguments(String[] args) throws ParseArgumentException
    {
        return;
    }


	/** 
	 * Print usage message and exit.
	 * @param errorMessage the error message for proper usage
	 */
	private static void printUsageAndExit(String errorMessage)
    {
        System.exit(1);
	}

}
