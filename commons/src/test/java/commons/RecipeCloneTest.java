package commons;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeCloneTest {

    private Recipe makeSimpleRecipe() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient(UUID.randomUUID(), null));
        List<String> steps = List.of("step1");
        return new Recipe("Original", ingredients, steps, 2);
    }

    @Test
    public void cloneCreatesNewIdAndCopiesFields() {
        Recipe original = makeSimpleRecipe();
        Recipe cloned = original.cloneWithTitle("Clone Name");

        assertNotNull(cloned);
        assertNotNull(cloned.getId());
        assertNotEquals(original.getId(), cloned.getId(), "IDs must differ");
        assertEquals("Clone Name", cloned.getTitle());
        assertEquals(original.getServingSize(), cloned.getServingSize());
        assertEquals(original.getSteps(), cloned.getSteps());
        assertNotSame(original.getSteps(), cloned.getSteps(), "Steps list must be copied");
        assertEquals(original.getIngredients().size(), cloned.getIngredients().size());
        assertNotSame(original.getIngredients().get(0), cloned.getIngredients().get(0),
                "RecipeIngredient instances should be distinct");
    }
}
