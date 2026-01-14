package client.utils;

import commons.Ingredient;
import commons.Recipe;

import java.util.List;

public interface IServerUtils {

    List<Ingredient> getIngredients();

    List<Recipe> getRecipes();

    Recipe addRecipe(Recipe recipe);

    boolean isServerAvailable();
}
