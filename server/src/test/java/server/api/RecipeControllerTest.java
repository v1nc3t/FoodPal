package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.IRecipeService;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecipeControllerTest {
    RecipeController recipeController;
    TestRecipeService testRecipeService;
    Ingredient yogurt = new Ingredient("Yogurt", new NutritionValues(1, 2, 3));
    Ingredient sugar = new Ingredient("Sugar", new NutritionValues(1, 2, 3));
    List<String> preparationSteps = List.of("Melt sugar", "Freeze yogurt", "Blend");
    Recipe recipe = new Recipe("Sugared Yogurt",
            List.of(
                    new RecipeIngredient(yogurt.getId(), new Amount(1, Unit.CUP)),
                    new RecipeIngredient(sugar.getId(), new Amount(1, Unit.KILOGRAM))),
            preparationSteps,
            1, Language.EN);

    @BeforeEach
    public void setup() {
        testRecipeService = new TestRecipeService();
        recipeController = new RecipeController(testRecipeService);
    }

    @Test
    public void setIngredient() {
        testRecipeService.setIngredient(yogurt);
        var expectedService = new TestRecipeService();
        expectedService.setIngredient(yogurt);

        assertEquals(expectedService, testRecipeService);
    }

    @Test
    public void setRecipe() throws InvalidRecipeError {
        recipeController.setIngredient(yogurt);
        recipeController.setIngredient(sugar);
        recipeController.setRecipe(recipe);

        var expectedService = new TestRecipeService();
        expectedService.setIngredient(yogurt);
        expectedService.setIngredient(sugar);
        expectedService.setRecipe(recipe);

        assertEquals(expectedService, testRecipeService);
    }

    @Test
    public void getState() throws InvalidRecipeError {
        recipeController.setIngredient(yogurt);
        recipeController.setIngredient(sugar);
        recipeController.setRecipe(recipe);

        var expectedService = new TestRecipeService();
        expectedService.setIngredient(yogurt);
        expectedService.setIngredient(sugar);
        expectedService.setRecipe(recipe);

        assertEquals(expectedService.getState(), recipeController.getRecipeState());
    }

    @Test
    public void deleteRecipe() {
        recipeController.setIngredient(yogurt);
        recipeController.deleteRecipe(recipe.getId());

        var expectedService = new TestRecipeService();
        expectedService.setIngredient(yogurt);

        assertEquals(expectedService, testRecipeService);
    }

    @Test
    public void deleteIngredient() {
        recipeController.setIngredient(yogurt);
        recipeController.deleteIngredient(recipe.getId());

        var expectedService = new TestRecipeService();
        expectedService.setIngredient(yogurt);

        assertEquals(expectedService, testRecipeService);
    }
}

class TestRecipeService implements IRecipeService {
    public final HashMap<UUID, Recipe> recipes = new HashMap<>();
    public final HashMap<UUID, Ingredient> ingredients = new HashMap<>();

    @Override
    public RecipeState getState() {
        return new RecipeState(recipes.values(), ingredients.values());
    }

    @Override
    public void setRecipe(Recipe recipe) throws InvalidRecipeError {
        if (recipe == null)
            throw new InvalidRecipeError();
        if (recipe
                .getIngredients()
                .stream()
                .anyMatch(ingredient -> !ingredients.containsKey(ingredient.getIngredientRef())))
            throw new InvalidRecipeError();
        recipes.put(recipe.getId(), recipe);
    }

    @Override
    public void setIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
    }

    @Override
    public void deleteRecipe(UUID recipeId) {
        recipes.remove(recipeId);
    }

    @Override
    public void deleteIngredient(UUID ingredientId) {
        ingredients.remove(ingredientId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        TestRecipeService that = (TestRecipeService) o;
        return recipes.equals(that.recipes) && ingredients.equals(that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipes, ingredients);
    }
}
