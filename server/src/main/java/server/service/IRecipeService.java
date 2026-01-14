package server.service;

import commons.*;

import java.util.UUID;

public interface IRecipeService {
    /// Get a snapshot of the current state of the `RecipeService`, the returned
    /// value is a record, so it will not be updated.
    RecipeState getState();

    /// Note: Setting a recipe with the same id should replace the old version with
    /// the new one
    void setRecipe(Recipe recipe) throws InvalidRecipeError;

    /// Note: Setting an ingredient with the same id should replace the old version
    /// with the new one
    void setIngredient(Ingredient ingredient) throws InvalidIngredientError;

    /// Deletes a recipe based on its unique identifier.
    void deleteRecipe(UUID recipeId);

    ///  Deletes an ingredient based on its unique identifier
    void deleteIngredient(UUID ingredientId);
}
