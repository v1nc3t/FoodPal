package client.utils;

import commons.Recipe;
import commons.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Small helper utilities for recipes: cloning and basic validation.
 */
public final class RecipeUtils {

    private RecipeUtils() {}

    /**
     * Create a clone of the given recipe with a new title and a new id.
     * The returned Recipe contains shallow-copied lists (new List objects),
     */
    public static Recipe cloneWithTitle(Recipe original, String newTitle) {
        if (original == null) throw new IllegalArgumentException("original is null");
        List<RecipeIngredient> ingr = original.getIngredients() == null ? null
                : new ArrayList<>(original.getIngredients().size());
        if (original.getIngredients() != null) {
            for (RecipeIngredient ri : original.getIngredients()) {
                ingr.add(ri); // copy of entries
            }
        }
        List<String> steps = original.getSteps() == null ? null : new ArrayList<>(original.getSteps());
        Recipe clone = new Recipe(newTitle, ingr, steps, original.getServingSize());
        try {
            var idField = Recipe.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(clone, UUID.randomUUID());
        } catch (Exception ignored) {}
        return clone;
    }

    /**
     * Basic validation that a recipe has a non-empty title and non-negative servings.
     */
    public static boolean validateBasic(Recipe recipe) {
        if (recipe == null) return false;
        String t = recipe.getTitle();
        if (t == null || t.trim().isEmpty()) return false;
        return recipe.getServingSize() >= 0;
    }
}
