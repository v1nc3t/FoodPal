package config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import client.config.Config;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    Config config;
    String server;
    List<UUID> recipeIds;
    UUID recId = UUID.randomUUID();
    UUID recId2 = UUID.randomUUID();
    String lang;
    List<String> filters;
    List<UUID> ingredientIds;
    UUID ingredientId = UUID.randomUUID();
    UUID ingredientId2 = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        config = new Config();
        recipeIds = Arrays.asList(recId, recId2);
        config.setFavoriteRecipeIDs(recipeIds);
        server = "http://localhost:8080";
        config.setServerAddress(server);
        lang = "en";
        config.setLanguagePreference(lang);
        filters = Arrays.asList("en", "fr");
        config.setLanguageFilters(filters);
        ingredientIds = Arrays.asList(ingredientId, ingredientId2);
        config.setShoppingListIngredientIDs(ingredientIds);
    }

    @Test
    void testDefaultConstructor() {
        Config config1 = new Config();   
        assertNotNull(config1);
    }

    @Test
    void getServerAddressTest() {
        assertEquals(server, config.getServerAddress());
    }

    @Test
    void setServerAddressTest() {
        String server = "http://example.com";
        config.setServerAddress(server);
        assertEquals(server, config.getServerAddress());
    }

    @Test
    void getFavoriteRecipeIDsTest() {
        assertEquals(recipeIds, config.getFavoriteRecipeIDs());
    }

    @Test
    void setFavoriteRecipeIDsTest() {
        UUID recId3 = UUID.randomUUID();
        List<UUID> newRecipeIds = Arrays.asList(recId, recId2, recId3);
        config.setFavoriteRecipeIDs(newRecipeIds);
        assertEquals(newRecipeIds, config.getFavoriteRecipeIDs());
    }

    @Test
    void getLanguagePreferenceTest() {
        assertEquals(lang, config.getLanguagePreference());
    }

    @Test
    void setLanguagePreferenceTest() {
        String newLang = "fr";
        config.setLanguagePreference(newLang);
        assertEquals(newLang, config.getLanguagePreference());
    }

    @Test
    void getLanguageFiltersTest() {
        assertEquals(filters, config.getLanguageFilters());
    }  

    @Test
    void setLanguageFiltersTest() {
        List<String> newFilters = Arrays.asList("es", "de");
        config.setLanguageFilters(newFilters);
        assertEquals(newFilters, config.getLanguageFilters());
    }

    @Test
    void getShoppingListIngredientIDsTest() {
        assertEquals(ingredientIds, config.getShoppingListIngredientIDs());
    }

    @Test
    void setShoppingListIngredientIDsTest() {
        UUID ingredientId3 = UUID.randomUUID();
        List<UUID> newIngredientIds = Arrays.asList(ingredientId, ingredientId2, ingredientId3);
        config.setShoppingListIngredientIDs(newIngredientIds);
        assertEquals(newIngredientIds, config.getShoppingListIngredientIDs());
    }
}