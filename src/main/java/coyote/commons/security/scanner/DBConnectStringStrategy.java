package coyote.commons.security.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that identifies Database Connection Strings with embedded passwords.
 * It scans for JDBC, ODBC, and URI-based connection strings (e.g., MongoDB, Redis, AMQP)
 * that contain credentials, using regular expressions.
 */
public class DBConnectStringStrategy implements DetectionStrategy {

    // Matches URI-style credentials: protocol://username:password@
    private static final Pattern URI_CREDENTIALS_PATTERN = Pattern.compile("(?i)(?:mongodb(?:\\+srv)?|jdbc:[a-z0-9]+|amqp|redis|postgres(?:ql)?|mysql)://[^:/\\s\"'\n\r]+:[^@\\s\"'\n\r]+@");

    // Matches JDBC query parameter passwords: jdbc:mysql://...?password=xyz
    private static final Pattern JDBC_PASSWORD_PATTERN = Pattern.compile("(?i)jdbc:[a-z0-9]+://[^\\s\"'\n\r]+(?:password|pwd)\\s*=\\s*[^;\\s\\n\\r\"'&]+");

    // Matches ODBC connection strings: Driver={...};...Pwd=xyz
    private static final Pattern ODBC_PASSWORD_PATTERN = Pattern.compile("(?i)(?:Driver|Provider)\\s*=\\s*\\{[^}]+\\}[^\\n\r\"']*(?:password|pwd)\\s*=\\s*[^;\\s\\n\\r\"']+");

    /**
     * Analyzes the provided file content to find database connection strings with embedded passwords.
     *
     * @param fileContent the content of the file to be analyzed
     * @return a list of {@link ScanIssue} objects where connection strings were found
     */
    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty()) {
            return issues;
        }

        findMatches(fileContent, URI_CREDENTIALS_PATTERN, "Database Connection String (URI with credentials) detected", issues);
        findMatches(fileContent, JDBC_PASSWORD_PATTERN, "Database Connection String (JDBC with password) detected", issues);
        findMatches(fileContent, ODBC_PASSWORD_PATTERN, "Database Connection String (ODBC with password) detected", issues);

        return issues;
    }

    private void findMatches(String content, Pattern pattern, String issueNature, List<ScanIssue> issues) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            issues.add(new ScanIssue(matcher.start(), issueNature));
        }
    }
}
