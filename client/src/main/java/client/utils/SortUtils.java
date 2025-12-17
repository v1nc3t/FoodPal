package client.utils;

import commons.Recipe;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SortUtils {
    private List<String> languageFilters;
    private String sortMethod;
    private final ObservableList<String> list;

    /**
     * Instantiates SortUtils with a given ObservableList of String
     * @param list the ObservableList the utils will use
     */
    @Inject
    public SortUtils(ObservableList<String> list) {
        loadConfig();
        this.list = list;
    }

    private static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    /**
     * Instantiates SortUtils with a given ObservableList of Recipe
     * Creates a derived ObservableList of String that the SortUtils will use
     * @param list the ObservableList the utils will sort from
     */
    public static SortUtils fromRecipeList(ObservableList<Recipe> list) {
        ObservableList<String> derivedList= FXCollections.observableArrayList();
        CountDownLatch latch = new CountDownLatch(1);
        runOnFx(() -> derivedList.addAll(list.stream().map(Recipe::getTitle).toList()));
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.addListener((ListChangeListener<? super Recipe>) changed -> {
            CountDownLatch latch2 = new CountDownLatch(1);
            runOnFx(() -> {
                derivedList.clear();
                derivedList.addAll(changed.getList().stream().map(Recipe::getTitle).toList());
                latch2.countDown();
            });
            try {
                latch2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return new SortUtils(derivedList);
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
    public SortedList<String> applyFilters() {
        SortedList<String> sortedList = new SortedList<>(list);

        Comparator<String> recipeComparator = getComparator();
        sortedList.setComparator(recipeComparator);

        return sortedList;
    }

    /**
     * Determines and returns the comparator of the respective ordering manner.
     * @return Comparator for sorting Recipes
     */
    public Comparator<String> getComparator() {
        return switch (sortMethod) {
            case "Alphabetical" ->
                    String.CASE_INSENSITIVE_ORDER;
            case "Most recent" ->
                    String.CASE_INSENSITIVE_ORDER.reversed();
            default -> throw new IllegalStateException("Unexpected value: " + sortMethod);
        };
    }
}
