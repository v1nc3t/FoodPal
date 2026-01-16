package server.service;

import commons.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.IngredientRepository;
import server.database.RecipeRepository;
import server.websocket.WebSocketHub;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
        @Mock
        private RecipeRepository recipeRepository;
        @Mock
        private IngredientRepository ingredientRepository;
        @Mock
        private WebSocketHub webSocketHub;
        @InjectMocks
        private RecipeService recipeService;

        Ingredient yogurt = new Ingredient("Yogurt", new NutritionValues(1, 2, 3));
        Ingredient sugar = new Ingredient("Sugar", new NutritionValues(1, 2, 3));
        Recipe sugaredYogurt = new Recipe(
                        "Sweet Yogurt",
                        List.of(
                                        new RecipeIngredient(
                                                        yogurt.getId(),
                                                        new Amount(10, Unit.GRAM)),
                                        new RecipeIngredient(
                                                        sugar.getId(),
                                                        new Amount(1, "A bucket"))),
                        List.of(
                                        "Get sugar",
                                        "Get yogurt",
                                        "Mix"),
                        67,
                        Language.EN);

        @Test
        public void addIngredient() throws InvalidIngredientError {
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
                assertThrows(InvalidRecipeError.class, () -> recipeService.setRecipe(null));
        }

        @Test
        public void ingredientNull() {
            assertThrows(InvalidIngredientError.class, () -> recipeService.setIngredient(null));
        }

        @Test
        public void recipeInvalidIngredient() {
                var recipe = new Recipe(
                                "Evil yogurt",
                                List.of(
                                                new RecipeIngredient(
                                                                UUID.randomUUID(),
                                                                new Amount(
                                                                                10,
                                                                                Unit.GRAM))),
                                List.of(),
                                67,
                                Language.EN);
                assertThrows(InvalidRecipeError.class, () -> recipeService.setRecipe(recipe));
        }

        @Test
        public void addRecipe() throws InvalidRecipeError, InvalidIngredientError {
            recipeService.setIngredient(yogurt);
            recipeService.setIngredient(sugar);
            recipeService.setRecipe(sugaredYogurt);
            assertEquals(1, recipeService.getState().recipes().size());
            assertEquals(2, recipeService.getState().ingredients().size());
            var firstRecipe = recipeService
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
            assertEquals(sugaredYogurt, firstRecipe.get());
        }

    @Test
        public void loadRecipes() throws InvalidRecipeError, InvalidIngredientError {
            when(ingredientRepository.findAll()).thenReturn(
                    List.of(yogurt, sugar));
            when(recipeRepository.findAll()).thenReturn(
                    List.of(sugaredYogurt));
            var loadedService = new RecipeService(recipeRepository, ingredientRepository, webSocketHub);

            recipeService.setIngredient(yogurt);
            recipeService.setIngredient(sugar);
            recipeService.setRecipe(sugaredYogurt);

            assertEquals(recipeService.getState(), loadedService.getState());
        }

        @Test
        public void deleteRecipe() throws InvalidRecipeError, InvalidIngredientError {
                recipeService.setIngredient(yogurt);
                recipeService.setIngredient(sugar);
                recipeService.setRecipe(sugaredYogurt);

                recipeService.deleteRecipe(sugaredYogurt.getId());

                assertTrue(recipeService.getState().recipes().isEmpty());
                verify(recipeRepository).deleteById(sugaredYogurt.getId());
                verify(webSocketHub).broadcastRecipeDelete(sugaredYogurt.getId());
                verify(webSocketHub, atLeastOnce()).broadcastStateUpdate(any());
        }

        @Test
        public void deleteNonExistentRecipe() {
                UUID randomId = UUID.randomUUID();
                recipeService.deleteRecipe(randomId);

                // Verify that no repository or broadcast calls happened because the recipe
                // wasn't found
                verify(recipeRepository, never()).deleteById(any());
                verify(webSocketHub, never()).broadcastRecipeDelete(any());
        }

        @Test
        public void deleteIngredient() throws InvalidIngredientError {
                recipeService.setIngredient(yogurt);
                recipeService.setIngredient(sugar);

                recipeService.deleteIngredient(yogurt.getId());
                recipeService.deleteIngredient(sugar.getId());

                assertTrue(recipeService.getState().ingredients().isEmpty());
                verify(ingredientRepository).deleteById(yogurt.getId());
                verify(ingredientRepository).deleteById(sugar.getId());
        }

        @Test
        public void deleteNonExistentIngredient() {
            UUID randomId = UUID.randomUUID();
            recipeService.deleteIngredient(randomId);

            verify(ingredientRepository, never()).deleteById(any());
        }
}
