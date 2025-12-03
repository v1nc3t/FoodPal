package client.config;

import java.util.List;
import java.util.UUID;

public class Config {
    private String serverAddress;
    private List<UUID> favoriteRecipeIDs;
    private String languagePreference;
    private List<String> languageFilters;
    private List<UUID> shoppingListIngredientIDs;

    public Config() {}

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public List<UUID> getFavoriteRecipeIDs() {
        return favoriteRecipeIDs;
    }

    public void setFavoriteRecipeIDs(List<UUID> favoriteRecipeIDs) {
        this.favoriteRecipeIDs = favoriteRecipeIDs;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public List<String> getLanguageFilters() {
        return languageFilters;
    }

    public void setLanguageFilters(List<String> languageFilters) {
        this.languageFilters = languageFilters;
    }

    public List<UUID> getShoppingListIngredientIDs() {
        return shoppingListIngredientIDs;
    }

    public void setShoppingListIngredientIDs(List<UUID> shoppingListIngredientIDs) {
        this.shoppingListIngredientIDs = shoppingListIngredientIDs;
    }
}
