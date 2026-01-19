package client.services;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * - Keeps maps for fast lookup and validation.
 * - Exposes an ObservableList for JavaFX views.
 * - Marshals UI updates onto the JavaFX thread.
 */
public class RecipeManager {

    private final Map<UUID, Recipe> recipesMap = new ConcurrentHashMap<>();
    private final Map<UUID, Ingredient> ingredientsMap = new ConcurrentHashMap<>();
    private final Set<UUID> favouriteRecipes = new HashSet<>();
    private final Map<UUID, Integer> scaledRecipesMap = new ConcurrentHashMap<>();

    // Observable list for UI binding (JavaFX)
    private final ObservableList<Recipe> recipesFx = FXCollections.observableArrayList();
    private final ObservableList<Ingredient> ingredientsFx = FXCollections.observableArrayList();

    @Inject
    private ServerUtils server;

    public RecipeManager() {
        // Seed a test recipe so ListView shows something immediately during manual
        // testing.
        seedSampleRecipe();
    }

    /** Observable list for binding to ListView (mutated on FX thread). */
    public ObservableList<Recipe> getObservableRecipes() {
        return recipesFx;
    }

    /** Observable list for binding to ListView (mutated on FX thread). */
    public ObservableList<Ingredient> getObservableIngredients() {
        return ingredientsFx;
    }

    /** Non-JavaFX snapshot for tests / logic. */
    public List<Recipe> getRecipesSnapshot() {
        return new ArrayList<>(recipesFx);
    }

    /** Return a snapshot state (defensive copies). */
    public RecipeState getStateSnapshot() {
        Collection<Recipe> rc = List.copyOf(recipesMap.values());
        Collection<Ingredient> ic = List.copyOf(ingredientsMap.values());
        return new RecipeState(rc, ic);
    }

    /** Get the ingredient by ingredient reference */
    public Ingredient getIngredient(RecipeIngredient recipeIngredient) {
        return ingredientsMap.get(recipeIngredient.getIngredientRef());
    }

    /** Get the ingredient by ingredient id */
    public Ingredient getIngredient(UUID id) {
        return ingredientsMap.get(id);
    }

    public void sync(List<Recipe> recipes, List<Ingredient> ingredients) {
        runOnFx(() -> {
            recipesMap.clear();
            ingredientsMap.clear();

            for (Ingredient ingredient : ingredients) {
                ingredientsMap.put(ingredient.getId(), ingredient);
            }
            ingredientsFx.setAll(ingredients);

            for (Recipe recipe : recipes) {
                recipesMap.put(recipe.getId(), recipe);
            }
            recipesFx.setAll(recipes);
        });
    }

    public void applyRecipeUpdate(Recipe recipe) {
        if (recipe == null || recipe.getId() == null)
            return;
        recipesMap.put(recipe.getId(), recipe);
        runOnFx(() -> {
            int idx = indexOfRecipe(recipe.getId());
            if (idx >= 0)
                recipesFx.set(idx, recipe);
            else
                recipesFx.add(recipe);
        });
    }

