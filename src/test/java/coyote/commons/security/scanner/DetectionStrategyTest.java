package coyote.commons.security.scanner;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link DetectionStrategy} interface default methods,
 * especially {@link DetectionStrategy#extractContext(String, int)}.
 */
public class DetectionStrategyTest {

    private final DetectionStrategy strategy = new DetectionStrategy() {
        @Override
        public List<ScanIssue> analyze(String fileContent) {
            return Collections.emptyList();
        }
    };

    @Test
    public void testExtractContextNullAndEmpty() {
        assertEquals("", strategy.extractContext(null, 0));
        assertEquals("", strategy.extractContext(null, 10));
        assertEquals("", strategy.extractContext("", 0));
        assertEquals("", strategy.extractContext("", 5));
    }

    @Test
    public void testExtractContextAtBeginning() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // offset at 0: should take from 0 to min(length, 20)
        String context = strategy.extractContext(text, 0);
        assertEquals("0123456789ABCDEFGHIJ", context);
        assertEquals(20, context.length());
    }

    @Test
    public void testExtractContextNearBeginning() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        // offset at 10: start should be max(0, 10-20)=0, end should be 10+20=30
        String context = strategy.extractContext(text, 10);
        assertEquals("0123456789ABCDEFGHIJKLMNOPQRST", context);
        assertEquals(30, context.length());
    }

    @Test
    public void testExtractContextMidString() {
        String text = "01234567890123456789TARGET01234567890123456789";
        int offset = 20; // 'T' of TARGET
        // start = 20 - 20 = 0, end = 20 + 20 = 40
        String context = strategy.extractContext(text, offset);
        assertEquals(40, context.length());
        assertEquals("01234567890123456789TARGET01234567890123", context);
    }

    @Test
    public void testExtractContextNearEnd() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // length is 36. offset at 30: start = max(0, 30-20) = 10, end = min(36, 30+20) = 36
        // substring from 10 to 36 is 26 characters
        String context = strategy.extractContext(text, 30);
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", context);
        assertEquals(26, context.length());
    }

    @Test
    public void testExtractContextAtEnd() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // length is 36. offset at 36: start = max(0, 36-20) = 16, end = min(36, 36+20) = 36
        String context = strategy.extractContext(text, 36);
        assertEquals("GHIJKLMNOPQRSTUVWXYZ", context);
        assertEquals(20, context.length());
    }

    @Test
    public void testExtractContextShortString() {
        String text = "short";
        String context = strategy.extractContext(text, 2);
        assertEquals("short", context);
    }

    @Test
    public void testExtractContextNegativeOffset() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String context = strategy.extractContext(text, -5);
        // Clamped offset 0 -> substring(0, 20)
        assertEquals("0123456789ABCDEFGHIJ", context);
    }

    @Test
    public void testExtractContextOffsetBeyondLength() {
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String context = strategy.extractContext(text, 100);
        // Clamped offset 36 -> substring(16, 36)
        assertEquals("GHIJKLMNOPQRSTUVWXYZ", context);
    }
}
