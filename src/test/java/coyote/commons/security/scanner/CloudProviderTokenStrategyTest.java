package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CloudProviderTokenStrategy}.
 */
public class CloudProviderTokenStrategyTest {

    private final CloudProviderTokenStrategy strategy = new CloudProviderTokenStrategy();

    @Test
    public void testAwsAccessKeyDetection() {
        String content = "Here is a config file\naws_access_key_id=AKIAIOSFODNN7EXAMPLE\nother_config=value";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("AWS Access Key ID detected", issue.getNatureOfIssue());
        // AKIA is at index 40 in the string
        assertEquals(40, issue.getOffset());
    }

    @Test
    public void testAwsSecretKeyDetection() {
        String content = "export AWS_SECRET_ACCESS_KEY=\"wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\"";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("AWS Secret Access Key detected", issue.getNatureOfIssue());
        assertEquals(7, issue.getOffset()); // starts at "AWS_SECRET_ACCESS_KEY"
    }

    @Test
    public void testGcpServiceAccountDetection() {
        String content = "{\n  \"type\": \"service_account\",\n  \"project_id\": \"my-project\",\n  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\n...\"\n}";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(2, issues.size());
        assertEquals("GCP Service Account key detected", issues.get(0).getNatureOfIssue());
        assertEquals("GCP Service Account key detected", issues.get(1).getNatureOfIssue());
    }

    @Test
    public void testAzureConnectionStringDetection() {
        String content = "Connection setting: DefaultEndpointsProtocol=https;AccountName=myaccount;AccountKey=MyAccountKey1234567890+/=;";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Azure Connection String detected", issues.get(0).getNatureOfIssue());
        assertEquals(20, issues.get(0).getOffset()); // starts at "DefaultEndpointsProtocol"
    }

    @Test
    public void testNoMatches() {
        String content = "This is a clean file without any sensitive cloud provider tokens.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
