package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies National Identification Numbers.
 * Currently supports:
 * - US Social Security Numbers (SSNs), excluding invalid combinations 
 *   (e.g., area numbers 000, 666, 900-999; group number 00; serial 0000).
 * - UK National Insurance Numbers (NINO).
 */
public class NationalIdStrategy implements DetectionStrategy {

    // US SSN pattern with basic exclusions
    // Matches formats: AAA-GG-SSSS, AAAGGSSSS, or AAA GG SSSS
    private static final Pattern US_SSN_PATTERN = Pattern.compile(
            "\\b(?!000|666|9\\d\\d)\\d{3}[- ]?(?!00)\\d{2}[- ]?(?!0000)\\d{4}\\b"
    );

    // UK NINO pattern
    // Two prefix letters, 6 digits, one suffix letter A-D
    private static final Pattern UK_NINO_PATTERN = Pattern.compile(
            "(?i)\\b[A-Z]{2}[- ]?\\d{2}[- ]?\\d{2}[- ]?\\d{2}[- ]?[A-D]\\b"
    );

    /**
     * Analyzes the provided file content to find National Identification Numbers.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where ID numbers were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        findMatches(fileContent, US_SSN_PATTERN, "US Social Security Number (SSN) detected", issues);
        findMatches(fileContent, UK_NINO_PATTERN, "UK National Insurance Number (NINO) detected", issues);

        return issues;
    }

    /**
     * Helper method to find matches for a given pattern and add them to the issues list.
     *
     * @param content     the text to be analyzed
     * @param pattern     the compiled regular expression to match against the text
     * @param issueNature the description of the nature of the issue found
     * @param issues      the list of issues to populate with any matches
     */
    private void findMatches(String content, Pattern pattern, String issueNature, List<ScanIssue> issues) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), issueNature));
        }
    }
}
