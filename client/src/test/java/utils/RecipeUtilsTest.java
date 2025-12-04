package utils;

import client.utils.RecipeUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeUtilsTest {

    private Recipe makeSimpleRecipe(String title) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        steps.add("step1");
        steps.add("step2");
        return new Recipe(title, ingredients, steps, 2);
    }

    @Test
    public void cloneWithTitle_createsNewIdAndCopiesFields() {
        Recipe original = makeSimpleRecipe("Original");
        UUID origId = original.getId();

        Recipe cloned = RecipeUtils.cloneWithTitle(original, "Clone Name");

        assertNotNull(cloned);
        assertNotNull(cloned.getId());
        assertNotEquals(origId, cloned.getId(), "Cloned recipe should have a different id");
        assertEquals("Clone Name", cloned.getTitle(), "Cloned recipe title should match provided title");
        assertEquals(original.getServingSize(), cloned.getServingSize(), "Serving size should be copied");
        assertEquals(original.getSteps(), cloned.getSteps(), "Steps content should be equal");
        assertNotSame(original.getSteps(), cloned.getSteps(), "Steps list instance should be a distinct copy");
        assertEquals(original.getIngredients(), cloned.getIngredients(), "Ingredients content should be equal");
        assertNotSame(original.getIngredients(), cloned.getIngredients(), "Ingredients list instance should be a distinct copy");
    }
    @Test
    public void validateBasic_acceptsAndRejectsCorrectly() {
        Recipe good = makeSimpleRecipe("Good");
        assertTrue(RecipeUtils.validateBasic(good));

        Recipe noTitle = new Recipe("", new ArrayList<>(), List.of("s"), 1);
        assertFalse(RecipeUtils.validateBasic(noTitle));

        Recipe nullRecipe = null;
        assertFalse(RecipeUtils.validateBasic(nullRecipe));

        Recipe negativeServing = new Recipe("T", new ArrayList<>(), List.of("s"), -1);
        assertFalse(RecipeUtils.validateBasic(negativeServing));
    }
}
