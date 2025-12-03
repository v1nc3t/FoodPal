package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;  

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecipeIngredientTest {
    NutritionValues nutritionValues;
    Ingredient ingredient;
    Amount amount;
    RecipeIngredient recipeIngredient;
    UUID ingredientID;

    @BeforeEach
    public void setUp() {
        nutritionValues = new NutritionValues(0.0, 0.0, 76.0);
        ingredient = new Ingredient("Flour", nutritionValues);
        ingredientID = ingredient.getId();
        amount = new Amount(500, Unit.GRAM);
        recipeIngredient = new RecipeIngredient(ingredientID, amount);
    }

    //Constructor Tests
    @Test
    void testEmptyConstructor() {
        RecipeIngredient ri = new RecipeIngredient();
        assertNotNull(ri);
        assertNull(ri.getIngredientRef());
        assertNull(ri.getAmount());
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(recipeIngredient);
        assertEquals(ingredientID, recipeIngredient.getIngredientRef());
        assertEquals(amount, recipeIngredient.getAmount());
    }

    //Getter Tests
    @Test
    public void testGetIngredientRef() {
        assertEquals(ingredient.getId(), recipeIngredient.getIngredientRef());
    }

    @Test
    public void testGetAmount() {
        assertEquals(amount, recipeIngredient.getAmount());
    }

    //toString Test
    @Test
    public void testToString() {
        String expectedString = "RecipeIngredient{ingredientRef=" + ingredientID.toString() +
                                ", amount=" + amount.toString() + '}';
        assertEquals(expectedString, recipeIngredient.toString());
    }

    //Equals Test
    @Test
    public void testEqualsSameObject() {
        assertEquals(recipeIngredient, recipeIngredient);
    }

    @Test
    public void testEqualsNull() {
        assertNotEquals(recipeIngredient, null);
    }

    @Test
    public void testEqualsDifferentClass() {
        String notARecipeIngredient = "Not a RecipeIngredient";
        assertNotEquals(recipeIngredient, notARecipeIngredient);
    }

    @Test
    public void testEqualsSameValues() {
        RecipeIngredient anotherRecipeIngredient = new RecipeIngredient(ingredientID, amount);
        assertEquals(recipeIngredient, anotherRecipeIngredient);
    }

    @Test
    public void testNotEqualsDifferentIngredientRef() {
        RecipeIngredient anotherRecipeIngredient = new RecipeIngredient(UUID.randomUUID(), amount);
        assertNotEquals(recipeIngredient, anotherRecipeIngredient);
    }

    @Test
    public void testNotEqualsDifferentAmount() {
        Amount differentAmount = new Amount(300, Unit.GRAM);
        RecipeIngredient anotherRecipeIngredient = new RecipeIngredient(ingredientID, differentAmount);
        assertNotEquals(recipeIngredient, anotherRecipeIngredient);
    }

    //hashCode Test
    @Test
    public void testHashCode() {
        RecipeIngredient anotherRecipeIngredient = new RecipeIngredient(ingredientID, amount);
        assertEquals(recipeIngredient.hashCode(), anotherRecipeIngredient.hashCode());
    }

    

}
