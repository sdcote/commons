package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies potential hardcoded passwords or credentials in a given text.
 * It uses a regular expression to find common password-related keywords followed by assignment operators.
 */
public class PasswordStrategy implements DetectionStrategy {
    // Looks for patterns like password=value or "password": "value"
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(?i)\\b(password|passwd|secret|pwd)\\s*[:=]\\s*[\"']?([^\"'\\s>]+)[\"']?",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Analyzes the provided file content to find potential hardcoded passwords.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where passwords were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        Matcher matcher = PASSWORD_PATTERN.matcher(fileContent);

        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), "Potential hardcoded password/credential detected"));
        }
        return issues;
    }
}