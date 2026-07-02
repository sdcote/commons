package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Cloud Provider Access Tokens in a given text.
 * It scans for AWS Access Key IDs, AWS Secret Access Keys, GCP Service Account keys,
 * and Azure Connection Strings using regular expressions.
 */
public class CloudProviderTokenStrategy implements DetectionStrategy {

    // AWS Access Key ID: 20 character alphanumeric starting with common prefixes
    private static final Pattern AWS_ACCESS_KEY_PATTERN = Pattern.compile("\\b(?:AKIA|ASIA|AROA|AIDA|AGPA|ANPA)[A-Z0-9]{16}\\b");

    // AWS Secret Access Key: ~40 character base64-like string near a descriptive keyword
    private static final Pattern AWS_SECRET_KEY_PATTERN = Pattern.compile("(?i)(?:aws_secret_access_key|aws_secret_key|secret_key)\\s*[:=]\\s*[\"']?([A-Za-z0-9/+=]{40})[\"']?");

    // GCP Service Account: identifies service account JSON structure
    private static final Pattern GCP_SA_PATTERN = Pattern.compile("(?i)\"type\"\\s*:\\s*\"service_account\"|\"private_key\"\\s*:\\s*\"-----BEGIN PRIVATE KEY-----");

    // Azure Connection String: typical format with Endpoint, SharedAccessKey, etc.
    private static final Pattern AZURE_CONNECTION_STRING_PATTERN = Pattern.compile("(?i)(?:Endpoint|DefaultEndpointsProtocol)=[^;]+;(?:SharedAccessKeyName|AccountName)=[^;]+;(?:SharedAccessKey|AccountKey)=[A-Za-z0-9+/=]+");

    /**
     * Analyzes the provided file content to find cloud provider tokens.
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

        findMatches(fileContent, AWS_ACCESS_KEY_PATTERN, "AWS Access Key ID detected", issues);
        findMatches(fileContent, AWS_SECRET_KEY_PATTERN, "AWS Secret Access Key detected", issues);
        findMatches(fileContent, GCP_SA_PATTERN, "GCP Service Account key detected", issues);
        findMatches(fileContent, AZURE_CONNECTION_STRING_PATTERN, "Azure Connection String detected", issues);

        return issues;
    }

    private void findMatches(String content, Pattern pattern, String issueNature, List<ScanIssue> issues) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), issueNature));
        }
    }
}
