package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Credit Card Numbers (PANs).
 * It uses regular expressions to match major issuer prefixes and lengths,
 * and validates the extracted sequences using the Luhn Algorithm.
 */
public class CreditCardStrategy implements DetectionStrategy {

    // Matches sequences of digits that could be credit card numbers,
    // allowing for optional spaces or hyphens between digits.
    // Length is between 13 and 19 digits.
    private static final Pattern PAN_PATTERN = Pattern.compile("\\b(?:\\d[ -]*){12,18}\\d\\b");

    /**
     * Analyzes the provided file content to find Credit Card Numbers.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where credit card numbers were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        Matcher matcher = PAN_PATTERN.matcher(fileContent);
        while (matcher.find()) {
            String match = matcher.group();
            String cleanNumber = match.replaceAll("[ -]", "");
            
            if (isValidIssuerAndLength(cleanNumber) && passesLuhnCheck(cleanNumber)) {
                issues.add(new ScanIssue(matcher.start(), "Credit Card Number (PAN) detected"));
            }
        }

        return issues;
    }

    private boolean isValidIssuerAndLength(String number) {
        int length = number.length();
        
        // Visa: Starts with 4, length 13 or 16
        if (number.startsWith("4") && (length == 13 || length == 16)) {
            return true;
        }
        
        // Mastercard: Starts with 51-55 or 2221-2720, length 16
        if (length == 16) {
            int prefix2 = Integer.parseInt(number.substring(0, 2));
            if (prefix2 >= 51 && prefix2 <= 55) {
                return true;
            }
            int prefix4 = Integer.parseInt(number.substring(0, 4));
            if (prefix4 >= 2221 && prefix4 <= 2720) {
                return true;
            }
        }
        
        // Amex: Starts with 34 or 37, length 15
        if ((number.startsWith("34") || number.startsWith("37")) && length == 15) {
            return true;
        }
        
        // Discover: Starts with 6011 or 65, length 16
        if ((number.startsWith("6011") || number.startsWith("65")) && length == 16) {
            return true;
        }
        
        return false;
    }

    private boolean passesLuhnCheck(String number) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
}
