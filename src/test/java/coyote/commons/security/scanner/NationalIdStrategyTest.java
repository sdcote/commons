package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NationalIdStrategy}.
 */
public class NationalIdStrategyTest {

    private final NationalIdStrategy strategy = new NationalIdStrategy();

    @Test
    public void testValidSsn() {
        String content = "Employee SSN is 123-45-6789 and must be kept secure.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("US Social Security Number (SSN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(16, issues.get(0).getOffset());
    }

    @Test
    public void testValidSsnWithoutHyphens() {
        String content = "id=123456789";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("US Social Security Number (SSN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(3, issues.get(0).getOffset());
    }

    @Test
    public void testInvalidSsnAreaNumber() {
        // Area number 000
        assertTrue(strategy.analyze("000-45-6789").isEmpty(), "SSN with 000 area should not be detected");
        // Area number 666
        assertTrue(strategy.analyze("666-45-6789").isEmpty(), "SSN with 666 area should not be detected");
        // Area number in 900s
        assertTrue(strategy.analyze("912-45-6789").isEmpty(), "SSN with 900s area should not be detected");
    }

    @Test
    public void testInvalidSsnGroupAndSerial() {
        // Group number 00
        assertTrue(strategy.analyze("123-00-6789").isEmpty(), "SSN with 00 group should not be detected");
        // Serial number 0000
        assertTrue(strategy.analyze("123-45-0000").isEmpty(), "SSN with 0000 serial should not be detected");
    }

    @Test
    public void testValidNino() {
        String content = "Your NINO is QQ 12 34 56 A, please verify.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("UK National Insurance Number (NINO) detected", issues.get(0).getNatureOfIssue());
        assertEquals(13, issues.get(0).getOffset());
    }

    @Test
    public void testValidNinoNoSpaces() {
        String content = "nino=QQ123456A";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("UK National Insurance Number (NINO) detected", issues.get(0).getNatureOfIssue());
        assertEquals(5, issues.get(0).getOffset());
    }

    @Test
    public void testNoMatches() {
        String content = "This is a clean file with numbers like 123456 and 12-34-56 that do not match patterns.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
