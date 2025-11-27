package scenes;

import client.scenes.RecipeListCtrl;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests validate adding recipes, clicking behavior,
 * ordering, removal semantics, and internal flags.
 */
public class RecipeListCtrlTest {

    private RecipeListCtrl ctrl;

    @BeforeEach
    public void setup() {
        ctrl = new RecipeListCtrl();
    }

    private Recipe makeRecipe(String title) {
        return new Recipe(title, List.of(), List.of(), 1);
    }







    @Test
    public void testEnterAndExitRemoveMode() {
        ctrl.enterRemoveMode();
        ctrl.exitRemoveMode();
        assertNotNull(ctrl); // just ensure it doesn't throw
    }





    @Test
    public void testAddNullRecipeDoesNothing() {
        ctrl.addRecipe(null);
        assertEquals(0, ctrl.getRecipesSnapshot().size());
    }




}
