

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeListCtrlTest {

    private RecipeListCtrl ctrl;

    @BeforeEach
    public void setUp() {
        ctrl = new RecipeListCtrl();
    }

    private Recipe makeRecipe(String title) {
        return new Recipe(title, List.of(), List.of(), 1);
    }

    @Test
    public void testAddAndSnapshot() {
        Recipe r1 = makeRecipe("Pancake");
        ctrl.addRecipe(r1);
        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(1, snap.size());
        assertEquals("Pancake", snap.get(0).getTitle());
    }

    @Test
    public void testRemoveRecipe() {
        Recipe r1 = makeRecipe("A");
        Recipe r2 = makeRecipe("B");
        ctrl.addRecipe(r1);
        ctrl.addRecipe(r2);
        ctrl.removeRecipe(r2);
        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(1, snap.size());
        assertEquals("A", snap.get(0).getTitle());
    }

    @Test
    public void testEnterRemoveModeDoesNotThrow() {
        // enterRemoveMode modifies internal flag but doesn't need JavaFX to work for our tests
        ctrl.enterRemoveMode();
        ctrl.exitRemoveMode();
        assertTrue(true);
    }
}
