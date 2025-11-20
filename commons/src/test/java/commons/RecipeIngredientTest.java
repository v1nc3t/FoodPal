package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class RecipeIngredientTest {
    @Test
    public void testRecipeIngredientCreation() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
        assertNotNull(recipeIngredient);
    }

    @Test
    public void testGetIngredientRef() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
        assertEquals(recipeIngredient.getIngredientRef(), ingredient.getId());
    }

    @Test
    public void testGetAmount() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
        assertEquals(amount, recipeIngredient.getAmount());
    }

    @Test
    public void testToString() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
        String expectedString = "RecipeIngredient{ingredientRef=" + ingredient.getId() + ", amount=" + amount + '}';
        assertEquals(expectedString, recipeIngredient.toString());
    }

    @Test
    public void testEqualsSameObject() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient.getId(), amount1);
        assertEquals(recipeIngredient1, recipeIngredient1);
    }

    @Test
    public void testEqualsNull() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient.getId(), amount1);
        assertNotEquals(null, recipeIngredient1);
    }

    @Test
    public void testEqualsDifferentClass() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient.getId(), amount1);
        String notARecipeIngredient = "Not a RecipeIngredient";
        assertNotEquals(recipeIngredient1, notARecipeIngredient);
    }

    @Test
    public void testEqualsDifferentIngredientRef() {
        Ingredient ingredient1 = new Ingredient("Flour");
        Ingredient ingredient2 = new Ingredient("Sugar");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient1.getId(), amount);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredient2.getId(), amount);
        assertNotEquals(recipeIngredient1, recipeIngredient2);
    }

    @Test
    public void testEqualsDifferentAmount() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount1 = new FormalAmount(500, Unit.GRAM);
        Amount amount2 = new FormalAmount(1, Unit.KILOGRAM);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredient.getId(), amount1);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredient.getId(), amount2);
        assertNotEquals(recipeIngredient1, recipeIngredient2);
    }

    @Test
    public void testHashCode() {
        Ingredient ingredient = new Ingredient("Flour");
        Amount amount = new FormalAmount(500, Unit.GRAM);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient.getId(), amount);
        assertEquals(recipeIngredient.hashCode(), recipeIngredient.hashCode());
    }
    
}
