package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


public class IngredientTest {

    @Test
    public void testIngredientCreation() {
        Ingredient ingredient = new Ingredient("Sugar");
        assertNotNull(ingredient);
    }

    @Test
    public void testGetId() {
        Ingredient ingredient = new Ingredient("Sugar");
        assertEquals(2, ingredient.getId());
    }

    @Test
    public void testGetName() {
        Ingredient ingredient = new Ingredient("Sugar");
        assertEquals("Sugar", ingredient.getName());
    }

    @Test
    public void testSetName() {
        Ingredient ingredient = new Ingredient("Sugar");
        ingredient.setName("Salt");
        assertEquals("Salt", ingredient.getName());
    }

    @Test
    public void testToString() {
        Ingredient ingredient = new Ingredient("Sugar");
        int id = ingredient.getId();
        String expectedString = "Ingredient{id=" + id + ", name='Sugar'}";
        assertEquals(expectedString, ingredient.toString());
    }

    @Test
    public void testEqualsSameObject() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        assertEquals(ingredient1, ingredient1);
    }

    @Test
    public void testEqualsNull() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        assertNotEquals(null, ingredient1);
    }

    @Test
    public void testEqualsDifferentClass() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        String notAnIngredient = "Not an Ingredient";
        assertNotEquals(ingredient1, notAnIngredient);
    }

    @Test
    public void testEqualsDifferentNames() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        Ingredient ingredient2 = new Ingredient("Salt");
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    public void testEqualsSameNamesDifferentIds() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        Ingredient ingredient2 = new Ingredient("Sugar");
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    public void testHashCode() {
        Ingredient ingredient1 = new Ingredient("Sugar");
        assertEquals(ingredient1.hashCode(), ingredient1.hashCode());
    }

}
