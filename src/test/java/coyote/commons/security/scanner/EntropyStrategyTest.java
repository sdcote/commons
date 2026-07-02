package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EntropyStrategy}.
 */
public class EntropyStrategyTest {

    private final EntropyStrategy strategy = new EntropyStrategy();

    @Test
    public void testHighEntropyHex() {
        // High entropy hex string (32 characters, well distributed)
        String hex = "b4c9e8f1a2d3b5c7e9f0a1d2b3c4e5f6";
        String content = "privateKey = '" + hex + "';";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size(), "Should detect high entropy hex string");
        assertEquals("High entropy string detected (possible key/secret)", issues.get(0).getNatureOfIssue());
        assertEquals(14, issues.get(0).getOffset());
    }

    @Test
    public void testLowEntropyHex() {
        // Low entropy hex string (repeating characters)
        String hex = "00000000000000000000000000000000";
        String content = "zeros = '" + hex + "';";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should ignore low entropy hex strings");
    }

    @Test
    public void testHighEntropyBase64() {
        // High entropy base64 string
        String randomB64 = "aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5A6bC7dE8fG9hI0jK1lM2nO3pQ=";
        String content = "secret_key: \"" + randomB64 + "\",";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size(), "Should detect high entropy base64 string");
        assertEquals(13, issues.get(0).getOffset());
    }

    @Test
    public void testLowEntropyBase64() {
        // Low entropy base64 (repeating characters)
        String b64 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String content = "value: '" + b64 + "'";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should ignore low entropy base64 strings");
    }

    @Test
    public void testShortStringIgnored() {
        // Less than 16 chars should be ignored even if it's high entropy
        String shortB64 = "aB3cD4eF5gH6iJ7";
        String content = "secret = '" + shortB64 + "'";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should ignore strings shorter than 16 characters");
    }

    @Test
    public void testNoAssignmentIgnored() {
        // High entropy string not near an assignment operator
        String randomB64 = "aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5A6bC7dE8fG9hI0jK1lM2nO3pQ=";
        String content = "Random text containing " + randomB64 + " without assignment.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should ignore high entropy strings without an assignment operator");
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
