package client.scenes;

import commons.Ingredient;
import commons.Recipe;

import java.util.UUID;

public record ListObject(UUID id, String name) {
    public static ListObject fromRecipe(Recipe recipe) {
        return new ListObject(recipe.getId(), recipe.getTitle());
    }
    public static ListObject fromIngredient(Ingredient ingredient) {
        return new ListObject(ingredient.getId(), ingredient.getName());
    }
}
