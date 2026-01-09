package client.utils;

import client.scenes.ListObject;
import commons.Language;
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
    private List<Language> languageFilters;
    private String sortMethod;
    private final ObservableList<ListObject> list;

    /**
     * Instantiates SortUtils with a given ObservableList of String
     * @param list the ObservableList the utils will use
     */
    @Inject
    public SortUtils(ObservableList<ListObject> list) {
        loadDefault();
        this.list = list;
    }

    private static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    /**
     * Instantiates SortUtils with a given ObservableList of Recipe
     * Creates a derived ObservableList of ListObject that the SortUtils will use
     * @param list the ObservableList the utils will sort from
     */
    public static SortUtils fromRecipeList(ObservableList<Recipe> list) {
        ObservableList<ListObject> derivedList= FXCollections.observableArrayList();
        CountDownLatch latch = new CountDownLatch(1);
        runOnFx(() ->
                derivedList.addAll(
                        list
                            .stream()
                            .map(ListObject::fromRecipe).toList()
                )
        );
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
                derivedList.addAll(
                        list
                            .stream()
                            .map(ListObject::fromRecipe).toList()
                );
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
     * Toggles the language filter of the provided language.
     * This means that a filter is added if it doesn't exist yet
     * and is removed if it does exist.
     * @param language provided language to toggle filter of
     */
    public void toggleLanguageFilter(Language language) {
        if (languageFilters.contains(language)) {
            languageFilters.remove(language);
        }
        else {
            languageFilters.add(language);
        }
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
     * Reverts to default filtering (all languages) and sorting (alphabetical) manners.
     */
    public void loadDefault() {
        languageFilters = new ArrayList<>(List.of(Language.EN, Language.DE, Language.NL));
        sortMethod = "Alphabetical";
    }

    /**
     * Sorts and filters the recipes from the ObservableList
     * of ListObjects by creating a SortedList.
     * @return filtered SortedList with a set comparator
     */
    public SortedList<ListObject> applyFilters() {
        SortedList<ListObject> sortedList = new SortedList<>(list
                .filtered(listObject ->
                            listObject.language().isPresent()
                            && languageFilters.contains(listObject.language().get())));

        Comparator<ListObject> recipeComparator = getComparator();
        sortedList.setComparator(recipeComparator);

        return sortedList;
    }

    /**
     * Determines and returns the comparator of the respective ordering manner.
     * @return Comparator for sorting Recipes
     */
    public Comparator<ListObject> getComparator() {
        return switch (sortMethod) {
            case "Alphabetical" ->
                Comparator.comparing(
                        ListObject::name,
                        String.CASE_INSENSITIVE_ORDER
                );
            case "Most recent" ->
                Comparator.comparing(
                        ListObject::name,
                        String.CASE_INSENSITIVE_ORDER.reversed()
                );
            default -> throw new IllegalStateException("Unexpected value: " + sortMethod);
        };
    }
}
