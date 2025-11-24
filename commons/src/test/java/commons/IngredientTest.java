package commons;

import static org.junit.jupiter.api.Assertions.*;

<<<<<<< HEAD
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
=======
>>>>>>> parent of c38410b (refactor tests to use NutritionValues for ingredient creation and improve readability)
import org.junit.jupiter.api.Test;


public class IngredientTest {

    @Test
    public void testIngredientCreation() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertNotNull(ingredient);
    }

    @Test
    public void testIngredientCreationWithId() {
        UUID id = UUID.randomUUID();
        Ingredient ingredientWithId = new Ingredient(id, "Salt", nutritionValues);
        assertNotNull(ingredientWithId);
        assertEquals(id, ingredientWithId.getId());
    }

    @Test
    public void testGetId() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertNotNull(ingredient.getId());
    }

    @Test
    public void testGetName() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals("Sugar", ingredient.getName());
    }

    @Test
    public void testGetProteinPer100g() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals(0.0, ingredient.getProteinPer100g());
    }

    @Test
    public void testGetFatPer100g() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals(0.0, ingredient.getFatPer100g());
    }

    @Test
    public void testGetCarbsPer100g() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals(100.0, ingredient.getCarbsPer100g());
    }

    @Test
    public void testToString() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        String id = ingredient.getId().toString();
        String expectedString = "Ingredient{id=" + id + ", name='Sugar'}";
        assertEquals(expectedString, ingredient.toString());
    }

    @Test
    public void testEqualsSameObject() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals(ingredient1, ingredient1);
    }

    @Test
    public void testEqualsNull() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertFalse(ingredient1.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        String notAnIngredient = "Not an Ingredient";
        assertNotEquals(ingredient1, notAnIngredient);
    }

    @Test
<<<<<<< HEAD
    public void testEqualsSameValues() {
        Ingredient ingredient2 = new Ingredient(ingredient.getId(), "Sugar", nutritionValues);
        assertEquals(ingredient, ingredient2);
    }

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
=======
    public void testEqualsDifferentNames() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        Ingredient ingredient2 = new Ingredient("Salt", 0.0, 0.0, 0.0);
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    public void testEqualsSameNamesDifferentIds() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        Ingredient ingredient2 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertNotEquals(ingredient1, ingredient2);
>>>>>>> parent of c38410b (refactor tests to use NutritionValues for ingredient creation and improve readability)
    }

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
    
    @Test
    public void testHashCode() {
        Ingredient ingredient1 = new Ingredient("Sugar", 0.0, 0.0, 100.0);
        assertEquals(ingredient1.hashCode(), ingredient1.hashCode());
    }

}
