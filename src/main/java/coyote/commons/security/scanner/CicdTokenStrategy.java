package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies CI/CD and Webhook Tokens in a given text.
 * It scans for GitHub Personal Access Tokens, Slack Webhook URLs, and Jira API tokens
 * using regular expressions.
 */
public class CicdTokenStrategy implements DetectionStrategy {

    // GitHub token (e.g., Personal Access Token, OAuth, Server-to-server)
    private static final Pattern GITHUB_TOKEN_PATTERN = Pattern.compile("\\b(?:ghp|gho|ghu|ghs|ghr)_[a-zA-Z0-9]{36}\\b");

    // Slack Webhook URL
    private static final Pattern SLACK_WEBHOOK_PATTERN = Pattern.compile("https://hooks\\.slack\\.com/services/T[a-zA-Z0-9_]+/B[a-zA-Z0-9_]+/[a-zA-Z0-9_]+");

    // Jira API Token (Atlassian tokens often start with ATATT3, or appear near descriptive keywords)
    private static final Pattern JIRA_TOKEN_PATTERN = Pattern.compile("\\bATATT3[a-zA-Z0-9\\-_]{50,250}\\b|(?i)jira(?:_api)?_token\\s*[:=]\\s*[\"']?[A-Za-z0-9\\-_=]{24,}[\"']?");

    /**
     * Analyzes the provided file content to find CI/CD and Webhook tokens.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where tokens were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        findMatches(fileContent, GITHUB_TOKEN_PATTERN, "GitHub Token detected", issues);
        findMatches(fileContent, SLACK_WEBHOOK_PATTERN, "Slack Webhook URL detected", issues);
        findMatches(fileContent, JIRA_TOKEN_PATTERN, "Jira API Token detected", issues);

        return issues;
    }

    private void findMatches(String content, Pattern pattern, String issueNature, List<ScanIssue> issues) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), issueNature));
        }
    }
}
