package client.services;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class RecipeManager {

    private final Map<UUID, Recipe> recipesMap = new ConcurrentHashMap<>();
    private final Map<UUID, Ingredient> ingredientsMap = new ConcurrentHashMap<>();

    // Observable list for UI binding (JavaFX)
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();

    private static RecipeManager instance;

    private RecipeManager() {}

    public static synchronized RecipeManager getInstance() {
        if (instance == null) instance = new RecipeManager();
        return instance;
    }

    /* ---------- Observables / Snapshots ---------- */

    /** Observable list for binding to ListView (mutated on FX thread). */
    public ObservableList<Recipe> getObservableRecipes() {
        return recipes;
    }

    /** Non-JavaFX snapshot for tests / logic. */
    public List<Recipe> getRecipesSnapshot() {
        return new ArrayList<>(recipes);
    }

    /** Return a snapshot state  */
    public RecipeState getStateSnapshot() {
        Collection<Recipe> rc = List.copyOf(recipesMap.values());
        Collection<Ingredient> ic = List.copyOf(ingredientsMap.values());
        return new RecipeState(rc, ic);
    }

    /* ---------- Mutators (safe to call from any thread) ---------- */


    public boolean setRecipe(Recipe recipe) {
        if (recipe == null || recipe.getId() == null) return false;

        boolean allRefsExist = recipe.getIngredients().stream()
                .allMatch(ri -> ingredientsMap.containsKey(ri.getIngredientRef()));
        if (!allRefsExist) return false;

        recipesMap.put(recipe.getId(), recipe);
        runOnFx(() -> {
            int idx = indexOfRecipe(recipe.getId());
            if (idx >= 0) recipes.set(idx, recipe);
            else recipes.add(recipe);
        });
        return true;
    }

    /** Add recipe without strict ingredient validation */
    public void addRecipeOptimistic(Recipe recipe) {
        if (recipe == null || recipe.getId() == null) return;
        recipesMap.put(recipe.getId(), recipe);
        runOnFx(() -> {
            if (!recipes.contains(recipe)) recipes.add(recipe);
        });
    }

    public boolean removeRecipe(UUID recipeId) {
        Recipe removed = recipesMap.remove(recipeId);
        if (removed == null) return false;
        runOnFx(() -> recipes.removeIf(r -> Objects.equals(r.getId(), recipeId)));
        return true;
    }

    public boolean setIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null) return false;
        ingredientsMap.put(ingredient.getId(), ingredient);
        return true;
    }

    /* ---------- Helpers ---------- */

    private int indexOfRecipe(UUID id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (Objects.equals(recipes.get(i).getId(), id)) return i;
        }
        return -1;
    }

    private static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
