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
     * Analyzes the given file content to detect potential security issues.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects representing the detected vulnerabilities
     */
    List<ScanIssue> analyze(String fileContent);

}