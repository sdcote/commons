package coyote.commons.minivault.util;

/**
 * String utility class.
 */
public final class StringUtil {

    private StringUtil() {
        // utility class
    }


    /**
     * This method ensures that the output String has only valid XML unicode characters as specified by the XML 1.0
     * standard.
     * <p>For reference, please see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This
     * method will return an empty String if the input is null or empty.</p>
     * <p>WARNING: This can result in data loss. It is therefore recommended to find another way to represent data in
     * XML and JSON. It might be better to at lease warn the user when they enter "invalid" characters.</p>
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String stripNonValidXMLCharacters(final String in) {
        if (in == null || in.isEmpty()) {
            return in;
        }
        StringBuilder out = new StringBuilder();
        char current;
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            } else {
                out.append('?');
            }
        }
        return out.toString();
    }


    public static String stripString(String text) {
        return stripString(text, 80);
    }


    public static String stripString(String text, int length) {
        String result = text;
        if (text != null && text.length() > length) {
            result = text.substring(0, length) + "...";
        }
        return result;
    }


    public static String byteArrayToHex(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /**
     * Checks if a string is null, empty ("") or only whitespace.
     *
     * @param str the String to check, may be null
     * @return {@code true} if the argument is empty or null or only whitespace
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if a string is not null, empty ("") and not only whitespace.
     *
     * <p>This is a convenience wrapper around isBlank(String) to make code
     * slightly more readable.</p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null and not
     * whitespace
     * @see #isBlank(String)
     */
    public static boolean isNotBlank(String str) {
        return !StringUtil.isBlank(str);
    }


    /**
     * Determine if the supplied String is empty (i.e., null or consisting only of whitespace).
     *
     * @param str the string to check; may be null
     * @return true if the string is empty
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }


    /**
     * Determine if the supplied String is not blank.
     *
     * @param str the string to check; may be null
     * @return true if the string is not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * Count the occurrences of the substring in string s.
     *
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

}
