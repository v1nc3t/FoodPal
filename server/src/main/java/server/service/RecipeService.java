package server.service;

import commons.Ingredient;
import commons.InvalidRecipeError;
import commons.Recipe;
import commons.RecipeState;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.UUID;

/// Service that keeps track of recipes
/// Temporarily it stores everything in memory
@Service
public class RecipeService {
    private final HashMap<UUID, Recipe> recipes =  new HashMap<>();
    private final HashMap<UUID, Ingredient> ingredients = new HashMap<>();

    /// Get a snapshot of the current state of the `RecipeService`,
    /// the returned value is a record, so it will not be updated.
    public RecipeState getState() {
        return new RecipeState(recipes.values(), ingredients.values());
    }

    /// Note: Setting a recipe with the same id should replace the old version with the new one
    public void setRecipe(Recipe recipe) throws InvalidRecipeError {
        if (recipe == null)
            throw new InvalidRecipeError();
        if (recipe
                .getIngredients()
                .stream()
                .anyMatch(ingredient ->
                        !ingredients.containsKey(ingredient.getIngredientRef())
                )
        )
            throw new InvalidRecipeError();
        recipes.put(recipe.getId(), recipe);
    }

    /// Note: Setting an ingredient with the same id should replace the old version with the new one
    public void setIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
    }
}
