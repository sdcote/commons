package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SsnStrategy}.
 */
public class SsnStrategyTest {

    private final SsnStrategy strategy = new SsnStrategy();

    @Test
    public void testSsnDetectionWithContext() {
        String content = "User details record: 123-45-6789 is the social security number.";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("SSN detected", issue.getNatureOfIssue());
        assertEquals(21, issue.getOffset());
        assertEquals(content.substring(Math.max(0, 21 - 20), Math.min(content.length(), 21 + 20)), issue.getContext());
    }

    @Test
    public void testSsnAtStartBoundary() {
        String content = "123-45-6789 followed by trailing text information";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals(0, issue.getOffset());
        assertEquals(content.substring(0, 20), issue.getContext());
    }

    @Test
    public void testSsnAtEndBoundary() {
        String content = "User SSN record for verification: 123-45-6789";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        int offset = issue.getOffset();
        assertEquals(content.substring(offset - 20, content.length()), issue.getContext());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
