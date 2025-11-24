package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class IngredientTest {
    NutritionValues nutritionValues;
    Ingredient ingredient;

    @BeforeEach
    public void setUp() {
        nutritionValues = new NutritionValues(0.0, 0.0, 100.0);
        ingredient = new Ingredient("Sugar", nutritionValues);
    }

    @Test
    public void testIngredientCreation() {
        assertNotNull(ingredient);
    }

    /*
    @Test
    public void testIngredientCreationWithId() {
        UUID id = UUID.randomUUID();
        Ingredient ingredientWithId = new Ingredient(id, "Salt", nutritionValues);
        assertNotNull(ingredientWithId);
        assertEquals(id, ingredientWithId.getId());
    }
        */

    @Test
    public void testGetId() {
        assertNotNull(ingredient.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Sugar", ingredient.getName());
    }

    @Test
    public void testGetNutritionValues() {
        assertEquals(nutritionValues, ingredient.getNutritionValues());
    }

    @Test
    public void testToString() {
        String id = ingredient.getId().toString();
        String expectedString = "Ingredient{id=" + id + ", name='Sugar'}";
        assertEquals(expectedString, ingredient.toString());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(ingredient, ingredient);
    }

    @Test
    public void testEqualsNull() {
        assertFalse(ingredient.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        String notAnIngredient = "Not an Ingredient";
        assertNotEquals(ingredient, notAnIngredient);
    }

    /*
    @Test
    public void testEqualsSameValues() {
        Ingredient ingredient2 = new Ingredient(ingredient.getId(), "Sugar", nutritionValues);
        assertEquals(ingredient, ingredient2);
    }
    */

    @Test
    public void testNotEqualsDifferentValues() {
        NutritionValues differentNutritionValues = new NutritionValues(1.0, 0.0, 99.0);
        Ingredient ingredient2 = new Ingredient("Salt", differentNutritionValues);
        assertNotEquals(ingredient, ingredient2);
    }

    @Test
    public void testNotEqualsDifferentId() {
        Ingredient ingredient2 = new Ingredient("Sugar", nutritionValues);
        assertNotEquals(ingredient, ingredient2);
    }

    /*
    @Test
    public void testNotEqualsDifferentName() {
        Ingredient ingredient2 = new Ingredient(ingredient.getId(),"Salt", nutritionValues);
        assertNotEquals(ingredient, ingredient2);
    }

    @Test
    public void testNotEqualsDifferentNutritionValues() {
        NutritionValues differentNutritionValues = new NutritionValues(1.0, 0.0, 99.0);
        Ingredient ingredient2 = new Ingredient(ingredient.getId(), "Sugar", differentNutritionValues);
        assertNotEquals(ingredient, ingredient2);
    }
    */
    
    @Test
    public void testHashCode() {
        assertEquals(ingredient.hashCode(), ingredient.hashCode());
    }

}
