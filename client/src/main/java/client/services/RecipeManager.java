package client.services;

import commons.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * - Keeps maps for fast lookup and validation.
 * - Exposes an ObservableList for JavaFX views.
 * - Marshals UI updates onto the JavaFX thread.
 * Use RecipeManager.getInstance() to access the singleton.
 */
public class RecipeManager {

    private final Map<UUID, Recipe> recipesMap = new ConcurrentHashMap<>();
    private final Map<UUID, Ingredient> ingredientsMap = new ConcurrentHashMap<>();
    private final Set<UUID> favouriteRecipes = new HashSet<>();

    // Observable list for UI binding (JavaFX)
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();

    public RecipeManager() {
        // Seed a test recipe so ListView shows something immediately during manual testing.
        seedSampleRecipe();
    }

    /** Observable list for binding to ListView (mutated on FX thread). */
    public ObservableList<Recipe> getObservableRecipes() {
        return recipes;
    }

    /** Non-JavaFX snapshot for tests / logic. */
    public List<Recipe> getRecipesSnapshot() {
        return new ArrayList<>(recipes);
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



    /**
     Returns true if stored, false if invalid input.
     */
    public boolean setRecipe(Recipe recipe) {
        if (recipe == null) return false;

        boolean allRefsExist = recipe.getIngredients().stream()
                .allMatch(ri -> ingredientsMap.containsKey(ri.getIngredientRef()));
        if (!allRefsExist) return false;

        // store
        if (recipe.getId() != null) recipesMap.put(recipe.getId(), recipe);

        CountDownLatch latch = new CountDownLatch(1);
        runOnFx(() -> {
            int idx = indexOfRecipe(recipe.getId());
            if (idx >= 0) recipes.set(idx, recipe);
            else recipes.add(recipe);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Gets the recipe by id
     * @param id the key to look for
     * @return the recipe if found, null if not
     */
    public Recipe getRecipe(UUID id) {
        return recipesMap.get(id);
    }

    /** Add recipe without strict ingredient validation (useful for optimistic UI). */
    public void addRecipeOptimistic(Recipe recipe) {
        if (recipe == null) return;
        if (recipe.getId() != null) recipesMap.put(recipe.getId(), recipe);
        runOnFx(() -> {
            int idx = indexOfRecipe(recipe.getId());
            if (idx >= 0) recipes.set(idx, recipe);
            else recipes.add(recipe);
        });
    }

    public boolean removeRecipe(UUID recipeId) {
        if (recipeId == null) return false;
        favouriteRecipes.remove(recipeId); // keep favourites consistent
        Recipe removed = recipesMap.remove(recipeId);
        // still remove from observable list
        runOnFx(() -> recipes.removeIf(r -> Objects.equals(r.getId(), recipeId)));
        return removed != null;
    }

    public boolean setIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null) return false;
        ingredientsMap.put(ingredient.getId(), ingredient);
        return true;
    }



    private int indexOfRecipe(UUID id) {
        if (id == null) return -1;
        for (int i = 0; i < recipes.size(); i++) {
            if (Objects.equals(recipes.get(i).getId(), id)) return i;
        }
        return -1;
    }

    private static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    /** Adds a single in-memory test recipe so the ListView shows an entry at startup. */
    private void seedSampleRecipe() {
        try {
            Ingredient sampleIngredient = new Ingredient("Honey Tester", new NutritionValues(1,1,1));
            ingredientsMap.put(sampleIngredient.getId(), sampleIngredient);

            // empty ingredient list for quick seed
            List<String> preparations = List.of("Mix flour, eggs and milk", "Fry on medium heat");
            int servingSize = 2;


            Recipe sampleRecipe = new Recipe("Test Pancakes",
                    List.of(
                            new RecipeIngredient(
                                    sampleIngredient.getId(),
                                    new Amount(10, "doll hairs")
                            )
                    ),
                    preparations,
                    servingSize);

            // store in maps if id exists
            if (sampleRecipe.getId() != null) recipesMap.put(sampleRecipe.getId(), sampleRecipe);
            recipes.add(sampleRecipe);
        } catch (Throwable t) {
            // do not block startup on a test seed failure
            t.printStackTrace();
        }
    }

    /** Clears internal state for unit tests only. */
    public void clearForTests() {
        recipesMap.clear();
        ingredientsMap.clear();
        CountDownLatch latch =  new CountDownLatch(1);
        runOnFx(() -> {
            recipes.clear();
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
        if (id == null) return;
        if (favouriteRecipes.contains(id)) {
            favouriteRecipes.remove(id);
        } else {
            favouriteRecipes.add(id);
        }
    }

    public Set<UUID> getFavouriteRecipesSnapshot() {
        return Set.copyOf(favouriteRecipes);
    }

}
