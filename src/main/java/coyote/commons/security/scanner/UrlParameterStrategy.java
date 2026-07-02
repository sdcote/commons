package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that scans for sensitive data passed via URL or URI query parameters.
 * It extracts query parameters from strings containing URLs to check for sensitive keys
 * (e.g., token, apiKey, password, secret) because query strings are often logged in plaintext.
 */
public class UrlParameterStrategy implements DetectionStrategy {

    // Matches strings that look like URLs or URIs containing a query string.
    // Group 1: The query string portion after the '?' (excluding URL fragments starting with '#')
    private static final Pattern URL_WITH_QUERY_PATTERN = Pattern.compile(
            "[^\\s'\"<>\\?]+\\?([^\\s'\"<>#]+)"
    );

    // Matches the key-value pairs within a query string
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^=&]+)=([^&]+)");

    // Matches sensitive parameter keys in a query string.
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
            "(?i)^(token|api_?key|password|pass|secret|auth|access_token|jwt|session_?id|credential|client_secret|client_id)$"
    );

    /**
     * Analyzes the provided file content to find sensitive URL query parameters.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where sensitive query parameters were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        Matcher uriMatcher = URL_WITH_QUERY_PATTERN.matcher(fileContent);
        while (uriMatcher.find()) {
            String queryString = uriMatcher.group(1);
            int queryStartOffset = uriMatcher.start(1);
            
            Matcher paramMatcher = QUERY_PARAM_PATTERN.matcher(queryString);
            while (paramMatcher.find()) {
                String key = paramMatcher.group(1);
                
                if (isSensitiveKey(key)) {
                    int offset = queryStartOffset + paramMatcher.start();
                    issues.add(new ScanIssue(offset, "Sensitive URL/URI query parameter detected: " + key));
                }
            }
        }

        return issues;
    }

    private boolean isSensitiveKey(String key) {
        return SENSITIVE_KEY_PATTERN.matcher(key).matches();
    }
}
