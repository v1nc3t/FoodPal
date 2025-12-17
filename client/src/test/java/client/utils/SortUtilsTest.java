package client.utils;

import client.services.RecipeManager;
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

        sample = new Recipe(title1, new ArrayList<>(), preparations, servingSize);
        sample1 = new Recipe(title2, new ArrayList<>(), preparations, servingSize);
        sample2 = new Recipe(title3, new ArrayList<>(), preparations, servingSize);
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
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertNotNull(sortUtils, "Newly initialized SortUtils should not be null.");
    }

    @Test
    void getLanguageFilters() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<String> expected = new ArrayList<>(List.of("en", "de", "nl"));
        List<String> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match.");
    }

    @Test
    void setLanguageFilters() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<String> expected = new ArrayList<>(List.of("lt"));

        sortUtils.setLanguageFilters(expected);
        List<String> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after setting language filters.");
    }

    @Test
    void addLanguageFilter() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        List<String> expected = new ArrayList<>(List.of("en", "de", "nl", "lt"));

        sortUtils.addLanguageFilter("lt");
        List<String> actual = sortUtils.getLanguageFilters();

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual, "Language filters should match after adding one language filter.");
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
    void loadConfig() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        sortUtils.setSortMethod("Reverse alphabetical");
        sortUtils.setLanguageFilters(List.of("lt"));
        sortUtils.loadConfig();

        List<String> expected = new ArrayList<>(List.of("en", "de", "nl"));

        assertEquals("Alphabetical", sortUtils.getSortMethod(), "loadConfig() should have Alphabetical sorting manner by default.");
        assertEquals(expected, sortUtils.getLanguageFilters(), "loadConfig() should resort to default language filters");
    }

    @Test
    void applyFilters() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        ObservableList<String> sampleList = FXCollections.observableArrayList(sample.getTitle(), sample1.getTitle(), sample2.getTitle());
        SortedList<String> expected = new SortedList<>(sampleList);

        expected.setComparator(String::compareTo);
        SortedList<String> actual = sortUtils.applyFilters();

        assertEquals(expected, actual, "Expected a different SortedList after applyFilters().");
    }

    @Test
    void getComparator() {
        SortUtils sortUtils = SortUtils.fromRecipeList(recipeManager.getObservableRecipes());
        assertEquals("Alphabetical", sortUtils.getSortMethod());

        Comparator<String> comparator = String::compareTo;

        List<String> expected = new ArrayList<>(List.of(sample.getTitle(), sample1.getTitle(), sample2.getTitle()));
        List<String> actual = new ArrayList<>(List.of(sample.getTitle(), sample1.getTitle(), sample2.getTitle()));
        expected.sort(comparator);
        actual.sort(sortUtils.getComparator());

        assertEquals(expected, actual, "Comparators should match.");
    }
}