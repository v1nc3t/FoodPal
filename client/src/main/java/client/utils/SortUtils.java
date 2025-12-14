package client.utils;

import client.services.RecipeManager;
import commons.Language;
import commons.Recipe;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    private List<Language> languageFilters;
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
    public List<Language> getLanguageFilters() {
        return languageFilters;
    }

    /**
     * Sets the language filters.
     * @param languageFilters provided language filters
     */
    public void setLanguageFilters(List<Language> languageFilters) {
        this.languageFilters = languageFilters;
    }

    /**
     * Adds a language to the list of language filters.
     * @param languageFilter filter language to be added
     */
    public void addLanguageFilter(Language languageFilter) {
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
            languageFilters = new ArrayList<>(List.of(Language.EN, Language.DE, Language.NL));
        } finally {
            sortMethod = "Alphabetical";
        }
    }

    /**
     * Sorts and filters the recipes from ObservableList by creating a SortedList.
     * @return filtered SortedList with a set comparator
     */
    public SortedList<Recipe> applyFilters() {
        List<Recipe> filteredRecipes = recipeManager.getObservableRecipes()
                .parallelStream()
                .filter(recipe -> languageFilters.contains(recipe.getLanguage()))
                .toList();
        ObservableList<Recipe> filteredObservableList =
                FXCollections.observableArrayList(filteredRecipes);

        SortedList<Recipe> sortedList = new SortedList<>(filteredObservableList);

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
            case "Alphabetical" -> Comparator.comparing(Recipe::getTitle,
                    String.CASE_INSENSITIVE_ORDER);
            case "Most recent" -> Comparator.comparing(Recipe::getTitle,
                    String.CASE_INSENSITIVE_ORDER).reversed();
            default -> throw new IllegalStateException("Unexpected value: " + sortMethod);
        };
    }
}
