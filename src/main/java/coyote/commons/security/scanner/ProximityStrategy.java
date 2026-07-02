package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that scans for generic high-entropy strings or potential tokens
 * only if they occur within a specific window of text near anchor keywords like
 * SECRET, TOKEN, BEARER, IDENTITY, or SALT.
 */
public class ProximityStrategy implements DetectionStrategy {

    // Anchor keywords indicating potential secrets
    private static final Pattern ANCHOR_PATTERN = Pattern.compile(
            "(?i)\\b(secret|token|bearer|identity|salt)\\b"
    );

    // Potential tokens (at least 16 characters, base64/hex alphabet)
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?<![A-Za-z0-9+/=_-])[A-Za-z0-9+/=_-]{16,}(?![A-Za-z0-9+/=_-])"
    );

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-Fa-f]+$");
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]+={0,2}$");

    private static final double HEX_ENTROPY_THRESHOLD = 3.2;
    private static final double BASE64_ENTROPY_THRESHOLD = 4.5;

    // Proximity window size in characters
    private static final int WINDOW_SIZE = 50;

    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        List<int[]> anchors = new ArrayList<>();
        Matcher anchorMatcher = ANCHOR_PATTERN.matcher(fileContent);
        while (anchorMatcher.find()) {
            anchors.add(new int[]{anchorMatcher.start(), anchorMatcher.end()});
        }

        if (anchors.isEmpty()) {
            return issues;
        }

        Matcher tokenMatcher = TOKEN_PATTERN.matcher(fileContent);
        while (tokenMatcher.find()) {
            String token = tokenMatcher.group();
            int tokenStart = tokenMatcher.start();
            int tokenEnd = tokenMatcher.end();

            boolean nearAnchor = false;
            for (int[] anchor : anchors) {
                int anchorStart = anchor[0];
                int anchorEnd = anchor[1];
                
                int distance = Math.max(0, Math.max(anchorStart - tokenEnd, tokenStart - anchorEnd));
                
                if (distance <= WINDOW_SIZE) {
                    nearAnchor = true;
                    break;
                }
            }

            if (nearAnchor && isHighEntropy(token)) {
                issues.add(new ScanIssue(tokenStart, "High-entropy token found near anchor keyword"));
            }
        }

        return issues;
    }

    /**
     * Determines if the given string has high Shannon entropy based on its character set.
     */
    private boolean isHighEntropy(String s) {
        double entropy = EntropyStrategy.getShannonEntropy(s);
        
        if (HEX_PATTERN.matcher(s).matches()) {
            return entropy >= HEX_ENTROPY_THRESHOLD;
        } else if (BASE64_PATTERN.matcher(s).matches()) {
            return entropy >= BASE64_ENTROPY_THRESHOLD;
        }
        
        return entropy >= BASE64_ENTROPY_THRESHOLD;
    }
}
