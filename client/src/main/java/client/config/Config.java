package client.config;

import commons.Language;

import java.util.List;

public class Config {
    private String serverAddress;
    private List<FavoriteRecipe> favoriteRecipes;
    private String languagePreference;
    private List<Language> languageFilters;
    private List<client.shoppingList.ShoppingListItem> shoppingList;

    public Config() {
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public List<FavoriteRecipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<FavoriteRecipe> favoriteRecipeIDs) {
        this.favoriteRecipes = favoriteRecipeIDs;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public List<Language> getLanguageFilters() {
        return languageFilters;
    }

    public void setLanguageFilters(List<Language> languageFilters) {
        this.languageFilters = languageFilters;
    }

    public List<client.shoppingList.ShoppingListItem> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<client.shoppingList.ShoppingListItem> shoppingList) {
        this.shoppingList = shoppingList;
    }
}
