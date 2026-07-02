package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CryptographicKeyStrategy}.
 */
public class CryptographicKeyStrategyTest {

    private final CryptographicKeyStrategy strategy = new CryptographicKeyStrategy();

    @Test
    public void testRsaPrivateKeyDetection() {
        String content = "Here is a key:\n-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEA...\n-----END RSA PRIVATE KEY-----\nDone.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Cryptographic Private Key detected", issue.getNatureOfIssue());
        assertEquals(15, issue.getOffset()); // starts at "-----BEGIN RSA PRIVATE KEY-----"
    }

    @Test
    public void testOpenSshPrivateKeyDetection() {
        String content = "export KEY=\"-----BEGIN OPENSSH PRIVATE KEY-----\nb3BlbnNzaC1rZXktdjEAAAA...\n-----END OPENSSH PRIVATE KEY-----\"";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Cryptographic Private Key detected", issue.getNatureOfIssue());
        assertEquals(12, issue.getOffset());
    }

    @Test
    public void testGenericPrivateKeyDetection() {
        String content = "config: \n  key: |\n    -----BEGIN PRIVATE KEY-----\n    MIIEvQIBADANBgkqhkiG9w0BAQEFAASC...\n    -----END PRIVATE KEY-----";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Cryptographic Private Key detected", issues.get(0).getNatureOfIssue());
        assertEquals(22, issues.get(0).getOffset());
    }

    @Test
    public void testEncryptedPrivateKeyDetection() {
        String content = "-----BEGIN ENCRYPTED PRIVATE KEY-----\nMIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkq...\n-----END ENCRYPTED PRIVATE KEY-----";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Cryptographic Private Key detected", issues.get(0).getNatureOfIssue());
        assertEquals(0, issues.get(0).getOffset());
    }

    @Test
    public void testPgpPrivateKeyBlockDetection() {
        String content = "Some email content...\n-----BEGIN PGP PRIVATE KEY BLOCK-----\nVersion: GnuPG v2\n...\n-----END PGP PRIVATE KEY BLOCK-----";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Cryptographic Private Key detected", issues.get(0).getNatureOfIssue());
        assertEquals(22, issues.get(0).getOffset());
    }

    @Test
    public void testNoMatches() {
        String content = "This is a clean file. It has a public key:\n-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkq...\n-----END PUBLIC KEY-----";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
