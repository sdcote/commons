package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UrlParameterStrategy}.
 */
public class UrlParameterStrategyTest {

    private final UrlParameterStrategy strategy = new UrlParameterStrategy();

    @Test
    public void testFullUrlWithSensitiveParam() {
        String content = "Check out https://example.com/api/v1/resource?user=admin&token=SECURE123 in the logs.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Sensitive URL/URI query parameter detected: token", issues.get(0).getNatureOfIssue());
        
        // Assert the exact offset matches the start of "token=SECURE123"
        String expectedMatch = "token=SECURE123";
        assertEquals(expectedMatch, content.substring(issues.get(0).getOffset(), issues.get(0).getOffset() + expectedMatch.length()));
    }

    @Test
    public void testRelativeUriWithSensitiveParam() {
        String content = "Request made to /login?apiKey=abcdef123456";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Sensitive URL/URI query parameter detected: apiKey", issues.get(0).getNatureOfIssue());
        
        String expectedMatch = "apiKey=abcdef123456";
        assertEquals(expectedMatch, content.substring(issues.get(0).getOffset(), issues.get(0).getOffset() + expectedMatch.length()));
    }

    @Test
    public void testMultipleSensitiveParams() {
        String content = "URL: http://test.local?client_id=foo&client_secret=bar";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(2, issues.size());
        assertEquals("Sensitive URL/URI query parameter detected: client_id", issues.get(0).getNatureOfIssue());
        assertEquals("Sensitive URL/URI query parameter detected: client_secret", issues.get(1).getNatureOfIssue());
    }

    @Test
    public void testUrlWithFragment() {
        // Ensuring the fragment is ignored and doesn't break matching
        String content = "https://example.com/path?password=12345#section2";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Sensitive URL/URI query parameter detected: password", issues.get(0).getNatureOfIssue());
    }

    @Test
    public void testInnocentParams() {
        String content = "https://example.com/search?q=puppies&page=2";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty(), "Should not detect innocent parameters");
    }

    @Test
    public void testCaseInsensitiveKeys() {
        String content = "https://example.com/data?API_KEY=test&SessionId=abc";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(2, issues.size());
        assertEquals("Sensitive URL/URI query parameter detected: API_KEY", issues.get(0).getNatureOfIssue());
        assertEquals("Sensitive URL/URI query parameter detected: SessionId", issues.get(1).getNatureOfIssue());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
