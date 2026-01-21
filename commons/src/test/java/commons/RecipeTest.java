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
    private Language language;

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

        int portions = 4;
        language = Language.EN;

        recipe = new Recipe("Cake", ingredients, steps, portions, language);

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
    public void testGetPortions() {
        assertEquals(4, recipe.getPortions());
    }

    @Test
    public void testGetLanguage() {
        assertEquals(language, recipe.getLanguage());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(recipe, recipe);
    }

    @Test
    public void testScaleToPortions() {
        Recipe actual = recipe.scaleToPortions(2);

        List<RecipeIngredient> scaledIngredients = ingredients.stream()
                .map(ri -> ri.scale(0.5))
                .toList();

        Recipe expected = new Recipe(id, "Cake", scaledIngredients, steps, 2, language);
        assertEquals(expected, actual, "Expected scaled recipe to have scaled ingredients and portions.");
    }

    @Test
    public void testCalcTotalWeightInGrams() {
        double actual = recipe.calcTotalWeightInGrams();
        double expected = 480.0;
        assertEquals(expected, actual,
                0.00001, "Expected a different total weight in grams, while not counting informal amounts.");
    }
}
