package client.utils;

import client.scenes.ListObject;
import commons.Ingredient;
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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class SortUtils {
    private List<Language> languageFilters;
    private String sortMethod;
    private final ObservableList<ListObject> list;
    private boolean onlyFavourites = false;
    private List<UUID> favourites;

    /**
     * Instantiates SortUtils with a given ObservableList of String
     * @param list the ObservableList the utils will use
     */
    @Inject
    public SortUtils(ObservableList<ListObject> list) {
        loadDefault();
        this.list = list;
    }

    public ObservableList<ListObject> getList() {
        return this.list;
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
        return SortUtils.fromOtherList(list, ListObject::fromRecipe);
    }

    /**
     * Instantiates SortUtils with a given ObservableList of Ingredient
     * Creates a derived ObservableList of ListObject that the SortUtils will use
     * @param list the ObservableList the utils will sort from
     */
    public static SortUtils fromIngredientList(ObservableList<Ingredient> list) {
        return SortUtils.fromOtherList(list, ListObject::fromIngredient);
    }

    /**
     * Instantiates SortUtils with a given ObservableList of T
     * Creates a derived ObservableList of ListObject that the SortUtils will use
     * @param list the ObservableList the utils will sort from
     * @param mappingFunction the function that maps values from T to ListObject
     */
    public static <T> SortUtils fromOtherList(ObservableList<T> list, Function<T, ListObject> mappingFunction) {
        ObservableList<ListObject> derivedList= FXCollections.observableArrayList();
        CountDownLatch latch = new CountDownLatch(1);
        runOnFx(() ->
                derivedList.addAll(
                        list
                                .stream()
                                .map(mappingFunction).toList()
                )
        );
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.addListener((ListChangeListener<? super T>) changed -> {
            CountDownLatch latch2 = new CountDownLatch(1);
            runOnFx(() -> {
                derivedList.clear();
                derivedList.addAll(
                        list
                                .stream()
                                .map(mappingFunction).toList()
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
     * Returns whether the sortUtils is toggled to only show favourites.
     * @return true if only favourites are shown, false otherwise
     */
    public boolean isOnlyFavourites() {
        return onlyFavourites;
    }

    /**
     * Sets whether the sortUtils should only show favourites.
     * @param onlyFavourites true if only favourites should be shown, false otherwise
     */
    public void setOnlyFavourites(boolean onlyFavourites) {
        this.onlyFavourites = onlyFavourites;
    }

    /**
     * Gets the list of favourite UUIDs.
     * @return list of favourites as UUIDs
     */
    public List<UUID> getFavourites() {
        return favourites;
    }

    /**
     * Sets the list of favourite UUIDs.
     * @param favourites list of favourites as UUIDs
     */
    public void setFavourites(List<UUID> favourites) {
        this.favourites = favourites;
    }

    /**
     * Toggles the list of favourites as UUIDs.
     * This means that a UUID is added to the list if it doesn't exist yet
     * and is removed if it does exist.
     * @param id provided UUID to add or remove from favourites
     */
    public void toggleFavourite(UUID id) {
        if (favourites.contains(id)) {
            favourites.remove(id);
        } else {
            favourites.add(id);
        }
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
        onlyFavourites = false;
        favourites = new ArrayList<>();
    }

    /**
     * Sorts and filters the recipes from the ObservableList
     * of ListObjects by creating a SortedList.
     * @return filtered SortedList with a set comparator
     */
    public SortedList<ListObject> applyFilters() {
        SortedList<ListObject> sortedList = new SortedList<>(list
                .filtered(listObject ->
                        (listObject.language().isEmpty()
                            || languageFilters.contains(listObject.language().get()))
                            && (!onlyFavourites || favourites.contains(listObject.id()))
                ));

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
            case "Reverse alphabetical" ->
                Comparator.comparing(
                        ListObject::name,
                        String.CASE_INSENSITIVE_ORDER.reversed()
                );
            default -> throw new IllegalStateException("Unexpected value: " + sortMethod);
        };
    }
}
