package coyote.commons.dataframe.selector;


import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.PropertyFrame;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FieldSelectorTest {

    @Test
    public void testFieldSelector() {
        new FieldSelector("java.*.>");
        new FieldSelector(">");
        new FieldSelector("java");
        new FieldSelector("*.*.*.*");
        new FieldSelector("*.*.*.*.>");
    }


    @Test
    public void testSelect() {
        PropertyFrame marshaler = new PropertyFrame();
        DataFrame frame = marshaler.marshal(System.getProperties(), true);
        assertNotNull(frame);

        FieldSelector selector = new FieldSelector("java.vm.>");
        List<DataField> results = selector.select(frame);
        assertNotNull(results);
        assertTrue(results.size() > 0);

        for (DataField dframe : results) {
            System.out.println(dframe.getName());
        }

    }

}
