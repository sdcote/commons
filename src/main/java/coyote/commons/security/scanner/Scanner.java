package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * A security scanning engine that analyzes file content for vulnerabilities
 * using multiple registered {@link DetectionStrategy} instances.
 */
public class Scanner {
    private final List<DetectionStrategy> strategies;

    /**
     * Constructs a new {@code Scanner} with an empty list of detection
     * strategies.
     */
    public Scanner() {
        this.strategies = new ArrayList<>();
    }

    /**
     * Registers a new detection capability into the scanner engine.
     *
     * @param strategy the {@link DetectionStrategy} to add
     */
    public void addStrategy(DetectionStrategy strategy) {
        if (strategy != null) {
            strategy.setScanner(this);
            this.strategies.add(strategy);
        }
    }


    /**
     * Audits the provided HTML content using all registered strategies.
     *
     * @param fileContent the content to scan
     * @return a list of all {@link ScanIssue} objects found by the registered strategies
     */
    public List<ScanIssue> scan(String fileContent) {
        List<ScanIssue> allIssues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return allIssues;
        }

        for (DetectionStrategy strategy : strategies) {
            allIssues.addAll(strategy.analyze(fileContent));
        }

        return allIssues;
    }

}