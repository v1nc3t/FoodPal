package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecipeTest {
    private Recipe recipe;

    @BeforeEach
    public void setup() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 76.0);
        NutritionValues nutritionValues2 = new NutritionValues(13.0, 11.0, 1.1);   
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues1);
        Ingredient ingredient2 = new Ingredient("Eggs", nutritionValues2); 
        InformalAmount amount1 = new InformalAmount("2 cups");
        InformalAmount amount2 = new InformalAmount("3 large");
        RecipeIngredient RecIngredient = new RecipeIngredient(ingredient1.getId(), amount1);
        RecipeIngredient RecIngredient2 = new RecipeIngredient(ingredient2.getId(), amount2);
        List<RecipeIngredient> ingredients = List.of(RecIngredient, RecIngredient2);

        String step1 = "Mix ingredients.";
        String step2 = "Bake at 350 degrees for 30 minutes.";
        List<String> steps = List.of(step1, step2);

        int servingSize = 4;

        recipe = new Recipe("Cake", ingredients, steps, servingSize);
    }

    @Test
    public void testRecipeCreation() {
        assertNotNull(recipe);
    }

    @Test
    public void testGetId() {
        assertNotNull(recipe.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Cake", recipe.getTitle());
    }

    @Test
    public void testGetIngredients() {
        assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    public void testGetSteps() {
        assertEquals(2, recipe.getSteps().size());
    }

    @Test
    public void testGetServingSize() {
        assertEquals(4, recipe.getServingSize());
    }

    @Test
    public void testToString() {
        String expectedString = "Recipe{id=" + recipe.getId().toString() + ", title='Cake', ingredients=" + recipe.getIngredients() +
                ", steps='" + recipe.getSteps() + "', servingSize=4}";
        assertEquals(expectedString, recipe.toString());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(recipe, recipe);
    }

    @Test
    public void testEqualsNull() {
        assertNotEquals(null, recipe);
    }

    @Test
    public void testEqualsDifferentClass() {
        String notARecipe = "Not a Recipe";
        assertNotEquals(recipe, notARecipe);
    }

    @Test
    public void testEqualsSameValues() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 76.0);
        NutritionValues nutritionValues2 = new NutritionValues(13.0, 11.0, 1.1);
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues1);
        Ingredient ingredient2 = new Ingredient("Eggs", nutritionValues2); 
        InformalAmount amount1 = new InformalAmount("2 cups");
        InformalAmount amount2 = new InformalAmount("3 large");
        RecipeIngredient RecIngredient = new RecipeIngredient(ingredient1.getId(), amount1);
        RecipeIngredient RecIngredient2 = new RecipeIngredient(ingredient2.getId(), amount2);
        List<RecipeIngredient> ingredients = List.of(RecIngredient, RecIngredient2);

        String step1 = "Mix ingredients.";
        String step2 = "Bake at 350 degrees for 30 minutes.";
        List<String> steps = List.of(step1, step2);

        int servingSize = 4;

        Recipe anotherRecipe = new Recipe("Cake", ingredients, steps, servingSize);
        assertNotEquals(recipe, anotherRecipe);
    }

    @Test
    public void testNotEqualsDifferentValues() {
        NutritionValues nutritionValues = new NutritionValues(0.0, 0.0, 76.0);
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues);
        InformalAmount amount1 = new InformalAmount("2 cups");
        RecipeIngredient RecIngredient = new RecipeIngredient(ingredient1.getId(), amount1);
        List<RecipeIngredient> ingredients = List.of(RecIngredient);

        String step1 = "Mix ingredients.";
        List<String> steps = List.of(step1);

        int servingSize = 2;

        Recipe differentRecipe = new Recipe("Bread", ingredients, steps, servingSize);
        assertNotEquals(recipe, differentRecipe);
    }

    @Test
    public void testHashCode() {
        assertEquals(recipe.hashCode(), recipe.hashCode());
    }


}
