package coyote.commons.minivault.util;

import coyote.commons.minivault.Entries;
import coyote.commons.minivault.Entry;
import coyote.commons.minivault.MiniVault;
import coyote.commons.minivault.json.JSONArray;
import coyote.commons.minivault.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonMarshaller {

  /**
   * Read bytes from the input stream and marshal them onto entries.
   *
   * <p>Data is assumed to be a UTF-8 string representing valid JSON.</p>
   *
   * @param inputStream
   * @return
   * @throws IOException
   */
  public Entries read(InputStream inputStream) throws IOException {
    Entries retval = new Entries();

    byte[] bytes = readBytes(inputStream);
    JSONObject json = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
    JSONArray array = json.getJSONArray(MiniVault.ENTRIES_TAG);

    for (int i = 0; i < array.length(); i++) {
      JSONObject item = array.getJSONObject(i);
      if (item != null) {
        Entry entry = new Entry();
        if (item.has(MiniVault.NAME_TAG)) entry.setName(item.getString(MiniVault.NAME_TAG));
        if (item.has(MiniVault.NOTES_TAG)) entry.setNotes(item.getString(MiniVault.NOTES_TAG));
        if (item.has(MiniVault.PASSWORD_TAG)) entry.setPassword(item.getString(MiniVault.PASSWORD_TAG));
        if (item.has(MiniVault.PRIVATE_KEY_TAG)) entry.setPrivatekey(item.getString(MiniVault.PRIVATE_KEY_TAG));
        if (item.has(MiniVault.PUBLIC_KEY_TAG)) entry.setPublickey(item.getString(MiniVault.PUBLIC_KEY_TAG));
        if (item.has(MiniVault.PASSPHRASE_TAG)) entry.setPublickey(item.getString(MiniVault.PASSPHRASE_TAG));
        if (item.has(MiniVault.URL_TAG)) entry.setUrl(item.getString(MiniVault.URL_TAG));
        if (item.has(MiniVault.USER_TAG)) entry.setUsername(item.getString(MiniVault.USER_TAG));
        if (item.has(MiniVault.EMAIL_TAG)) entry.setEmail(item.getString(MiniVault.EMAIL_TAG));
        if (item.has(MiniVault.TOKEN_TAG)) entry.setToken(item.getString(MiniVault.TOKEN_TAG));
        retval.getEntry().add(entry);
      }
    }
    return retval;
  }

  /**
   * Write the given entries out to the output stream as UTF-8 JSON encoded string.
   *
   * @param entries
   * @param outputStream
   * @throws IOException
   */
  public void write(Entries entries, OutputStream outputStream) throws IOException {
    outputStream.write(new JSONObject(entries).toString().getBytes(StandardCharsets.UTF_8));
  }


  /**
   * Reads the input stream to the end of file.
   *
   * @param inputStream the input stream to read
   * @return the bytes read in from that stream
   * @throws IOException if there were problems reading the given input stream
   */
  private byte[] readBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len;
    while ((len = inputStream.read(buffer)) != -1) {
      os.write(buffer, 0, len);
    }
    return os.toByteArray();
  }

}
