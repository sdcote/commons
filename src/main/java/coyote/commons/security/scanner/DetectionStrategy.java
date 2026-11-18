package coyote.commons.security.scanner;

import java.util.List;

/**
 * Defines a strategy for detecting security vulnerabilities or sensitive data
 * within a file's content.
 */
public interface DetectionStrategy {

    /**
     * Sets the scanner engine for this strategy. This enables strategies
     * to recursively scan decoded or extracted blocks of text.
     *
     * @param scanner the {@link Scanner} engine using this strategy
     */
    default void setScanner(Scanner scanner) {
        // Default implementation does nothing
    }

    /**
     * Extracts a context substring surrounding the given character offset within the scanned text.
     * The extracted context contains up to 20 characters before the offset and up to 20 characters
     * after the offset (for a maximum target length of 40 characters).
     *
     * @param text   the scanned input string
     * @param offset the character offset of the detected issue
     * @return the surrounding context substring, or an empty string if text is null or empty
     */
    default String extractContext(String text, int offset) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int clampedOffset = Math.max(0, Math.min(offset, text.length()));
        int start = Math.max(0, clampedOffset - 20);
        int end = Math.min(text.length(), clampedOffset + 20);
        return text.substring(start, end);
    }


    /**
     * Analyzes the given file content to detect potential security issues.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects representing the detected vulnerabilities
     */
    List<ScanIssue> analyze(String fileContent);

}