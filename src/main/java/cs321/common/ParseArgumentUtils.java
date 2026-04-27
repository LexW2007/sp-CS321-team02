package cs321.common;

public class ParseArgumentUtils
{
    /**
     * Verifies if lowRangeInclusive <= argument <= highRangeInclusive
     */
    public static void verifyRanges(int argument, int lowRangeInclusive, int highRangeInclusive) throws ParseArgumentException
    {
        if (argument < lowRangeInclusive || argument > highRangeInclusive) {
            throw new ParseArgumentException(
                String.format(
                    "Argument value %d is outside the allowed range [%d, %d]",
                    argument,
                    lowRangeInclusive,
                    highRangeInclusive
                )
            );
        }
    }

    public static int convertStringToInt(String argument) throws ParseArgumentException
    {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new ParseArgumentException("Expected an integer value but received: " + argument);
        }
    }
}
