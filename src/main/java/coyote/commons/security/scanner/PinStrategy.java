package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies potential Personal Identification Numbers (PINs)
 * in a given text.
 * <p>
 * It scans for PIN indicators (such as "PIN", "Pin", "PIN#", "PIN#:", "PIN code",
 * "PIN number", "Personal Identification Number") and detects numeric PIN sequences
 * located within a specified proximity window.
 * </p>
 */
public class PinStrategy implements DetectionStrategy {

    /**
     * Anchor pattern matching various representations and casings of PIN keywords,
     * such as "PIN", "Pin", "PIN#", "PIN#:", "PIN code", "PIN number", "pin_code",
     * or "Personal Identification Number".
     */
    private static final Pattern ANCHOR_PATTERN = Pattern.compile(
            "(?i)\\b(?:personal\\s+identification\\s+number|pin(?:[-_\\s]*(?:number|code|num|no|id))?\\b(?:\\s*#)?(?::|=)?)"
    );

    /**
     * Candidate PIN pattern matching 4 to 8 digit numbers, either contiguous
     * or grouped with standard space or hyphen delimiters (e.g., 1234, 123456, 12-34, 123-456).
     */
    private static final Pattern PIN_CANDIDATE_PATTERN = Pattern.compile(
            "(?<![\\d\\-])(?:\\d{4,8}|\\d{2,4}[- ]\\d{2,4})(?![\\d\\-])"
    );

    /**
     * Default character distance window for proximity detection.
     */
    private static final int DEFAULT_WINDOW_SIZE = 50;

    /**
     * Description of the nature of issue reported when a PIN is detected.
     */
    private static final String ISSUE_NATURE = "Personal Identification Number (PIN) detected";

    private final int windowSize;

    /**
     * Constructs a new {@code PinStrategy} with the default proximity window size.
     */
    public PinStrategy() {
        this(DEFAULT_WINDOW_SIZE);
    }

    /**
     * Constructs a new {@code PinStrategy} with a custom proximity window size.
     *
     * @param windowSize the maximum character distance between an anchor keyword and a numeric PIN candidate
     */
    public PinStrategy(int windowSize) {
        this.windowSize = Math.max(0, windowSize);
    }

    /**
     * Analyzes the provided file content to detect potential Personal Identification Numbers (PINs).
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects representing detected PIN occurrences
     */
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

        Matcher candidateMatcher = PIN_CANDIDATE_PATTERN.matcher(fileContent);
        while (candidateMatcher.find()) {
            int tokenStart = candidateMatcher.start();
            int tokenEnd = candidateMatcher.end();

            boolean nearAnchor = false;
            for (int[] anchor : anchors) {
                int anchorStart = anchor[0];
                int anchorEnd = anchor[1];

                int distance = Math.max(0, Math.max(anchorStart - tokenEnd, tokenStart - anchorEnd));
                if (distance <= windowSize) {
                    nearAnchor = true;
                    break;
                }
            }

            if (nearAnchor) {
                issues.add(new ScanIssue(tokenStart, ISSUE_NATURE, extractContext(fileContent, tokenStart)));
            }
        }

        return issues;
    }

    /**
     * Gets the configured proximity window size in characters.
     *
     * @return the proximity window size
     */
    public int getWindowSize() {
        return windowSize;
    }
}
