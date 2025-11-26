package server.service;

import commons.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeServiceTest {
    @Test
    public void addIngredient() {
        RecipeService recipeService = new RecipeService();
        var yogurt = new Ingredient("Yogurt", new NutritionValues(1, 2, 3));
        recipeService.setIngredient(yogurt);
        assertTrue(recipeService.getState().recipes().isEmpty());
        assertEquals(1, recipeService.getState().ingredients().size());
        var firstIngredient = recipeService
                .getState()
                .ingredients()
                .stream()
                .findFirst();
        assertTrue(firstIngredient.isPresent());
        assertEquals(yogurt, firstIngredient.get());

    }
    @Test
    public void recipeNull() {
        RecipeService recipeService = new RecipeService();
        assertThrows(InvalidRecipeError.class, () -> recipeService.setRecipe(null));
    }
    @Test
    public void recipeInvalidIngredient() {
        RecipeService recipeService = new RecipeService();
        var recipe = new Recipe(
                "Evil yogurt",
                List.of(
                        new RecipeIngredient(
                                UUID.randomUUID(),
                                new FormalAmount(
                                        10,
                                        Unit.GRAM)
                        )
                ),
                List.of(),
                67
        );
        assertThrows(InvalidRecipeError.class, () ->
                recipeService.setRecipe(recipe)
        );
    }
    @Test
    public void addRecipe() throws InvalidRecipeError {
        RecipeService recipeService = new RecipeService();
        var yogurt = new Ingredient("Yogurt", new NutritionValues(1, 2, 3));
        var sugar  = new Ingredient("Sugar", new NutritionValues(1, 2, 3));
        recipeService.setIngredient(yogurt);
        recipeService.setIngredient(sugar);
        var recipe =  new Recipe(
                "Sweet Yogurt",
                List.of(
                        new RecipeIngredient(
                                yogurt.getId(),
                                new FormalAmount(
                                        10,
                                        Unit.GRAM)
                        ),
                        new RecipeIngredient(
                                sugar.getId(),
                                new InformalAmount(
                                        "A bucket"
                                )
                        )
                ),
                List.of(
                    "Get sugar",
                    "Get yogurt",
                    "Mix"
                ),
                67
        );
        recipeService.setRecipe(recipe);
        assertEquals(1, recipeService.getState().recipes().size());
        assertEquals(2, recipeService.getState().ingredients().size());
        var firstRecipe =   recipeService
                .getState()
                .recipes()
                .stream()
                .findFirst();
        assertTrue(firstRecipe.isPresent());
        assertEquals(2, firstRecipe.get().getIngredients().size());
        assertTrue(firstRecipe
                .get()
                .getIngredients()
                .stream()
                .anyMatch(v -> v.getIngredientRef() == yogurt.getId()));
        assertTrue(firstRecipe
                .get()
                .getIngredients()
                .stream()
                .anyMatch(v -> v.getIngredientRef() == sugar.getId()));
        assertEquals(recipe, firstRecipe.get());
    }
}
