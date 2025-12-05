package client.utils;

import client.services.RecipeManager;
import commons.Recipe;
import jakarta.inject.Inject;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    private List<String> languageFilters;
    private String sortMethod;
    private final RecipeManager recipeManager;

    /**
     * Instantiates SortUtils with a given RecipeManager
     * @param recipeManager provided RecipeManager
     */
    @Inject
    public SortUtils(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
        loadConfig();
    }

    /**
     * Gets the language filters.
     * @return list of language filters
     */
    public List<String> getLanguageFilters() {
        return languageFilters;
    }

    /**
     * Sets the language filters.
     * @param languageFilters provided language filters
     */
    public void setLanguageFilters(List<String> languageFilters) {
        this.languageFilters = languageFilters;
    }

    /**
     * Adds a language to the list of language filters.
     * @param languageFilter filter language to be added
     */
    public void addLanguageFilter(String languageFilter) {
        this.languageFilters.add(languageFilter);
    }

    /**
     * Returns the sort method of SortUtils.
     * @return ordering manner
     */
    public String getSortMethod() {
        return sortMethod;
    }

    /**
     * Sets the ordering manner for SortUtils
     * @param sortMethod ordering manner
     */
    public void setSortMethod(String sortMethod) {
        this.sortMethod = sortMethod;
    }

    /**
     * Loads the user config and defines what filters and ordering
     * manners SortUtils should use. Reverts to default ones in case reading fails.
     */
    public void loadConfig() {
        try {
            // TODO: try to load config for user defined filters (languages and favorites)
            throw new Exception("Mock config file failing.");
        } catch (Exception e) {
            languageFilters = new ArrayList<>(List.of("en", "de", "nl"));
        } finally {
            sortMethod = "Alphabetical";
        }
    }

    /**
     * Sorts and filters the recipes from ObservableList by creating a SortedList.
     * @return filtered SortedList with a set comparator
     */
    public SortedList<Recipe> applyFilters() {
        SortedList<Recipe> sortedList = new SortedList<>(recipeManager.getObservableRecipes());

        Comparator<Recipe> recipeComparator = getComparator();
        sortedList.setComparator(recipeComparator);

        return sortedList;
    }

    /**
     * Determines and returns the comparator of the respective ordering manner.
     * @return Comparator for sorting Recipes
     */
    public Comparator<Recipe> getComparator() {
        return switch (sortMethod) {
            case "Alphabetical" -> Comparator.comparing(Recipe::getTitle);
            case "Most recent" -> Comparator.comparing(Recipe::getTitle).reversed();
            default -> throw new IllegalStateException("Unexpected value: " + sortMethod);
        };
    }
}
