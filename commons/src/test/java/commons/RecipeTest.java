package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecipeTest {
    private Recipe recipe;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;
    private UUID id;

    @BeforeEach
    public void setup() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 76.0);
        NutritionValues nutritionValues2 = new NutritionValues(13.0, 11.0, 1.1);   
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues1);
        Ingredient ingredient2 = new Ingredient("Eggs", nutritionValues2); 
        Amount formalAmount = new Amount(2.0, Unit.CUP);
        Amount informalAmount = new Amount(3.0, "large");
        RecipeIngredient RecIngredient = new RecipeIngredient(ingredient1.getId(), formalAmount);
        RecipeIngredient RecIngredient2 = new RecipeIngredient(ingredient2.getId(), informalAmount);
        ingredients = List.of(RecIngredient, RecIngredient2);

        String step1 = "Mix ingredients.";
        String step2 = "Bake at 350 degrees for 30 minutes.";
        steps = List.of(step1, step2);

        int servingSize = 4;

        recipe = new Recipe("Cake", ingredients, steps, servingSize);

        id = recipe.getId();
    }

    @Test
    public void testRecipeCreation() {
        assertNotNull(recipe);
    }

    @Test
    public void testGetId() {
        assertEquals(id, recipe.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Cake", recipe.getTitle());
    }

    @Test
    public void testGetIngredients() {
        assertEquals(ingredients, recipe.getIngredients());
    }

    @Test
    public void testGetSteps() {
        assertEquals(steps, recipe.getSteps());
    }

    @Test
    public void testGetServingSize() {
        assertEquals(4, recipe.getServingSize());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(recipe, recipe);
    }   
}
