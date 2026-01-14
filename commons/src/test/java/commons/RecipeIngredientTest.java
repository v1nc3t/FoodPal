package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecipeIngredientTest {
    NutritionValues nutritionValues;
    Ingredient ingredient;
    Amount amount;
    RecipeIngredient recipeIngredient;

    @BeforeEach
    public void setUp() {
        nutritionValues = new NutritionValues(0.0, 0.0, 76.0);
        ingredient = new Ingredient("Flour", nutritionValues);
        amount = new Amount(500, Unit.GRAM);
        recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
    }

    @Test
    public void testRecipeIngredientCreation() {
        assertNotNull(recipeIngredient);
    }

    @Test
    public void testGetIngredientId() {
        assertEquals(ingredient.getId(), recipeIngredient.getIngredientRef());
    }

    @Test
    public void testGetAmount() {
        assertEquals(amount, recipeIngredient.getAmount());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(recipeIngredient, recipeIngredient);
    }
    @Test
    public void testScaleIngredientAmount() {
        RecipeIngredient scaled = recipeIngredient.scale(2.0);

        assertEquals(
                amount.quantity() * 2.0,
                scaled.getAmount().quantity()
        );
    }

    @Test
    public void testScaleKeepsSameIngredientReference() {
        RecipeIngredient scaled = recipeIngredient.scale(3.0);

        assertEquals(
                recipeIngredient.getIngredientRef(),
                scaled.getIngredientRef()
        );
    }

    @Test
    public void testScaleDoesNotModifyOriginal() {
        recipeIngredient.scale(5.0);

        assertEquals(
                500,
                recipeIngredient.getAmount().quantity()
        );
    }

}
