package commons;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCaloriesTest {

    @Test
    public void calculateTotalCaloriesTest() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 1, 1) // 17 kcal / 100 g
        );
        Ingredient ingredient1 = new Ingredient(
                "Test1",
                new NutritionValues(30, 10, 50) // 410 kcal / 100 g
        );

        UUID id = ingredient.getId();
        UUID id1 = ingredient1.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM) // 1.7 kcal
        );

        RecipeIngredient ri1 = new RecipeIngredient(
                id1,
                new Amount(100, Unit.GRAM) // 410 kcal
        );

        HashMap<UUID, Ingredient> ingredients = new HashMap<>();
        ingredients.put(id, ingredient);
        ingredients.put(id1, ingredient1);


        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri, ri1),
                List.of("step"),
                2,
                Language.EN
        );

        double kcal = recipe.calcTotalCalories(ingredients::get);
        assertEquals(411.7, kcal, 0.00001,
                "Expected a different amount of total calories for a recipe.");
    }

    @Test
    public void caloriesPerPortionIsCalculatedCorrectly() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 1, 1) // 17 kcal / 100 g
        );

        UUID id = ingredient.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM) // 1.7 kcal
        );

        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri),
                List.of("step"),
                2,
                Language.EN
        );

        double kcal = recipe.calcCaloriesPerPortion(uuid -> ingredient);
        assertEquals(0.85, kcal, 0.00001);


    }

    @Test
    public void calculate100gCaloriesTest() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 1, 1) // 17 kcal / 100 g
        );
        Ingredient ingredient1 = new Ingredient(
                "Test1",
                new NutritionValues(30, 10, 50) // 410 kcal / 100 g
        );

        UUID id = ingredient.getId();
        UUID id1 = ingredient1.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM)
        );

        RecipeIngredient ri1 = new RecipeIngredient(
                id1,
                new Amount(100, Unit.GRAM)
        );

        HashMap<UUID, Ingredient> ingredients = new HashMap<>();
        ingredients.put(id, ingredient);
        ingredients.put(id1, ingredient1);


        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri, ri1),
                List.of("step"),
                2,
                Language.EN
        );

        double kcal = recipe.calcKcalPer100g(ingredients::get);
        double expected = (411.7 / 110) * 100;
        assertEquals(expected, kcal, 0.00001,
                "Expected a different amount of calories per 100 g for a recipe.");
    }

    @Test
    public void calculateTotalProteinTest() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(20, 1, 1)
        );
        Ingredient ingredient1 = new Ingredient(
                "Test1",
                new NutritionValues(30, 10, 50)
        );

        UUID id = ingredient.getId();
        UUID id1 = ingredient1.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM)
        );

        RecipeIngredient ri1 = new RecipeIngredient(
                id1,
                new Amount(100, Unit.GRAM)
        );

        HashMap<UUID, Ingredient> ingredients = new HashMap<>();
        ingredients.put(id, ingredient);
        ingredients.put(id1, ingredient1);


        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri, ri1),
                List.of("step"),
                2,
                Language.EN
        );

        double protein = recipe.calcTotalProtein(ingredients::get);
        assertEquals(32.0, protein, 0.00001,
                "Expected a different amount of protein for a recipe.");
    }

    @Test
    public void calculateTotalFatTest() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 150, 1)
        );
        Ingredient ingredient1 = new Ingredient(
                "Test1",
                new NutritionValues(30, 10, 50)
        );

        UUID id = ingredient.getId();
        UUID id1 = ingredient1.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM)
        );

        RecipeIngredient ri1 = new RecipeIngredient(
                id1,
                new Amount(100, Unit.GRAM)
        );

        HashMap<UUID, Ingredient> ingredients = new HashMap<>();
        ingredients.put(id, ingredient);
        ingredients.put(id1, ingredient1);


        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri, ri1),
                List.of("step"),
                2,
                Language.EN
        );

        double fat = recipe.calcTotalFat(ingredients::get);
        assertEquals(25.0, fat, 0.00001,
                "Expected a different amount of fat for a recipe.");
    }

    @Test
    public void calculateTotalCarbsTest() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 1, 70)
        );
        Ingredient ingredient1 = new Ingredient(
                "Test1",
                new NutritionValues(30, 10, 50)
        );

        UUID id = ingredient.getId();
        UUID id1 = ingredient1.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM)
        );

        RecipeIngredient ri1 = new RecipeIngredient(
                id1,
                new Amount(100, Unit.GRAM)
        );

        HashMap<UUID, Ingredient> ingredients = new HashMap<>();
        ingredients.put(id, ingredient);
        ingredients.put(id1, ingredient1);


        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri, ri1),
                List.of("step"),
                2,
                Language.EN
        );

        double carbs = recipe.calcTotalCarbs(ingredients::get);
        assertEquals(57.0, carbs, 0.00001,
                "Expected a different amount of carbs for a recipe.");
    }

}
