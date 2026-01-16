package client.utils;

import commons.Ingredient;
import commons.Recipe;

import java.util.List;
import java.util.UUID;

public interface IServerUtils {

    List<Ingredient> getIngredients();

    List<Recipe> getRecipes();

    Recipe setRecipe(Recipe recipe);

    Ingredient setIngredient(Ingredient ingredient);

    void removeRecipe(UUID reciepId);

    void removeIngredient(UUID ingredientId);

    boolean isServerAvailable();
}
