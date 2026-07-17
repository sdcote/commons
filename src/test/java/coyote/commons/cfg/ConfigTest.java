package coyote.commons.cfg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Config} class.
 */
public class ConfigTest {

    @Test
    public void testGetOrCreateSection_existing() {
        Config config = new Config();
        Config section = new Config();
        section.put("Key", "Value");
        config.put("MySection", section);

        Config retrieved = config.getOrCreateSection("MySection");
        assertNotNull(retrieved, "Retrieved section should not be null");
        assertEquals("Value", retrieved.getString("Key"), "Retrieved section should contain the original data");
    }

    @Test
    public void testGetOrCreateSection_new() {
        Config config = new Config();
        
        Config created = config.getOrCreateSection("NewSection");
        assertNotNull(created, "Created section should not be null");
        
        // Ensure it was added to the config
        Config retrieved = config.getSection("NewSection");
        assertNotNull(retrieved, "Section should have been added to the config");
    }

    @Test
    public void testGetOrCreateSection_caseInsensitive() {
        Config config = new Config();
        Config section = new Config();
        section.put("Key", "Value");
        config.put("MYSECTION", section);

        Config retrieved = config.getOrCreateSection("mysection");
        assertNotNull(retrieved, "Retrieved section should not be null when case differs");
        assertEquals("Value", retrieved.getString("Key"), "Retrieved section should contain the original data");
    }

    @Test
    public void testGetOrCreateSection_nullTag() {
        Config config = new Config();
        Config retrieved = config.getOrCreateSection(null);
        assertNotNull(retrieved, "Method should return a new Config even if tag is null");
        
        // Ensure it wasn't added with a null tag
        assertNull(config.getSection(null), "Should not be able to retrieve a section with a null tag");
        assertEquals(0, config.getElementCount(), "Config should not have any elements added if tag was null");
    }
    
    @Test
    public void testGetOrCreateSection_emptyTag() {
        Config config = new Config();
        Config retrieved = config.getOrCreateSection("");
        assertNotNull(retrieved, "Method should return a new Config even if tag is empty");
        
        // Ensure it wasn't added with an empty tag
        assertNull(config.getSection(""), "Should not be able to retrieve a section with an empty tag");
        assertEquals(0, config.getElementCount(), "Config should not have any elements added if tag was empty");
    }
}
