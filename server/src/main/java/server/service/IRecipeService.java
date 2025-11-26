package server.service;

import commons.Ingredient;
import commons.InvalidRecipeError;
import commons.Recipe;
import commons.RecipeState;

public interface IRecipeService {
    /// Get a snapshot of the current state of the `RecipeService`,
    /// the returned value is a record, so it will not be updated.
    RecipeState getState();

    /// Note: Setting a recipe with the same id should replace the old version with the new one
    void setRecipe(Recipe recipe) throws InvalidRecipeError;

    /// Note: Setting an ingredient with the same id should replace the old version with the new one
    void setIngredient(Ingredient ingredient);
}
