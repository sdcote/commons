package coyote.commons.minivault;

import coyote.commons.minivault.json.JSONPropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Java class for anonymous complex type.
 */
public class Entries {

  protected List<Entry> entry;

  /**
   * Gets the value of the entry property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
   * make to the returned list will be present inside the object. This is why there is not a {@code set} method for
   * the entry property.</p>
   *
   * <p> For example, to add a new item, do as follows:<pre>
   *    getEntry().add(newItem);
   * </pre></p>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link Entry}</p>
   *
   * @return list of {@link Entry} objects
   */
  @JSONPropertyName(MiniVault.ENTRIES_TAG)
  public List<Entry> getEntry() {
    if (entry == null) {
      entry = new ArrayList<Entry>();
    }
    return this.entry;
  }

}
