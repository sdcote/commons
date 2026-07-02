package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that scans for strings with high Shannon Entropy, which indicates
 * a high degree of randomness. This is characteristic of cryptographic keys, salts, and strong passwords.
 * 
 * If a string appears near an assignment operator (e.g., = or :) and its calculated
 * entropy exceeds specific thresholds (e.g., 3.2 for Hex, 4.5 for Base64), it is flagged.
 * This approach finds secrets regardless of specific keyword matches in variable names.
 */
public class EntropyStrategy implements DetectionStrategy {

    // Matches assignments like: variable = "value" or "key": "value"
    // Group 1: The opening quote (if any)
    // Group 2: The assigned string value (at least 16 characters for meaningful entropy)
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(
            "[:=]\\s*(['\"]?)([A-Za-z0-9+/=_-]{16,})\\1"
    );

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-Fa-f]+$");
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]+={0,2}$");

    // Standard practical thresholds for secret detection.
    // Note: The maximum theoretical Shannon entropy for a pure hex string is 4.0 (log2 of 16),
    // and for base64 it is 6.0 (log2 of 64). Thus, using 3.2 for hex and 4.5 for base64.
    private static final double HEX_ENTROPY_THRESHOLD = 3.2;
    private static final double BASE64_ENTROPY_THRESHOLD = 4.5;

    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        Matcher matcher = ASSIGNMENT_PATTERN.matcher(fileContent);
        while (matcher.find()) {
            String value = matcher.group(2);
            int offset = matcher.start(2);

            if (isHighEntropy(value)) {
                issues.add(new ScanIssue(offset, "High entropy string detected (possible key/secret)"));
            }
        }

        return issues;
    }

    /**
     * Determines if the given string has high Shannon entropy based on its character set.
     */
    private boolean isHighEntropy(String s) {
        double entropy = getShannonEntropy(s);
        
        if (HEX_PATTERN.matcher(s).matches()) {
            return entropy >= HEX_ENTROPY_THRESHOLD;
        } else if (BASE64_PATTERN.matcher(s).matches()) {
            return entropy >= BASE64_ENTROPY_THRESHOLD;
        }
        
        // For mixed/other strings, default to the base64 threshold
        return entropy >= BASE64_ENTROPY_THRESHOLD;
    }

    /**
     * Calculates the Shannon entropy of the given string.
     */
    public static double getShannonEntropy(String s) {
        if (s == null || s.isEmpty()) {
            return 0.0;
        }
        
        Map<Character, Integer> charCounts = new HashMap<>();
        for (char c : s.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }
        
        double entropy = 0.0;
        int length = s.length();
        for (int count : charCounts.values()) {
            double probability = (double) count / length;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        
        return entropy;
    }
}
