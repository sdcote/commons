/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.cfg;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import coyote.commons.dataframe.DataFrameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ConfigUtil} class.
 * Ensures that configuration reading and writing functionality works as expected.
 */
public class ConfigUtilTest {

    @Test
    public void testWriteAndReadConfig(@TempDir Path tempDir) throws IOException, DataFrameException {
        File configFile = tempDir.resolve("test_config.json").toFile();
        String filename = configFile.getAbsolutePath();

        Config originalConfig = new Config();
        originalConfig.put("key1", "value1");
        originalConfig.put("key2", 42);

        // Test writing config
        ConfigUtil.writeConfig(originalConfig, filename);
        assertTrue(configFile.exists());
        assertTrue(configFile.length() > 0);

        // Test reading config
        Config readConfig = ConfigUtil.readConfig(filename);
        assertNotNull(readConfig);
        assertEquals("value1", readConfig.getString("key1"));
        assertEquals(42, readConfig.getAsInt("key2"));
    }

    @Test
    public void testReadConfigMissingFile(@TempDir Path tempDir) {
        File missingFile = tempDir.resolve("missing_config.json").toFile();
        String filename = missingFile.getAbsolutePath();

        // Test reading missing config returns empty config
        Config readConfig = ConfigUtil.readConfig(filename);
        assertNotNull(readConfig);
        assertTrue(readConfig.isEmpty());
    }

    @Test
    public void testReadConfigCorruptedFile(@TempDir Path tempDir) throws IOException {
        File corruptedFile = tempDir.resolve("corrupted_config.json").toFile();
        String filename = corruptedFile.getAbsolutePath();

        // Write invalid JSON content
        coyote.commons.FileUtil.stringToFile("{ corrupted json data : ", filename);

        // Test reading corrupted config returns empty config
        Config readConfig = ConfigUtil.readConfig(filename);
        assertNotNull(readConfig);
        assertTrue(readConfig.isEmpty());
    }

    @Test
    public void testWriteConfigNullConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigUtil.writeConfig(null, "some_file.json");
        });
    }

    @Test
    public void testWriteConfigNullOrEmptyFilename() {
        Config cfg = new Config();
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigUtil.writeConfig(cfg, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigUtil.writeConfig(cfg, "  ");
        });
    }

    @Test
    public void testWriteConfigFailure() {
        Config cfg = new Config();
        cfg.put("test", "data");
        
        // Mock static FileUtil to simulate write failure
        try (MockedStatic<coyote.commons.FileUtil> fileUtilMock = Mockito.mockStatic(coyote.commons.FileUtil.class)) {
            fileUtilMock.when(() -> coyote.commons.FileUtil.stringToFile(Mockito.anyString(), Mockito.anyString()))
                        .thenReturn(false);
            
            assertThrows(IOException.class, () -> {
                ConfigUtil.writeConfig(cfg, "dummy.json");
            });
        }
    }
}
