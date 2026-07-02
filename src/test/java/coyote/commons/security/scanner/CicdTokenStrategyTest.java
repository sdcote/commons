package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CicdTokenStrategy}.
 */
public class CicdTokenStrategyTest {

    private final CicdTokenStrategy strategy = new CicdTokenStrategy();

    @Test
    public void testGitHubTokenDetection() {
        String content = "My GitHub PAT is ghp_123456789012345678901234567890123456";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("GitHub Token detected", issue.getNatureOfIssue());
        assertEquals(17, issue.getOffset());
    }

    @Test
    public void testSlackWebhookDetection() {
        String content = "Send notification to https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Slack Webhook URL detected", issue.getNatureOfIssue());
        assertEquals(21, issue.getOffset());
    }

    @Test
    public void testJiraTokenDetectionByPrefix() {
        String content = "Here is an Atlassian token: ATATT3xFfGF0b2tlbl9wYXlsb2FkX2hlcmVfd2l0aF9sb3RzX29mX2NoYXJhY3RlcnMxd2R4Y2Z2Z2hiam5rbWwsLnsdfghjklmnhbgvfdcsxawsedrftgyhujikolpPLOKIJUHYGTFRDESWAQ1234567890ZAQWSXCDERFVBGTYHNMJUIKLOP";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Jira API Token detected", issues.get(0).getNatureOfIssue());
        assertEquals(28, issues.get(0).getOffset());
    }

    @Test
    public void testJiraTokenDetectionByKeyword() {
        String content = "export JIRA_API_TOKEN=\"aBcDeFgHiJkLmNoPqRsTuVwXyZ123456\"";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Jira API Token detected", issues.get(0).getNatureOfIssue());
        assertEquals(7, issues.get(0).getOffset());
    }

    @Test
    public void testNoMatches() {
        String content = "This is a clean file without any sensitive tokens. ghp_123 is too short.";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
