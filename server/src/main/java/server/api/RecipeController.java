package server.api;

import commons.Ingredient;
import commons.InvalidRecipeError;
import commons.Recipe;
import commons.RecipeState;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.service.IRecipeService;

import java.util.Collection;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final IRecipeService recipeService;
    /// Creates a Recipe RestController which uses the provided interface service
    /// for resolving requests.
    ///
    /// In runtime this interface will be injected by spring-boot
    public RecipeController(IRecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("")
    public RecipeState getRecipeState() {
        return recipeService.getState();
    }

    @GetMapping("/all")
    public Collection<Recipe> getAllRecipes() {
        return recipeService.getState().recipes();
    }

    @PostMapping("")
    public void setRecipe(@RequestBody Recipe recipe) throws InvalidRecipeError {
        recipeService.setRecipe(recipe);
    }

    @PostMapping(path = "/ingredient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setIngredient(@RequestBody Ingredient ingredient) {
        recipeService.setIngredient(ingredient);
    }
}
