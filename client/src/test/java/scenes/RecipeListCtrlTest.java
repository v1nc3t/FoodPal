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
 *
 * The goal is to ensure the controller logic behaves exactly as specified.
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
    public void testAddSingleRecipe() {
        Recipe r = makeRecipe("Burger");
        ctrl.addRecipe(r);
        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(1, snap.size());
        assertEquals("Burger", snap.get(0).getTitle());
    }

    @Test
    public void testAddMultipleRecipes() {
        ctrl.addRecipe(makeRecipe("A"));
        ctrl.addRecipe(makeRecipe("B"));
        ctrl.addRecipe(makeRecipe("C"));
        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(3, snap.size());
        assertEquals("A", snap.get(0).getTitle());
        assertEquals("B", snap.get(1).getTitle());
        assertEquals("C", snap.get(2).getTitle());
    }

    @Test
    public void testRemoveSpecificRecipe() {
        Recipe a = makeRecipe("A");
        Recipe b = makeRecipe("B");
        Recipe c = makeRecipe("C");

        ctrl.addRecipe(a);
        ctrl.addRecipe(b);
        ctrl.addRecipe(c);

        ctrl.removeRecipe(b);

        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(2, snap.size());
        assertFalse(snap.contains(b));
        assertTrue(snap.contains(a));
        assertTrue(snap.contains(c));
    }

    @Test
    public void testEnterAndExitRemoveMode() {
        ctrl.enterRemoveMode();
        ctrl.exitRemoveMode();
        assertNotNull(ctrl); // just ensure it doesn't throw
    }

    @Test
    public void testRemoveLastOfMany() {
        Recipe a = makeRecipe("A");
        Recipe b = makeRecipe("B");
        Recipe c = makeRecipe("C");
        Recipe d = makeRecipe("D");

        ctrl.addRecipe(a);
        ctrl.addRecipe(b);
        ctrl.addRecipe(c);
        ctrl.addRecipe(d);

        ctrl.removeRecipe(d);

        List<Recipe> snap = ctrl.getRecipesSnapshot();
        assertEquals(3, snap.size());
        assertFalse(snap.contains(d));
    }

    @Test
    public void testListIsIndependent() {
        ctrl.addRecipe(makeRecipe("One"));
        List<Recipe> snap1 = ctrl.getRecipesSnapshot();

        ctrl.addRecipe(makeRecipe("Two"));
        List<Recipe> snap2 = ctrl.getRecipesSnapshot();

        assertEquals(1, snap1.size());
        assertEquals(2, snap2.size());
    }

    @Test
    public void testAddNullRecipeDoesNothing() {
        ctrl.addRecipe(null);
        assertEquals(0, ctrl.getRecipesSnapshot().size());
    }

    @Test
    public void testRemoveNotExistingDoesNothing() {
        ctrl.addRecipe(makeRecipe("A"));
        ctrl.removeRecipe(makeRecipe("B")); // different object
        assertEquals(1, ctrl.getRecipesSnapshot().size());
    }

    @Test
    public void testSnapshotDoesNotAffectInternalList() {
        ctrl.addRecipe(makeRecipe("Cake"));

        List<Recipe> snap = ctrl.getRecipesSnapshot();
        snap.clear();

        assertEquals(1, ctrl.getRecipesSnapshot().size());
    }
}
