package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ScanIssue}.
 */
public class ScanIssueTest {

    @Test
    public void testScanIssueProperties() {
        ScanIssue issue = new ScanIssue(42, "Sensitive Token", "surrounding context text");
        assertEquals(42, issue.getOffset());
        assertEquals("Sensitive Token", issue.getNatureOfIssue());
        assertEquals("surrounding context text", issue.getContext());
        assertTrue(issue.toString().contains("42"));
        assertTrue(issue.toString().contains("Sensitive Token"));
        assertTrue(issue.toString().contains("surrounding context text"));
    }
}
