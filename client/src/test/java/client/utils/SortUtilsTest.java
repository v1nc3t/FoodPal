package client.utils;

import client.scenes.ListObject;
import client.services.RecipeManager;
import commons.Language;
import commons.Recipe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class SortUtilsTest {

    private RecipeManager recipeManager;
    private Recipe sample;
    private Recipe sample1;
    private Recipe sample2;
    private Language languageEN;
    private Language languageDE;
    private Language languageNL;

    @Start
    public void start(Stage stage) {
        stage.show();
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void beforeEach() {
        recipeManager = new RecipeManager();
        recipeManager.clearForTests();
        String title1 = "Test Pancakes";
        String title2 = "A pizza";
        String title3 = "X pizza";
        List<String> preparations = List.of("Mix flour, eggs and milk", "Fry on medium heat");
        int portions = 2;
        languageEN = Language.EN;
        languageDE = Language.DE;
        languageNL = Language.NL;

        sample = new Recipe(title1, new ArrayList<>(), preparations, portions, languageEN);
        sample1 = new Recipe(title2, new ArrayList<>(), preparations, portions, languageDE);
        sample2 = new Recipe(title3, new ArrayList<>(), preparations, portions, languageNL);
        recipeManager.addRecipeOptimistic(sample);
        recipeManager.addRecipeOptimistic(sample1);
        recipeManager.addRecipeOptimistic(sample2);
    }

    @Test
    void constructorTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertNotNull(sortUtils, "Newly initialized SortUtils should not be null.");
    }

    @Test
    void isOnlyFavouritesTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertFalse(sortUtils.isOnlyFavourites(), "By default, SortUtils should not only show favourites.");
    }

    @Test
    void setOnlyFavouritesTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertFalse(sortUtils.isOnlyFavourites(), "By default, SortUtils should not only show favourites.");
        sortUtils.setOnlyFavourites(true);
        assertTrue(sortUtils.isOnlyFavourites(), "After setting onlyFavourites to true, SortUtils should only show favourites.");
    }

    @Test
    void getFavouritesTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertEquals(0, sortUtils.getFavourites().size(), "By default, SortUtils should not have any favourites.");
    }

    @Test
    void setFavouritesTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<UUID> favourites = List.of(sample.getId(), sample1.getId());
        sortUtils.setFavourites(favourites);
        assertEquals(favourites, sortUtils.getFavourites(), "After setting favourites, SortUtils should have the same favourites.");
    }

    @Test
    void toggleFavouriteTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        UUID id = sample.getId();
        sortUtils.toggleFavourite(id);
        assertTrue(sortUtils.getFavourites().contains(id),
                "After toggling a favourite that was previously not there, SortUtils should have the recipe UUID in the list of favourites.");
    }

    @Test
    void getLanguageFilters() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<Language> expected = new ArrayList<>(List.of(Language.EN, Language.DE,
                Language.NL));
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match.");
    }

    @Test
    void setLanguageFilters() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<Language> expected = new ArrayList<>(List.of(Language.DE));

        sortUtils.setLanguageFilters(expected);
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after setting language filters.");
    }

    @Test
    void addLanguageFilter() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<Language> expected = new ArrayList<>(List.of(Language.DE,
                Language.NL));

        List<Language> actual = new ArrayList<>(List.of(Language.DE));
        sortUtils.setLanguageFilters(actual);

        sortUtils.addLanguageFilter(Language.NL);
        actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after adding one language filter.");
    }

    @Test
    void toggleRemoveLanguageFilter() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<Language> expected = new ArrayList<>(List.of(Language.DE,
                Language.NL));

        sortUtils.toggleLanguageFilter(Language.EN);
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after toggling (here, removing) one language filter.");
    }

    @Test
    void toggleAddLanguageFilter() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<Language> expected = new ArrayList<>(List.of(Language.DE));

        sortUtils.setLanguageFilters(expected);
        expected.add(Language.NL);

        sortUtils.toggleLanguageFilter(Language.NL);
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after toggling one language filter.");
    }

    @Test
    void getSortMethod() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());

        assertEquals("Alphabetical", sortUtils.getSortMethod(), "Expected a different sort method.");
    }

    @Test
    void setSortMethod() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        sortUtils.setSortMethod("Reverse alphabetical");

        assertEquals("Reverse alphabetical", sortUtils.getSortMethod(), "Expected a different sort method afer setting one.");
    }

    @Test
    void loadDefault() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        sortUtils.setSortMethod("Reverse alphabetical");
        sortUtils.setLanguageFilters(List.of(Language.NL));
        sortUtils.setOnlyFavourites(true);
        sortUtils.toggleFavourite(UUID.randomUUID());
        sortUtils.loadDefault();

        List<Language> expected = new ArrayList<>(List.of(Language.EN, Language.DE,
                Language.NL));

        assertEquals("Alphabetical", sortUtils.getSortMethod(), "loadDefault() should have Alphabetical sorting manner by default.");
        assertEquals(expected, sortUtils.getLanguageFilters(), "loadDefault() should resort to default language filters");
        assertFalse(sortUtils.isOnlyFavourites(), "loadDefault() should not only show favourites by default.");
        assertEquals(0, sortUtils.getFavourites().size(), "loadDefault() should not have any favourites by default.");
    }

    @Test
    void applyFiltersSortTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        ObservableList<ListObject> sampleList = FXCollections.observableArrayList(
                Stream.of(sample, sample1, sample2).map(ListObject::fromRecipe).toList()
        );
        var expected = new SortedList<>(sampleList);

        expected.setComparator(Comparator.comparing(ListObject::name));
        var actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void applyFiltersLanguageTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        sortUtils.setLanguageFilters(List.of(Language.DE, Language.NL));
        assertEquals(List.of(Language.DE, Language.NL), sortUtils.getLanguageFilters());

        ObservableList<ListObject> expectedSampleList = FXCollections.observableArrayList(
                Stream.of(sample1, sample2).map(ListObject::fromRecipe).toList()
        );
        var expected = new SortedList<>(expectedSampleList);

        expected.setComparator(Comparator.comparing(ListObject::name));
        var actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void applyFiltersFavouritesTest() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<UUID> favourites = List.of(sample.getId(), sample1.getId());

        ObservableList<ListObject> expectedSampleList = FXCollections.observableArrayList(
                Stream.of(sample, sample1).map(ListObject::fromRecipe).toList()
        );
        var expected = new SortedList<>(expectedSampleList);

        expected.setComparator(Comparator.comparing(ListObject::name));

        sortUtils.setFavourites(favourites);
        sortUtils.setOnlyFavourites(true);
        var actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void getComparator() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        Comparator<ListObject> comparator = Comparator.comparing(ListObject::name);

        var expected = new ArrayList<>(Stream.of(sample, sample1, sample2).map(ListObject::fromRecipe).toList());
        var actual = new ArrayList<>(Stream.of(sample, sample1, sample2).map(ListObject::fromRecipe).toList());
        expected.sort(comparator);
        actual.sort(sortUtils.getComparator());

        assertEquals(expected, actual, "Comparators should match.");
    }
}