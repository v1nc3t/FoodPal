package client.utils;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        recipeManager = RecipeManager.getInstance();
        String title1 = "Test Pancakes";
        String title2 = "A pizza";
        String title3 = "X pizza";
        List<String> preparations = List.of("Mix flour, eggs and milk", "Fry on medium heat");
        int servingSize = 2;
        languageEN = Language.EN;
        languageDE = Language.DE;
        languageNL = Language.NL;

        sample = new Recipe(title1, new ArrayList<>(), preparations, servingSize, languageEN);
        sample1 = new Recipe(title2, new ArrayList<>(), preparations, servingSize, languageDE);
        sample2 = new Recipe(title3, new ArrayList<>(), preparations, servingSize, languageNL);
        recipeManager.addRecipeOptimistic(sample);
        recipeManager.addRecipeOptimistic(sample1);
        recipeManager.addRecipeOptimistic(sample2);
    }

    @AfterEach
    void afterEach() {
        recipeManager.clearForTests();
    }

    @Test
    void constructorTest() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        assertNotNull(sortUtils, "Newly initialized SortUtils should not be null.");
    }

    @Test
    void getLanguageFilters() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        List<Language> expected = new ArrayList<>(List.of(Language.EN, Language.DE,
                Language.NL));
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match.");
    }

    @Test
    void setLanguageFilters() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        List<Language> expected = new ArrayList<>(List.of(Language.DE));

        sortUtils.setLanguageFilters(expected);
        List<Language> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after setting language filters.");
    }

    @Test
    void addLanguageFilter() {
        SortUtils sortUtils = new SortUtils(recipeManager);
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
    void getSortMethod() {
        SortUtils sortUtils = new SortUtils(recipeManager);

        assertEquals("Alphabetical", sortUtils.getSortMethod(), "Expected a different sort method.");
    }

    @Test
    void setSortMethod() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        sortUtils.setSortMethod("Reverse alphabetical");

        assertEquals("Reverse alphabetical", sortUtils.getSortMethod(), "Expected a different sort method afer setting one.");
    }

    @Test
    void loadConfig() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        sortUtils.setSortMethod("Reverse alphabetical");
        sortUtils.setLanguageFilters(List.of(Language.NL));
        sortUtils.loadConfig();

        List<Language> expected = new ArrayList<>(List.of(Language.EN, Language.DE,
                Language.NL));

        assertEquals("Alphabetical", sortUtils.getSortMethod(), "loadConfig() should have Alphabetical sorting manner by default.");
        assertEquals(expected, sortUtils.getLanguageFilters(), "loadConfig() should resort to default language filters");
    }

    @Test
    void applyFiltersSortTest() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        ObservableList<Recipe> sampleList = FXCollections.observableArrayList(sample, sample1, sample2);
        SortedList<Recipe> expected = new SortedList<>(sampleList);

        expected.setComparator(Comparator.comparing(Recipe::getTitle));
        SortedList<Recipe> actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void applyFiltersLanguageTest() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        sortUtils.setLanguageFilters(List.of(Language.DE, Language.NL));
        assertEquals(List.of(Language.DE, Language.NL), sortUtils.getLanguageFilters());

        ObservableList<Recipe> expectedSampleList = FXCollections.observableArrayList(sample1, sample2);
        SortedList<Recipe> expected = new SortedList<>(expectedSampleList);

        expected.setComparator(Comparator.comparing(Recipe::getTitle));
        SortedList<Recipe> actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void getComparator() {
        SortUtils sortUtils = new SortUtils(recipeManager);
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        Comparator<Recipe> comparator = Comparator.comparing(Recipe::getTitle);

        List<Recipe> expected = new ArrayList<>(List.of(sample, sample1, sample2));
        List<Recipe> actual = new ArrayList<>(List.of(sample2, sample1, sample));
        expected.sort(comparator);
        actual.sort(sortUtils.getComparator());

        assertEquals(expected, actual, "Comparators should match.");
    }
}