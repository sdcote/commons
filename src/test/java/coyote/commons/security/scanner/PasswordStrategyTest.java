package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordStrategy}.
 */
public class PasswordStrategyTest {

    private final PasswordStrategy strategy = new PasswordStrategy();

    @Test
    public void testPasswordDetectionWithContext() {
        String content = "prefix_text_here; password=mySuperSecretValue123; suffix_text_here";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Potential hardcoded password/credential detected", issue.getNatureOfIssue());
        assertEquals(18, issue.getOffset());
        assertEquals(content.substring(Math.max(0, 18 - 20), Math.min(content.length(), 18 + 20)), issue.getContext());
    }

    @Test
    public void testPasswordAtStartBoundary() {
        String content = "password=secret1234567890abcdefghijklmnopqrstuvwxyz";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals(0, issue.getOffset());
        assertEquals(content.substring(0, 20), issue.getContext());
    }

    @Test
    public void testPasswordAtEndBoundary() {
        String content = "leading_characters_01234567890123456789; secret=abc";
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
