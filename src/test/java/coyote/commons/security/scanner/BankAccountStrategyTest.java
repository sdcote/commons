package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BankAccountStrategy}.
 */
public class BankAccountStrategyTest {

    private final BankAccountStrategy strategy = new BankAccountStrategy();

    @Test
    public void testValidAbaRoutingNumber() {
        // Valid ABA RTN for JPMorgan Chase, New York: 021000021
        String content = "Please transfer to routing 021000021 today.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("ABA Routing Transit Number detected", issues.get(0).getNatureOfIssue());
        assertEquals(27, issues.get(0).getOffset());
    }

    @Test
    public void testInvalidAbaRoutingNumber() {
        // Invalid ABA RTN (checksum fails: last digit changed from 1 to 2)
        String content = "Please transfer to routing 021000022 today.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should not detect ABA routing number with invalid checksum");
    }

    @Test
    public void testValidIban() {
        // Valid IBAN for UK
        // We use a known test IBAN: GB47 MIDL 4005 1561 2816 99
        String content = "My IBAN is GB47 MIDL 4005 1561 2816 99 for wire transfer.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("International Bank Account Number (IBAN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(11, issues.get(0).getOffset());
    }

    @Test
    public void testValidIbanWithoutSpaces() {
        String content = "iban=GB47MIDL40051561281699";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("International Bank Account Number (IBAN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(5, issues.get(0).getOffset());
    }

    @Test
    public void testInvalidIbanChecksum() {
        // Invalid IBAN (checksum GB48 instead of GB47)
        String content = "My IBAN is GB48 MIDL 4005 1561 2816 99 for wire transfer.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should not detect IBAN with invalid checksum");
    }

    @Test
    public void testNoMatches() {
        String content = "Just a clean file with 12345678 and some text.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
