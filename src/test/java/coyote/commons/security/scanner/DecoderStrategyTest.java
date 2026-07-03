package coyote.commons.security.scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DecoderStrategy}.
 */
public class DecoderStrategyTest {

    private Scanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = new Scanner();
        // Add a strategy that detects simple passwords (e.g. password=supersecret)
        scanner.addStrategy(new PasswordStrategy());
        // Add the decoder strategy to catch encoded secrets
        scanner.addStrategy(new DecoderStrategy());
    }

    @Test
    public void testBase64Decoding() {
        // Base64 for "password=supersecret" -> "cGFzc3dvcmQ9c3VwZXJzZWNyZXQ="
        String content = "Here is a token: cGFzc3dvcmQ9c3VwZXJzZWNyZXQ=";
        List<ScanIssue> issues = scanner.scan(content);
        
        assertFalse(issues.isEmpty(), "Should detect password inside base64 string");
        // Ensure the offset matches the start of the Base64 block
        assertEquals(17, issues.get(0).getOffset());
        assertTrue(issues.get(0).getNatureOfIssue().contains("Base64 block"));
    }

    @Test
    public void testUrlDecoding() {
        // URL encoded "password=supersecret" -> "password%3Dsupersecret"
        String content = "config: url?password%3Dsupersecret";
        List<ScanIssue> issues = scanner.scan(content);
        
        assertFalse(issues.isEmpty(), "Should detect password inside URL encoded string");
        // offset of "url?password%3Dsupersecret" is 8
        assertEquals(8, issues.get(0).getOffset());
        assertTrue(issues.get(0).getNatureOfIssue().contains("URL-encoded block"));
    }

    @Test
    public void testHexDecoding() {
        // Hex for "password=supersecret" -> "70617373776f72643d7375706572736563726574"
        String content = "hex-data: 70617373776f72643d7375706572736563726574";
        List<ScanIssue> issues = scanner.scan(content);
        
        assertFalse(issues.isEmpty(), "Should detect password inside Hex string");
        assertEquals(10, issues.get(0).getOffset());
        assertTrue(issues.get(0).getNatureOfIssue().contains("Hex-encoded block"));
    }

    @Test
    public void testHtmlDecoding() {
        // HTML encoded "password=supersecret" -> "password&#61;supersecret"
        String content = "<div>password&#61;supersecret</div>";
        List<ScanIssue> issues = scanner.scan(content);
        
        assertFalse(issues.isEmpty(), "Should detect password inside HTML encoded string");
        // It matches the whole non-whitespace string "<div>password&#61;supersecret</div>"
        assertEquals(0, issues.get(0).getOffset());
        assertTrue(issues.get(0).getNatureOfIssue().contains("HTML-encoded block"));
    }

    @Test
    public void testInfiniteRecursionPrevention() {
        // "password=supersecret"
        // L1 Base64: "cGFzc3dvcmQ9c3VwZXJzZWNyZXQ="
        // L2 Base64: "Y0dGemMzZHZjbVE5YzNWd1pYSnpaV055WlhRPQ=="
        // L3 Base64: "WTBkR2VtTXpaSFpqYlZFNVl6TldkMXBZU25wYVYwNTVXbGhSUFE9PQ=="
        
        String content = "WTBkR2VtTXpaSFpqYlZFNVl6TldkMXBZU25wYVYwNTVXbGhSUFE9PQ==";
        List<ScanIssue> issues = scanner.scan(content);
        
        assertFalse(issues.isEmpty(), "Should recursively decode multiple times to find the secret");
    }

    @Test
    public void testEmptyOrNullContent() {
        DecoderStrategy strategy = new DecoderStrategy();
        strategy.setScanner(new Scanner());
        
        assertTrue(strategy.analyze(null).isEmpty());
        assertTrue(strategy.analyze("").isEmpty());
    }
}
