package server.api;

import commons.Ingredient;
import commons.InvalidRecipeError;
import commons.Recipe;
import commons.RecipeState;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.service.RecipeService;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/")
    public RecipeState getRecipeState() {
        return recipeService.getState();
    }

    @PostMapping("/")
    public void setRecipe(@RequestBody Recipe recipe) throws InvalidRecipeError {
        recipeService.setRecipe(recipe);
    }

    @PostMapping(path = "/ingredient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setIngredient(@RequestBody Ingredient ingredient) {
        recipeService.setIngredient(ingredient);
    }
}
