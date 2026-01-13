package config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import client.config.Config;
import client.config.ConfigManager;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @TempDir
    Path tempDir;

    // GROUP 1: CONSTRUCTOR & INITIALIZATION LOGIC

    @Test
    void testConstructorUsesCustomPath() {
        // Test that passing a path actually uses that path
        Path customPath = tempDir.resolve("custom.json");
        ConfigManager manager = new ConfigManager(customPath.toString());

        manager.save();

        assertTrue(Files.exists(customPath), "Should create file at the custom path provided");
    }

    @Test
    void testLoadFromCustomPathExistingFile() throws IOException {
        Path customPath = tempDir.resolve("existing_custom.json");
        String json = "{ \"serverAddress\": \"http://custom-server.com\" }";
        Files.writeString(customPath, json);

        ConfigManager manager = new ConfigManager(customPath.toString());
        manager.load();

        assertEquals("http://custom-server.com", manager.getConfig().getServerAddress(),
                "Should load the correct server address from the existing custom path");
    }

    @Test
    void testSaveToCustomPathUpdatesFile() throws IOException {
        Path customPath = tempDir.resolve("save_custom.json");
        ConfigManager manager = new ConfigManager(customPath.toString());
        manager.load();

        Config cfg = manager.getConfig();
        cfg.setServerAddress("http://updated-custom.com");
        manager.save();

        String content = Files.readString(customPath);
        assertTrue(content.contains("http://updated-custom.com"),
                "The file at the custom path should contain the updated server address");
    }

    @Test
    void testLoadThrowsExceptionIfCustomPathIsDirectory() throws IOException {
        Path customDirPath = tempDir.resolve("invalid_dir");
        Files.createDirectories(customDirPath);

        ConfigManager manager = new ConfigManager(customDirPath.toString());

        // This should fail because it can't write/read to a directory as a file
        assertThrows(RuntimeException.class, manager::load,
                "Should throw RuntimeException if the custom path is a directory");
    }

    @Test
    void testConstructorWithNullCreatesDefaultDirectory() {
        // Test the "if (customCfgPath != null)" ELSE branch
        // We mock user.home to prevent writing to your actual disk
        String originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toString());

        try {
            ConfigManager manager = new ConfigManager(null);
            manager.load(); // Trigger file creation

            Path expectedDir = tempDir.resolve(".foodpal");
            assertTrue(Files.exists(expectedDir), "Should create .foodpal directory in user home");
        } finally {
            System.setProperty("user.home", originalHome);
        }
    }

    @Test
    void testConstructorWithNullSkipsCreationIfDirectoryExists() throws IOException {
        // Test the "if (!Files.exists(dirPath))" check when it returns FALSE
        String originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toString());

        // Pre-create the directory
        Path expectedDir = tempDir.resolve(".foodpal");
        Files.createDirectories(expectedDir);

        try {
            // Act: Constructor should NOT throw exception and NOT try to recreate dir
            assertDoesNotThrow(() -> new ConfigManager(null));
        } finally {
            System.setProperty("user.home", originalHome);
        }
    }

    @Test
    void testConstructorThrowsExceptionOnDirectoryCreationFailure() throws IOException {
        String originalHome = System.getProperty("user.home");

        // 1. Create a plain FILE to act as the fake "User Home"
        Path fileAsHome = tempDir.resolve("fakeHomeFile");
        Files.createFile(fileAsHome);

        // 2. Set the user.home property to point to this FILE
        System.setProperty("user.home", fileAsHome.toAbsolutePath().toString());

        try {
            /*
             * WHY THIS WORKS:
             * 1. ConfigManager calculates path: ".../fakeHomeFile/.foodpal"
             * 2. Files.exists(...) returns FALSE (because the folder .foodpal doesn't exist
             * yet).
             * 3. It enters the IF block.
             * 4. Files.createDirectories(...) attempts to create the folder.
             * 5. The OS throws IOException because "fakeHomeFile" is a file, not a
             * directory.
             */
            RuntimeException ex = assertThrows(RuntimeException.class, () -> new ConfigManager(null));
            assertTrue(ex.getMessage().contains("Failed to create config directory"));
        } finally {
            // 3. Cleanup
            System.setProperty("user.home", originalHome);
        }
    }

    // GROUP 2: LOADING LOGIC

    @Test
    void testLoadCreatesDefaultConfigIfFileMissing() {
        // Test "if (!Files.exists(configPath))" inside load()
        Path jsonPath = tempDir.resolve("missing.json");
        ConfigManager manager = new ConfigManager(jsonPath.toString());

        manager.load();

        assertTrue(Files.exists(jsonPath), "File should be created if it didn't exist");
        assertEquals("http://localhost:8080", manager.getConfig().getServerAddress());
    }

    @Test
    void testLoadReadsExistingFileCorrectly() throws IOException {
        // Test normal loading behavior
        Path jsonPath = tempDir.resolve("config.json");
        String json = "{ \"serverAddress\": \"http://real-server.com\" }";
        Files.writeString(jsonPath, json);

        ConfigManager manager = new ConfigManager(jsonPath.toString());
        manager.load();

        assertEquals("http://real-server.com", manager.getConfig().getServerAddress());
    }

    @Test
    void testLoadRecoversFromCorruptJson() throws IOException {
        // Test the "catch (Exception e)" inside load()
        Path jsonPath = tempDir.resolve("corrupt.json");
        Files.writeString(jsonPath, "{ BROKEN_JSON }");

        ConfigManager manager = new ConfigManager(jsonPath.toString());

        // This should trigger the catch block, which calls createDefaultConfig()
        manager.load();

        assertEquals("http://localhost:8080", manager.getConfig().getServerAddress(), "Should reset to defaults");
    }

    @Test
    void testCreateDefaultConfigThrowsExceptionIfWriteFails() throws IOException {
        // Test the "catch" inside createDefaultConfig
        // We trigger this by calling load() on a path where the PARENT directory is
        // actually a file

        Path blockingFile = tempDir.resolve("blocking-file");
        Files.createFile(blockingFile);

        // Try to put config INSIDE the file: "blocking-file/config.json"
        Path invalidPath = blockingFile.resolve("config.json");

        ConfigManager manager = new ConfigManager(invalidPath.toString());

        RuntimeException ex = assertThrows(RuntimeException.class, manager::load);
        assertTrue(ex.getMessage().contains("Failed to create default config"));
    }

    // GROUP 3: SAVING LOGIC

    @Test
    void testSavePersistsChanges() {
        Path jsonPath = tempDir.resolve("save_test.json");
        ConfigManager manager = new ConfigManager(jsonPath.toString());
        manager.load(); // Init defaults

        Config cfg = manager.getConfig();
        cfg.setLanguagePreference("fr");

        manager.save();

        // Verify by reading a new instance
        ConfigManager newManager = new ConfigManager(jsonPath.toString());
        newManager.load();
        assertEquals("fr", newManager.getConfig().getLanguagePreference());
    }

    @Test
    void testSaveThrowsExceptionOnIOFailure() throws IOException {
        // Test the "catch" inside save()
        Path jsonPath = tempDir.resolve("config.json");
        ConfigManager manager = new ConfigManager(jsonPath.toString());
        manager.load();

        // Trick: Turn the file into a directory so writeString fails
        Files.delete(jsonPath);
        Files.createDirectory(jsonPath);

        RuntimeException ex = assertThrows(RuntimeException.class, manager::save);
        assertTrue(ex.getMessage().contains("Failed to save config"));
    }

    // GROUP 4: CONFIG OBJECT & ACCESSORS

    @Test
    void testSetConfigManually() {
        ConfigManager manager = new ConfigManager(tempDir.resolve("test.json").toString());
        Config newConfig = new Config();

        manager.setConfig(newConfig);

        assertSame(newConfig, manager.getConfig());
    }

    @Test
    void testDefaultListsAreMutable() {
        // Ensures we didn't use Collections.emptyList() which causes crashes later
        ConfigManager manager = new ConfigManager(tempDir.resolve("mutable.json").toString());
        manager.load();

        assertDoesNotThrow(() -> {
            manager.getConfig().getFavoriteRecipeIDs().add(UUID.randomUUID());
        }, "Default lists should be modifiable (ArrayList), not immutable");
    }
}