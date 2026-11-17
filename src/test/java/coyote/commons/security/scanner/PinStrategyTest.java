package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PinStrategy}.
 */
@ExtendWith(MockitoExtension.class)
public class PinStrategyTest {

    private final PinStrategy strategy = new PinStrategy();

    @Mock
    private Scanner mockScanner;

    @Test
    public void testPinUppercaseDetection() {
        String content = "Your card PIN is 4829 for future transactions.";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Personal Identification Number (PIN) detected", issue.getNatureOfIssue());
        assertEquals(17, issue.getOffset());
    }

    @Test
    public void testPinTitleCaseDetection() {
        String content = "Please enter your Pin: 5678.";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Personal Identification Number (PIN) detected", issue.getNatureOfIssue());
        assertEquals(23, issue.getOffset());
    }

    @Test
    public void testPinHashDetection() {
        String content = "Temporary PIN# 9012 issued to customer.";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Personal Identification Number (PIN) detected", issue.getNatureOfIssue());
        assertEquals(15, issue.getOffset());
    }

    @Test
    public void testPinHashColonDetection() {
        String content = "Account record: PIN#: 3456";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        ScanIssue issue = issues.get(0);
        assertEquals("Personal Identification Number (PIN) detected", issue.getNatureOfIssue());
        assertEquals(22, issue.getOffset());
    }

    @Test
    public void testPinHashNoSpaceDetection() {
        String content = "PIN#1234";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        assertEquals(4, issues.get(0).getOffset());
    }

    @Test
    public void testPinHashColonNoSpaceDetection() {
        String content = "PIN#:5678";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        assertEquals(5, issues.get(0).getOffset());
    }

    @Test
    public void testNumbersBeforePin() {
        String content = "The digits 8765 are your new PIN.";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        assertEquals(11, issues.get(0).getOffset());
    }

    @Test
    public void testPinCodeAndNumberVariations() {
        String content1 = "PIN code: 1122";
        List<ScanIssue> issues1 = strategy.analyze(content1);
        assertEquals(1, issues1.size());
        assertEquals(10, issues1.get(0).getOffset());

        String content2 = "pin_number = 3344";
        List<ScanIssue> issues2 = strategy.analyze(content2);
        assertEquals(1, issues2.size());
        assertEquals(13, issues2.get(0).getOffset());

        String content3 = "PIN-Code: 5566";
        List<ScanIssue> issues3 = strategy.analyze(content3);
        assertEquals(1, issues3.size());
        assertEquals(10, issues3.get(0).getOffset());
    }

    @Test
    public void testPersonalIdentificationNumberPhrase() {
        String content = "Personal Identification Number: 7788";
        List<ScanIssue> issues = strategy.analyze(content);

        assertEquals(1, issues.size());
        assertEquals(32, issues.get(0).getOffset());
    }

    @Test
    public void testFormattedPinWithHyphenAndSpace() {
        String contentHyphen4 = "PIN: 12-34";
        List<ScanIssue> issues1 = strategy.analyze(contentHyphen4);
        assertEquals(1, issues1.size());
        assertEquals(5, issues1.get(0).getOffset());

        String contentHyphen6 = "PIN: 123-456";
        List<ScanIssue> issues2 = strategy.analyze(contentHyphen6);
        assertEquals(1, issues2.size());
        assertEquals(5, issues2.get(0).getOffset());

        String contentHyphen8 = "PIN: 1234-5678";
        List<ScanIssue> issues3 = strategy.analyze(contentHyphen8);
        assertEquals(1, issues3.size());
        assertEquals(5, issues3.get(0).getOffset());

        String contentSpace = "PIN: 12 34";
        List<ScanIssue> issues4 = strategy.analyze(contentSpace);
        assertEquals(1, issues4.size());
        assertEquals(5, issues4.get(0).getOffset());
    }

    @Test
    public void testPinInJsonAndXml() {
        String json = "{\"user\": \"test\", \"pin\": \"4321\"}";
        List<ScanIssue> jsonIssues = strategy.analyze(json);
        assertEquals(1, jsonIssues.size());
        assertEquals(json.indexOf("4321"), jsonIssues.get(0).getOffset());

        String xml = "<auth><pin>9999</pin></auth>";
        List<ScanIssue> xmlIssues = strategy.analyze(xml);
        assertEquals(1, xmlIssues.size());
        assertEquals(xml.indexOf("9999"), xmlIssues.get(0).getOffset());
    }

    @Test
    public void testNonPinWordsRejected() {
        // Words containing 'pin' substring should not match as anchors
        assertTrue(strategy.analyze("Spin the wheel 1234 times").isEmpty());
        assertTrue(strategy.analyze("Buy a pint of milk 1234").isEmpty());
        assertTrue(strategy.analyze("Alpine road 1234").isEmpty());
        assertTrue(strategy.analyze("Pinpoint location 1234").isEmpty());
        assertTrue(strategy.analyze("Opinion poll 1234").isEmpty());
    }

    @Test
    public void testNumberOutsideWindowSize() {
        // Proximity window default is 50 chars
        String padding = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String content = "PIN " + padding + " 1234";
        List<ScanIssue> issues = strategy.analyze(content);
        assertTrue(issues.isEmpty(), "Candidate number outside window size should not be detected");
    }

    @Test
    public void testCustomWindowSize() {
        PinStrategy customStrategy = new PinStrategy(10);
        assertEquals(10, customStrategy.getWindowSize());

        // Distance is 15 -> should not match with windowSize=10
        String content1 = "PIN: 123456789012 1234";
        assertTrue(customStrategy.analyze(content1).isEmpty());

        // Distance is 1 -> should match
        String content2 = "PIN: 1234";
        assertEquals(1, customStrategy.analyze(content2).size());

        PinStrategy negativeWindow = new PinStrategy(-5);
        assertEquals(0, negativeWindow.getWindowSize());
    }

    @Test
    public void testInvalidNumberLengths() {
        // 3 digits is too short for PIN
        assertTrue(strategy.analyze("PIN: 123").isEmpty());
        // 9 contiguous digits (e.g. SSN or routing number) is not a 4-8 digit PIN
        assertTrue(strategy.analyze("PIN: 123456789").isEmpty());
    }

    @Test
    public void testEmptyAndNullContent() {
        assertTrue(strategy.analyze("").isEmpty());
        assertTrue(strategy.analyze(null).isEmpty());
    }

    @Test
    public void testScannerIntegration() {
        Scanner scanner = new Scanner();
        scanner.addStrategy(strategy);

        String content = "Confidential document:\nUser PIN#: 9876\nDo not share.";
        List<ScanIssue> issues = scanner.scan(content);

        assertEquals(1, issues.size());
        assertEquals("Personal Identification Number (PIN) detected", issues.get(0).getNatureOfIssue());
    }

    @Test
    public void testSetScannerWithMockito() {
        // Test setScanner default implementation
        strategy.setScanner(mockScanner);
        verifyNoInteractions(mockScanner);
    }
}
