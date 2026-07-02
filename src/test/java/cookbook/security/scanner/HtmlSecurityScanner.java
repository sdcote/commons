package cookbook.security.scanner;

import coyote.commons.security.scanner.PasswordStrategy;
import coyote.commons.security.scanner.ScanIssue;
import coyote.commons.security.scanner.Scanner;
import coyote.commons.security.scanner.SsnStrategy;

import java.util.List;

public class HtmlSecurityScanner {
    public static void main(String[] args) {
        // Sample HTML content containing exposure risks
        String mockHtmlContent = "<html>\n" +
                "<body>\n" +
                "  <form>\n" +
                "    <input type=\"hidden\" id=\"adminPwd\" value=\"SuperSecret123\">\n" +
                "    <p>Staff SSN record: 000-12-3456</p>\n" +
                "  </form>\n" +
                "</body>\n" +
                "</html>";

        // Initialize engine
        Scanner scanner = new Scanner();

        // Register the modular rules
        scanner.addStrategy(new SsnStrategy());
        scanner.addStrategy(new PasswordStrategy());

        // Execute scan
        List<ScanIssue> report = scanner.scan(mockHtmlContent);

        // Process results
        for (ScanIssue issue : report) {
            System.out.println("Line/Offset character position: " + issue.getOffset());
            System.out.println("Nature of vulnerability: " + issue.getNatureOfIssue());
            System.out.println("---");
        }
    }
}