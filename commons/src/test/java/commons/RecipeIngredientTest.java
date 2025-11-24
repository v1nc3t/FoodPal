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
        amount = new FormalAmount(500, Unit.GRAM);
        recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
    }

    @Test
    public void testRecipeIngredientCreation() {
       assertNotNull(recipeIngredient);
    }

    @Test
    public void testGetIngredientRef() {
        assertEquals(recipeIngredient.getIngredientRef(), ingredient.getId());
    }

    @Test
    public void testGetAmount() {
       assertEquals(amount, recipeIngredient.getAmount());
    }

    @Test
    public void testToString() {
        String expectedString = "RecipeIngredient{ingredientRef=" + ingredient.getId() + ", amount=" + amount + '}';
        assertEquals(expectedString, recipeIngredient.toString());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(recipeIngredient, recipeIngredient);
    }

    @Test
    public void testEqualsNull() {
        assertFalse(recipeIngredient.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        String notARecipeIngredient = "Not a RecipeIngredient";
        assertNotEquals(recipeIngredient, notARecipeIngredient);
    }

    @Test
    public void testEqualsDifferentIngredientRef() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 100.0);
        Ingredient ingredient2 = new Ingredient("Sugar", nutritionValues1);
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredient2.getId(), amount1);
        assertNotEquals(recipeIngredient, recipeIngredient2);
    }

    @Test
    public void testEqualsDifferentAmount() {
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        Amount amount2 = new FormalAmount(1, Unit.KILOGRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount1);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredient.getId(), amount2);
        assertNotEquals(recipeIngredient, recipeIngredient2);
    }

    @Test
    public void testEqualsSameFieldsAndID() {
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient.getId(), amount1);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredient.getId(), amount1);
        assertEquals(recipeIngredient1, recipeIngredient2);
    }

    @Test
    public void testHashCode() {
       assertEquals(recipeIngredient.hashCode(), recipeIngredient.hashCode());
    }
    
}
