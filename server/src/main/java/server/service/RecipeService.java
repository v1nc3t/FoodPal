package server.service;

import commons.Ingredient;
import commons.InvalidRecipeError;
import commons.Recipe;
import commons.RecipeState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.IngredientRepository;
import server.database.RecipeRepository;
import server.websocket.WebSocketHub;

import java.util.HashMap;
import java.util.UUID;

/// Service that keeps track of recipes
@Service
@Transactional
public class RecipeService implements IRecipeService {
    private final HashMap<UUID, Recipe> recipes = new HashMap<>();
    private final HashMap<UUID, Ingredient> ingredients = new HashMap<>();

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final WebSocketHub webSocketHub;

    public RecipeService(RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            WebSocketHub webSocketHub) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.webSocketHub = webSocketHub;

        for (Ingredient ingredient : ingredientRepository.findAll()) {
            ingredients.put(ingredient.getId(), ingredient);
        }
        System.out.println("Loaded " + ingredients.size() + " ingredients");

        for (Recipe recipe : recipeRepository.findAll()) {
            recipes.put(recipe.getId(), recipe);
        }
        System.out.println("Loaded " + recipes.size() + " recipes");
    }

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
                .anyMatch(ingredient -> !ingredients.containsKey(ingredient.getIngredientRef())))
            throw new InvalidRecipeError();
        recipes.put(recipe.getId(), recipe);
        recipeRepository.save(recipe);

        webSocketHub.broadcastRecipeUpdate(recipe.getId(), recipe);
        webSocketHub.broadcastTitleUpdate(getState());
    }

    @Override
    public void setIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
        ingredientRepository.save(ingredient);

        webSocketHub.broadcastTitleUpdate(getState());
    }

    @Override
    public void deleteRecipe(UUID recipeId) {
        if (recipes.remove(recipeId) != null) {
            recipeRepository.deleteById(recipeId);

            webSocketHub.broadcastRecipeDelete(recipeId);
            webSocketHub.broadcastTitleUpdate(getState());
        }
    }

    @Override
    public void deleteIngredient(UUID ingredientId) {
        if (ingredients.remove(ingredientId) != null) {
            ingredientRepository.deleteById(ingredientId);
        }
    }
}
