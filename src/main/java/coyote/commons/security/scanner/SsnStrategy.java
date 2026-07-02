package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Social Security Numbers (SSNs) in a given text.
 * It uses a regular expression to match the standard US SSN format (AAA-GG-SSSS).
 */
public class SsnStrategy implements DetectionStrategy {
    // Basic US SSN regex pattern: AAA-GG-SSSS
    private static final Pattern SSN_PATTERN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");

    /**
     * Analyzes the provided file content to find SSNs.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where SSNs were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        Matcher matcher = SSN_PATTERN.matcher(fileContent);

        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), "SSN detected"));
        }
        return issues;
    }
}