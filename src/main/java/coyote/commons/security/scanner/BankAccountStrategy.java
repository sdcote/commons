package coyote.commons.security.scanner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Bank Routing and Account Numbers.
 * It currently supports:
 * - ABA Routing Transit Numbers (9 digits, validated with a checksum).
 * - International Bank Account Numbers (IBAN) (up to 34 alphanumeric characters, validated with Modulo 97).
 */
public class BankAccountStrategy implements DetectionStrategy {

    // Matches 9 consecutive digits for ABA routing numbers
    private static final Pattern ABA_PATTERN = Pattern.compile("\\b\\d{9}\\b");

    // Matches potential IBANs in either contiguous electronic format or standard print format (blocks of 4 characters).
    private static final Pattern IBAN_PATTERN = Pattern.compile(
            "\\b[A-Z]{2}[0-9]{2}(?:[ -]?[A-Z0-9]{4}){2,7}(?:[ -]?[A-Z0-9]{1,4})?\\b|\\b[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}\\b",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Analyzes the provided file content to find Bank Routing and Account Numbers.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where account numbers were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        findAbaRoutingNumbers(fileContent, issues);
        findIbans(fileContent, issues);

        return issues;
    }

    private void findAbaRoutingNumbers(String content, List<ScanIssue> issues) {
        Matcher matcher = ABA_PATTERN.matcher(content);
        while (matcher.find()) {
            String match = matcher.group();
            if (isValidAbaRoutingNumber(match)) {
                issues.add(new ScanIssue(matcher.start(), "ABA Routing Transit Number detected"));
            }
        }
    }

    private void findIbans(String content, List<ScanIssue> issues) {
        Matcher matcher = IBAN_PATTERN.matcher(content);
        while (matcher.find()) {
            String match = matcher.group();
            String cleanMatch = match.replaceAll("[\\s\\-]+", "").toUpperCase();
            if (isValidIban(cleanMatch)) {
                issues.add(new ScanIssue(matcher.start(), "International Bank Account Number (IBAN) detected"));
            }
        }
    }

    /**
     * Validates an ABA routing transit number using its checksum algorithm.
     * 3 * (d1 + d4 + d7) + 7 * (d2 + d5 + d8) + 1 * (d3 + d6 + d9) must be a multiple of 10.
     */
    private boolean isValidAbaRoutingNumber(String number) {
        if (number == null || number.length() != 9) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 9; i += 3) {
            sum += Character.getNumericValue(number.charAt(i)) * 3
                 + Character.getNumericValue(number.charAt(i + 1)) * 7
                 + Character.getNumericValue(number.charAt(i + 2));
        }
        return sum != 0 && sum % 10 == 0;
    }

    /**
     * Validates an IBAN using the Modulo 97 algorithm.
     */
    private boolean isValidIban(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34) {
            return false;
        }
        
        // Move the first 4 characters to the end
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        
        // Convert to integer string
        StringBuilder numericIban = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numericIban.append(c);
            } else if (Character.isLetter(c)) {
                // Character.getNumericValue('A') returns 10, 'B' returns 11, etc.
                numericIban.append(Character.getNumericValue(c));
            } else {
                return false;
            }
        }
        
        // Perform mod 97
        try {
            BigInteger value = new BigInteger(numericIban.toString());
            return value.mod(BigInteger.valueOf(97)).intValue() == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
