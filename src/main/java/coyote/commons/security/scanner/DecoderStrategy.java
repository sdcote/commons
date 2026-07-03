package coyote.commons.security.scanner;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A detection strategy that targets blocks of text matching common encoding signatures
 * (such as Base64, Hex/Binary arrays, URL encoding, or HTML entities).
 * The strategy decodes the block and recursively submits the resulting plaintext 
 * back into the scanner. This catches secrets hidden inside strings like 
 * YWRtaW46cGFzc3dvcmQxMjM= (Base64 for admin:password123).
 */
public class DecoderStrategy implements DetectionStrategy {

    private Scanner scanner;

    // Pattern for Base64 (at least 12 characters, properly padded)
    private static final Pattern BASE64_PATTERN = Pattern.compile("(?<![A-Za-z0-9+/=])(?:[A-Za-z0-9+/]{4}){3,}(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?(?![A-Za-z0-9+/=])");

    // Pattern for URL encoding (captures a word containing at least one %XX)
    private static final Pattern URL_ENCODED_PATTERN = Pattern.compile("[^\\s\"']*%(?:[0-9A-Fa-f]{2})[^\\s\"']*");

    // Pattern for Hex arrays (continuous hex string, at least 16 characters)
    private static final Pattern HEX_PATTERN = Pattern.compile("(?<![A-Fa-f0-9])[A-Fa-f0-9]{16,}(?![A-Fa-f0-9])");

    // Pattern for HTML entities block (word containing an HTML entity)
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("[^\\s\"']*(?:&[a-zA-Z]+;|&#[0-9]+;|&#x[0-9A-Fa-f]+;)[^\\s\"']*");

    private static final ThreadLocal<Integer> depthCounter = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_DEPTH = 5;

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public List<ScanIssue> analyze(String fileContent) {
        List<ScanIssue> issues = new ArrayList<>();
        if (fileContent == null || fileContent.isEmpty() || scanner == null) {
            return issues;
        }

        if (depthCounter.get() > MAX_DEPTH) {
            return issues;
        }

        try {
            depthCounter.set(depthCounter.get() + 1);

            // Find and decode Base64
            Matcher b64Matcher = BASE64_PATTERN.matcher(fileContent);
            while (b64Matcher.find()) {
                String match = b64Matcher.group();
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(match);
                    String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
                    if (!decoded.equals(match) && isPrintable(decoded)) {
                        List<ScanIssue> nestedIssues = scanner.scan(decoded);
                        for (ScanIssue issue : nestedIssues) {
                            issues.add(new ScanIssue(b64Matcher.start(), "Nested issue in Base64 block: " + issue.getNatureOfIssue()));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // Not valid base64, skip
                }
            }

            // Find and decode URL Encoded
            Matcher urlMatcher = URL_ENCODED_PATTERN.matcher(fileContent);
            while (urlMatcher.find()) {
                String match = urlMatcher.group();
                try {
                    String decoded = URLDecoder.decode(match, StandardCharsets.UTF_8.name());
                    if (!decoded.equals(match) && isPrintable(decoded)) {
                        List<ScanIssue> nestedIssues = scanner.scan(decoded);
                        for (ScanIssue issue : nestedIssues) {
                            issues.add(new ScanIssue(urlMatcher.start(), "Nested issue in URL-encoded block: " + issue.getNatureOfIssue()));
                        }
                    }
                } catch (Exception e) {
                    // skip
                }
            }

            // Find and decode Hex
            Matcher hexMatcher = HEX_PATTERN.matcher(fileContent);
            while (hexMatcher.find()) {
                String match = hexMatcher.group();
                try {
                    if (match.length() % 2 == 0) {
                        byte[] decodedBytes = new byte[match.length() / 2];
                        for (int i = 0; i < decodedBytes.length; i++) {
                            int index = i * 2;
                            int v = Integer.parseInt(match.substring(index, index + 2), 16);
                            decodedBytes[i] = (byte) v;
                        }
                        String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
                        if (!decoded.equals(match) && isPrintable(decoded)) {
                            List<ScanIssue> nestedIssues = scanner.scan(decoded);
                            for (ScanIssue issue : nestedIssues) {
                                issues.add(new ScanIssue(hexMatcher.start(), "Nested issue in Hex-encoded block: " + issue.getNatureOfIssue()));
                            }
                        }
                    }
                } catch (Exception e) {
                    // skip
                }
            }
            
            // Find and decode HTML Entities
            Matcher htmlMatcher = HTML_ENTITY_PATTERN.matcher(fileContent);
            while (htmlMatcher.find()) {
                String match = htmlMatcher.group();
                try {
                    String decoded = decodeHtmlEntities(match);
                    if (!decoded.equals(match) && isPrintable(decoded)) {
                        List<ScanIssue> nestedIssues = scanner.scan(decoded);
                        for (ScanIssue issue : nestedIssues) {
                            issues.add(new ScanIssue(htmlMatcher.start(), "Nested issue in HTML-encoded block: " + issue.getNatureOfIssue()));
                        }
                    }
                } catch (Exception e) {
                    // skip
                }
            }
            
        } finally {
            depthCounter.set(depthCounter.get() - 1);
        }

        return issues;
    }

    private boolean isPrintable(String s) {
        int printable = 0;
        for (char c : s.toCharArray()) {
            if (c >= 32 && c <= 126 || c == '\n' || c == '\r' || c == '\t') {
                printable++;
            }
        }
        return s.length() > 0 && ((double) printable / s.length()) >= 0.7;
    }

    private String decodeHtmlEntities(String s) {
        String decoded = s.replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&nbsp;", " ")
                .replace("&#39;", "'");
        
        Matcher m = Pattern.compile("&#([0-9]+);").matcher(decoded);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            try {
                int codePoint = Integer.parseInt(m.group(1));
                m.appendReplacement(sb, new String(Character.toChars(codePoint)));
            } catch (Exception e) {
                m.appendReplacement(sb, m.group());
            }
        }
        m.appendTail(sb);
        decoded = sb.toString();

        Matcher mx = Pattern.compile("&#x([0-9a-fA-F]+);").matcher(decoded);
        StringBuffer sbx = new StringBuffer();
        while (mx.find()) {
            try {
                int codePoint = Integer.parseInt(mx.group(1), 16);
                mx.appendReplacement(sbx, new String(Character.toChars(codePoint)));
            } catch (Exception e) {
                mx.appendReplacement(sbx, mx.group());
            }
        }
        mx.appendTail(sbx);
        
        return sbx.toString();
    }
}
