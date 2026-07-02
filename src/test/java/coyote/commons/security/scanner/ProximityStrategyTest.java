package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ProximityStrategy}.
 */
public class ProximityStrategyTest {

    private final ProximityStrategy strategy = new ProximityStrategy();

    @Test
    public void testHighEntropyNearAnchor() {
        String content = "Authorization: Bearer aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5A6bC7dE8fG9hI0jK1lM2nO3pQ=";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("High-entropy token found near anchor keyword", issues.get(0).getNatureOfIssue());
        assertEquals(22, issues.get(0).getOffset());
    }

    @Test
    public void testHighEntropyNotNearAnchor() {
        // Distance from "secret" to the token should be exactly > 50.
        // "secret" is 6 chars. Token starts at 6 + 51 = 57
        StringBuilder content = new StringBuilder("secret");
        for (int i = 0; i < 51; i++) {
            content.append(" ");
        }
        content.append("aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5A6bC7dE8fG9hI0jK1lM2nO3pQ=");
        List<ScanIssue> issues = strategy.analyze(content.toString());
        
        assertTrue(issues.isEmpty(), "Should not detect high entropy string if no anchor is nearby");
    }

    @Test
    public void testLowEntropyNearAnchor() {
        String content = "Secret value: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should ignore low entropy strings even if near anchor");
    }

    @Test
    public void testDifferentAnchors() {
        String[] anchors = {"SECRET", "token", "Bearer", "Identity", "SALT"};
        String hex = "b4c9e8f1a2d3b5c7e9f0a1d2b3c4e5f6";
        
        for (String anchor : anchors) {
            String content = anchor + " -> " + hex;
            List<ScanIssue> issues = strategy.analyze(content);
            assertEquals(1, issues.size(), "Should detect hex string near anchor: " + anchor);
        }
    }

    @Test
    public void testDistanceThreshold() {
        String hex = "b4c9e8f1a2d3b5c7e9f0a1d2b3c4e5f6";
        
        // exactly 50 chars away
        StringBuilder sb = new StringBuilder("secret");
        for (int i = 0; i < 50; i++) {
            sb.append(" ");
        }
        sb.append(hex);
        
        List<ScanIssue> issues = strategy.analyze(sb.toString());
        assertEquals(1, issues.size(), "Should detect when exactly at max window size");
        
        // 51 chars away
        sb = new StringBuilder("secret");
        for (int i = 0; i < 51; i++) {
            sb.append(" ");
        }
        sb.append(hex);
        
        issues = strategy.analyze(sb.toString());
        assertTrue(issues.isEmpty(), "Should not detect when outside window size");
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
