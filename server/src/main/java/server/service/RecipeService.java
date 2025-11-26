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
public class RecipeService implements IRecipeService {
    private final HashMap<UUID, Recipe> recipes =  new HashMap<>();
    private final HashMap<UUID, Ingredient> ingredients = new HashMap<>();

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
                .anyMatch(ingredient ->
                        !ingredients.containsKey(ingredient.getIngredientRef())
                )
        )
            throw new InvalidRecipeError();
        recipes.put(recipe.getId(), recipe);
    }

    @Override
    public void setIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
    }
}
