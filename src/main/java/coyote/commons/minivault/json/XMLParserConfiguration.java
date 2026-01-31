package coyote.commons.minivault.json;

/**
 * Configuration object for the XML parser.
 */
public class XMLParserConfiguration {
  /**
   * Original Configuration of the XML Parser.
   */
  public static final XMLParserConfiguration ORIGINAL = new XMLParserConfiguration();
  /**
   * Original configuration of the XML Parser except that values are kept as strings.
   */
  public static final XMLParserConfiguration KEEP_STRINGS = new XMLParserConfiguration(true);
  /**
   * When parsing the XML into JSON, specifies if values should be kept as strings (true), or if
   * they should try to be guessed into JSON values (numeric, boolean, string)
   */
  public final boolean keepStrings;
  /**
   * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
   * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
   * processing.
   */
  public final String cDataTagName;
  /**
   * When parsing the XML into JSON, specifies if values with attribute xsi:nil="true"
   * should be kept as attribute(false), or they should be converted to null(true)
   */
  public final boolean convertNilAttributeToNull;

  /**
   * Default parser configuration. Does not keep strings, and the CDATA Tag Name is "content".
   */
  public XMLParserConfiguration() {
    this(false, "content", false);
  }

  /**
   * Configure the parser string processing and use the default CDATA Tag Name as "content".
   *
   * @param keepStrings <code>true</code> to parse all values as string.
   *                    <code>false</code> to try and convert XML string values into a JSON value.
   */
  public XMLParserConfiguration(final boolean keepStrings) {
    this(keepStrings, "content", false);
  }

  /**
   * Configure the parser string processing to try and convert XML values to JSON values and
   * use the passed CDATA Tag Name the processing value. Pass <code>null</code> to
   * disable CDATA processing
   *
   * @param cDataTagName<code>null</code> to disable CDATA processing. Any other value
   *                                      to use that value as the JSONObject key name to process as CDATA.
   */
  public XMLParserConfiguration(final String cDataTagName) {
    this(false, cDataTagName, false);
  }

  /**
   * Configure the parser to use custom settings.
   *
   * @param keepStrings                   <code>true</code> to parse all values as string.
   *                                      <code>false</code> to try and convert XML string values into a JSON value.
   * @param cDataTagName<code>null</code> to disable CDATA processing. Any other value
   *                                      to use that value as the JSONObject key name to process as CDATA.
   */
  public XMLParserConfiguration(final boolean keepStrings, final String cDataTagName) {
    this.keepStrings = keepStrings;
    this.cDataTagName = cDataTagName;
    this.convertNilAttributeToNull = false;
  }

  /**
   * Configure the parser to use custom settings.
   *
   * @param keepStrings               <code>true</code> to parse all values as string.
   *                                  <code>false</code> to try and convert XML string values into a JSON value.
   * @param cDataTagName              <code>null</code> to disable CDATA processing. Any other value
   *                                  to use that value as the JSONObject key name to process as CDATA.
   * @param convertNilAttributeToNull <code>true</code> to parse values with attribute xsi:nil="true" as null.
   *                                  <code>false</code> to parse values with attribute xsi:nil="true" as {"xsi:nil":true}.
   */
  public XMLParserConfiguration(final boolean keepStrings, final String cDataTagName, final boolean convertNilAttributeToNull) {
    this.keepStrings = keepStrings;
    this.cDataTagName = cDataTagName;
    this.convertNilAttributeToNull = convertNilAttributeToNull;
  }
}
