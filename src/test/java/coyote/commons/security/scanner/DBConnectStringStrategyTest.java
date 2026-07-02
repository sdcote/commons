package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DBConnectStringStrategy}.
 */
public class DBConnectStringStrategyTest {

    private final DBConnectStringStrategy strategy = new DBConnectStringStrategy();

    @Test
    public void testMongoDbUriDetection() {
        String content = "Connecting to db:\nmongoUri=mongodb+srv://admin:SuperSecret123@cluster0.mongodb.net/test\n";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Database Connection String (URI with credentials) detected", issue.getNatureOfIssue());
        assertEquals(27, issue.getOffset()); // starts at "mongodb+srv://..."
    }

    @Test
    public void testJdbcUriDetection() {
        String content = "export DB_URL=\"jdbc:postgresql://dbuser:dbpass@localhost:5432/mydb\"";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Database Connection String (URI with credentials) detected", issue.getNatureOfIssue());
        assertEquals(15, issue.getOffset());
    }

    @Test
    public void testJdbcQueryParamDetection() {
        String content = "jdbc.url=jdbc:mysql://localhost:3306/db?user=root&password=mypassword123&useSSL=false";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Database Connection String (JDBC with password) detected", issues.get(0).getNatureOfIssue());
        assertEquals(9, issues.get(0).getOffset());
    }

    @Test
    public void testOdbcDetection() {
        String content = "connection=Driver={SQL Server};Server=myServerAddress;Database=myDataBase;Uid=myUsername;Pwd=myPassword;";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertEquals(1, issues.size());
        assertEquals("Database Connection String (ODBC with password) detected", issues.get(0).getNatureOfIssue());
        assertEquals(11, issues.get(0).getOffset()); // starts at "Driver="
    }

    @Test
    public void testNoMatches() {
        String content = "jdbc:mysql://localhost:3306/db?user=root&useSSL=false\nDriver={SQL Server};Server=myServerAddress;Trusted_Connection=yes;";
        List<ScanIssue> issues = strategy.analyze(content);
        
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }
}
