package config;

import commons.Language;
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
    List<Language> filters;
    List<client.shoppingList.ShoppingListItem> items;
    client.shoppingList.ShoppingListItem item1 = new client.shoppingList.ShoppingListItem(UUID.randomUUID(),
            new commons.Amount(1, "kg"));
    client.shoppingList.ShoppingListItem item2 = new client.shoppingList.ShoppingListItem(UUID.randomUUID(),
            new commons.Amount(2, "l"));

    @BeforeEach
    void setUp() {
        config = new Config();
        recipeIds = Arrays.asList(recId, recId2);
        config.setFavoriteRecipeIDs(recipeIds);
        server = "http://localhost:8080";
        config.setServerAddress(server);
        lang = "en";
        config.setLanguagePreference(lang);
        filters = Arrays.asList(Language.EN, Language.DE);
        config.setLanguageFilters(filters);
        items = Arrays.asList(item1, item2);
        config.setShoppingList(items);
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
        List<Language> newFilters = Arrays.asList(Language.DE, Language.NL);
        config.setLanguageFilters(newFilters);
        assertEquals(newFilters, config.getLanguageFilters());
    }

    @Test
    void getShoppingListTest() {
        assertEquals(items, config.getShoppingList());
    }

    @Test
    void setShoppingListTest() {
        client.shoppingList.ShoppingListItem item3 = new client.shoppingList.ShoppingListItem(UUID.randomUUID(),
                new commons.Amount(5, "kg"));
        List<client.shoppingList.ShoppingListItem> newItems = Arrays.asList(item1, item2, item3);
        config.setShoppingList(newItems);
        assertEquals(newItems, config.getShoppingList());
    }
}