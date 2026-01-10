package commons;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCaloriesTest {

    @Test
    void caloriesPerPortionIsCalculatedCorrectly() {
        Ingredient ingredient = new Ingredient(
                "Test",
                new NutritionValues(1, 1, 1) // 17 kcal
        );

        UUID id = ingredient.getId();

        RecipeIngredient ri = new RecipeIngredient(
                id,
                new Amount(10, Unit.GRAM)
        );

        Recipe recipe = new Recipe(
                "Test Recipe",
                List.of(ri),
                List.of("step"),
                2,
                Language.EN
        );

        int kcal = recipe.getCaloriesPerPortion(
                (UUID lookup) -> ingredient
        );

        assertEquals(85, kcal); // 17 * 10 / 2
    }
}
