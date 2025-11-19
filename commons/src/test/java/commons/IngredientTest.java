package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IngredientTest {
    
    @Test
    public void testIngredientCreation() {
        Ingredient ingredient = new Ingredient("Sugar");
    }

    @Test
    public void testGetName() {
        Ingredient ingredient = new Ingredient("Salt");
        assertEquals("Salt", ingredient.getName());
    }
}
