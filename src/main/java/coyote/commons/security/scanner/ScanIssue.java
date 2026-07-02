package coyote.commons.security.scanner;

/**
 * Represents a security issue or vulnerability found during a scan.
 * It contains the character offset where the issue was found and a description of its nature.
 */
public class ScanIssue {
    private final int offset;
    private final String natureOfIssue;

    /**
     * Constructs a new {@code ScanIssue} with the specified offset and nature of the issue.
     *
     * @param offset        the character position in the content where the issue starts
     * @param natureOfIssue a description of the type of security issue found
     */
    public ScanIssue(int offset, String natureOfIssue) {
        this.offset = offset;
        this.natureOfIssue = natureOfIssue;
    }

    /**
     * Gets the character offset where this issue was found in the content.
     *
     * @return the character offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the description of the nature of this issue.
     *
     * @return the nature of the issue
     */
    public String getNatureOfIssue() {
        return natureOfIssue;
    }

    @Override
    public String toString() {
        return "Issue Found [Offset: " + offset + ", Nature: " + natureOfIssue + "]";
    }
}