    public void applyIngredientUpdate(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null)
            return;
        ingredientsMap.put(ingredient.getId(), ingredient);
        runOnFx(() -> {
            int idx = indexOfIngredient(ingredient.getId());
            if (idx >= 0)
                ingredientsFx.set(idx, ingredient);
            else
                ingredientsFx.add(ingredient);
        });
    }

    public void applyRecipeDelete(UUID id) {
        if (id == null)
            return;
        recipesMap.remove(id);
        favouriteRecipes.remove(id);
        scaledRecipesMap.remove(id);
        runOnFx(() -> recipesFx.removeIf(r -> Objects.equals(r.getId(), id)));
    }

    public void applyIngredientDelete(UUID id) {
        if (id == null)
            return;
        ingredientsMap.remove(id);
        runOnFx(() -> ingredientsFx.removeIf(i -> Objects.equals(i.getId(), id)));
    }

    /**
     * Returns true if stored, false if invalid input.
     */
    public boolean setRecipe(Recipe recipe) {
        if (recipe == null)
            return false;

        boolean allRefsExist = recipe.getIngredients().stream()
                .allMatch(ri -> ingredientsMap.containsKey(ri.getIngredientRef()));
        if (!allRefsExist)
            return false;

        try {
            server.setRecipe(recipe);

            if (recipe.getId() != null) {
                recipesMap.put(recipe.getId(), recipe);
            }

            CountDownLatch latch = new CountDownLatch(1);
            runOnFx(() -> {
                int idx = indexOfRecipe(recipe.getId());
                if (idx >= 0) {
                    recipesFx.set(idx, recipe);
                } else {
                    recipesFx.add(recipe);
                }
                latch.countDown();
            });

            return true;
        } catch (Exception e) {
            System.err.println("Failed to save recipe to server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the recipe by id
     * 
     * @param id the key to look for
     * @return the recipe if found, null if not
     */
    public Recipe getRecipe(UUID id) {
        return recipesMap.get(id);
    }

    /**
     * Add recipe without strict ingredient validation (useful for optimistic UI).
     */
    public void addRecipeOptimistic(Recipe recipe) {
        if (recipe == null)
            return;
        if (recipe.getId() != null)
            recipesMap.put(recipe.getId(), recipe);
        runOnFx(() -> {
            int idx = indexOfRecipe(recipe.getId());
            if (idx >= 0)
                recipesFx.set(idx, recipe);
            else
                recipesFx.add(recipe);
        });
    }

    public boolean removeRecipe(UUID recipeId) {
        if (recipeId == null)
            return false;

        server.removeRecipe(recipeId);

        // keep favourites consistent
        favouriteRecipes.remove(recipeId);
        // keep scaled recipes consistent
        scaledRecipesMap.remove(recipeId);
        Recipe removed = recipesMap.remove(recipeId);

        // still remove from observable list
        runOnFx(() -> recipesFx.removeIf(r -> Objects.equals(r.getId(), recipeId)));

        return removed != null;
    }

    public boolean setIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null)
            return false;

        try {
            server.setIngredient(ingredient);

            ingredientsMap.put(ingredient.getId(), ingredient);

            runOnFx(() -> {
                int idx = indexOfIngredient(ingredient.getId());
                if (idx >= 0)
                    ingredientsFx.set(idx, ingredient);
                else
                    ingredientsFx.add(ingredient);
            });

            return true;
        } catch (Exception e) {
            System.err.println("Failed to save ingredient to server: " + e.getMessage());
            return false;
        }
    }

    public boolean removeIngredient(UUID ingredientId) {
        if (ingredientId == null)
            return false;

        var usedRecipe = recipesMap
                .values()
                .stream()
                .filter(rv -> rv
                        .getIngredients()
                        .stream()
                        .anyMatch(ri -> ri.ingredientRef == ingredientId))
                .findAny().orElse(null);

        if (usedRecipe != null)
            throw new RuntimeException(
                    "Cannot remove ingredient, because it is used in recipe: " +
                            usedRecipe.getTitle());

        server.removeIngredient(ingredientId);

        Ingredient removed = ingredientsMap.remove(ingredientId);

        // still remove from observable list
        runOnFx(() -> ingredientsFx.removeIf(r -> Objects.equals(r.getId(), ingredientId)));

        return removed != null;
    }

    public int indexOfRecipe(UUID id) {
        if (id == null)
            return -1;
        for (int i = 0; i < recipesFx.size(); i++) {
            if (Objects.equals(recipesFx.get(i).getId(), id))
                return i;
        }
        return -1;
    }

    private int indexOfIngredient(UUID id) {
        if (id == null)
            return -1;
        for (int i = 0; i < ingredientsFx.size(); i++) {
            if (Objects.equals(ingredientsFx.get(i).getId(), id))
                return i;
        }
        return -1;
    }

    private static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread())
            r.run();
        else
            Platform.runLater(r);
    }

    /**
     * Adds a single in-memory test recipe so the ListView shows an entry at
     * startup.
     */
    private void seedSampleRecipe() {
        try {
            Ingredient sampleIngredient = new Ingredient("Honey Tester",
                    new NutritionValues(1, 1, 1));
            setIngredient(sampleIngredient);

            // empty ingredient list for quick seed
            List<String> preparations = List.of("Mix flour, eggs and milk", "Fry on medium heat");
            int portions = 2;
            Language language = Language.EN;

            Recipe sampleRecipe = new Recipe("Test Pancakes",
                    List.of(
                            new RecipeIngredient(
                                    sampleIngredient.getId(),
                                    new Amount(10, "doll hairs"))),
                    preparations,
                    portions,
                    language);

            addRecipeOptimistic(sampleRecipe);
        } catch (Throwable t) {
            // do not block startup on a test seed failure
            t.printStackTrace();
        }
    }

    /** Clears internal state for unit tests only. */
    public void clearForTests() {
        recipesMap.clear();
        ingredientsMap.clear();
        CountDownLatch latch = new CountDownLatch(1);
        runOnFx(() -> {
            recipesFx.clear();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isFavourite(UUID id) {
        return favouriteRecipes.contains(id);
    }

    public void toggleFavourite(UUID id) {
        if (id == null)
            return;
        if (favouriteRecipes.contains(id)) {
            favouriteRecipes.remove(id);
        } else {
            favouriteRecipes.add(id);
        }
    }

    public Set<UUID> getFavouriteRecipesSnapshot() {
        return Set.copyOf(favouriteRecipes);
    }

    /**
     * Checks whether a recipe with a given id is scaled.
     * 
     * @param id recipe UUID
     * @return true if scaled, false otherwise
     */
    public boolean isScaled(UUID id) {
        return scaledRecipesMap.containsKey(id);
    }

    /**
     * Adds a scaled recipe entry to the map or updates the scale if the recipe
     * already exists.
     * Treats a scale of 0 as resetting the scale, therefore removing the recipe
     * from the map.
     * 
     * @param id    recipe UUID
     * @param scale integer number to be scaled by (in portion units)
     */
    public void setScaledRecipe(UUID id, Integer scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Scale must be an integer");
        }
        if (scale == 0) {
            removeScaledRecipe(id);
        } else {
            scaledRecipesMap.put(id, scale);
        }
    }

    /**
     * Gets the scale of a scaled recipe.
     * 
     * @param id recipe UUID
     * @return the scale of the recipe
     */
    public Integer getRecipeScale(UUID id) {
        return scaledRecipesMap.get(id);
    }

    /**
     * Removes a scaled recipe entry from the map.
     * 
     * @param id recipe UUID
     */
    public void removeScaledRecipe(UUID id) {
        scaledRecipesMap.remove(id);
    }
}
