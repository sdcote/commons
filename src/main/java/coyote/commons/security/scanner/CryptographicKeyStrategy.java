package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Asymmetric and Cryptographic Keys in a given text.
 * It uses a regular expression to find high-entropy patterns that capture the boundaries
 * of private keys, such as "-----BEGIN RSA PRIVATE KEY-----" or "-----BEGIN OPENSSH PRIVATE KEY-----".
 */
public class CryptographicKeyStrategy implements DetectionStrategy {

    // Matches various private key boundaries, e.g., BEGIN PRIVATE KEY, BEGIN RSA PRIVATE KEY, etc.
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("-----BEGIN [A-Z0-9 ]*PRIVATE KEY[A-Z0-9 ]*-----");

    /**
     * Analyzes the provided file content to find cryptographic private keys.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where private keys were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        Matcher matcher = PRIVATE_KEY_PATTERN.matcher(fileContent);
        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), "Cryptographic Private Key detected"));
        }

        return issues;
    }
}
