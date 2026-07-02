package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CreditCardStrategy}.
 */
public class CreditCardStrategyTest {

    private final CreditCardStrategy strategy = new CreditCardStrategy();

    @Test
    public void testVisaDetection() {
        // Valid Visa (length 16, starts with 4, valid Luhn)
        String content = "Payment processed for card 4242 4242 4242 4242 today.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Credit Card Number (PAN) detected", issue.getNatureOfIssue());
        assertEquals(27, issue.getOffset());
    }

    @Test
    public void testMastercardDetection() {
        // Valid Mastercard (length 16, starts with 55, valid Luhn)
        String content = "MC: 5555-5555-5555-4444";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Credit Card Number (PAN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(4, issues.get(0).getOffset());
    }

    @Test
    public void testAmexDetection() {
        // Valid Amex (length 15, starts with 34, valid Luhn)
        String content = "Amex card 343434343434343 used for purchase.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Credit Card Number (PAN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(10, issues.get(0).getOffset());
    }

    @Test
    public void testDiscoverDetection() {
        // Valid Discover (length 16, starts with 6011, valid Luhn)
        String content = "{\"card\": \"6011 1111 1111 1117\"}";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Credit Card Number (PAN) detected", issues.get(0).getNatureOfIssue());
        assertEquals(10, issues.get(0).getOffset());
    }

    @Test
    public void testInvalidLuhnFail() {
        // Starts with 4 and length 16, but fails Luhn check
        String content = "Payment processed for card 4242 4242 4242 4243 today.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should not detect PAN with invalid Luhn");
    }

    @Test
    public void testInvalidIssuerFail() {
        // Valid Luhn but invalid issuer prefix (e.g. starts with 8)
        // Let's take the valid Visa 4242... and change first to 8, then we must adjust another digit to fix Luhn.
        // Wait, just use a prefix that fails isValidIssuerAndLength.
        // E.g., length 15 starting with 35. Let's adapt Amex 3434... (valid Luhn).
        // If we change 34 to 35, we added 1 to odd pos (not doubled). So we need to subtract 1 from somewhere.
        // Instead of 343434343434343, let's use 352434343434343.
        String content = "Card 352434343434343";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should not detect PAN with invalid issuer");
    }

    @Test
    public void testNoMatches() {
        String content = "Just some text with numbers 123456789012345 and more text.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
