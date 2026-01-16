package client.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import commons.Language;

public class ConfigManager {

    // Default config directory and file name
    // It creates a ".foodpal" directory in the user's home folder
    private static final String CONFIG_FILE = "config.json";

    private final ObjectMapper mapper;
    private final Path configPath;

    private Config config;

    public ConfigManager(String customCfgPath) {
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        if (customCfgPath != null) {
            this.configPath = Path.of(customCfgPath);
        } else {
            String userHome = System.getProperty("user.home");
            Path dirPath = Path.of(userHome, ".foodpal");

            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectories(dirPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create config directory", e);
                }
            }
            this.configPath = dirPath.resolve(CONFIG_FILE);
        }
    }

    public void load() {
        try {
            if (!Files.exists(configPath)) {
                createDefaultConfig();
            }

            String json = Files.readString(configPath, StandardCharsets.UTF_8);
            this.config = mapper.readValue(json, Config.class);
        } catch (Exception e) {
            createDefaultConfig(); // If loading fails, create a default config
            // throw new RuntimeException("Failed to load config: " + e.getMessage(), e);
        }
    }

    private void createDefaultConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            this.config = createDefaultConfigObject();
            save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default config: " + e.getMessage(), e);
        }
    }

    public void save() {
        try {
            String json = mapper.writeValueAsString(this.config);
            Files.writeString(configPath, json, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save config: " + e.getMessage(), e);
        }
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    private Config createDefaultConfigObject() {
        Config defaultConfig = new Config();
        defaultConfig.setServerAddress("http://localhost:8080/");
        defaultConfig.setFavoriteRecipeIDs(java.util.Collections.emptyList());
        defaultConfig.setLanguagePreference("en");
        defaultConfig.setLanguageFilters(List.of(Language.EN, Language.DE, Language.NL));
        defaultConfig.setShoppingList(java.util.Collections.emptyList());
        return defaultConfig;
    }
}