package client.services;

import client.config.ConfigManager;
import client.config.Config;
import client.shoppingList.ShoppingListItem;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShoppingListManagerTest {

    private ShoppingListManager shoppingListManager;
    private ConfigManager configManager;
    private RecipeManager recipeManager;

    @BeforeEach
    public void setup() {
        configManager = mock(ConfigManager.class);
        recipeManager = mock(RecipeManager.class);
        when(configManager.getConfig()).thenReturn(new Config());
        shoppingListManager = new ShoppingListManager(configManager, recipeManager);
    }

    @Test
    public void getRecipeItemsWithoutScaling() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        RecipeIngredient ri = new RecipeIngredient(ingredientId, new Amount(100, Unit.GRAM));
        Recipe recipe = new Recipe("Test Recipe", List.of(ri), List.of(), 2, Language.EN);
        recipe.id = recipeId;

        when(recipeManager.isScaled(recipeId)).thenReturn(false);

        List<ShoppingListItem> items = shoppingListManager.getRecipeItems(recipe);

        assertEquals(1, items.size());
        assertEquals(100.0, items.get(0).getAmount().quantity(), 0.001);
    }

    @Test
    public void getRecipeItemsWithScaling() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        RecipeIngredient ri = new RecipeIngredient(ingredientId, new Amount(100, Unit.GRAM));
        Recipe recipe = new Recipe("Test Recipe", List.of(ri), List.of(), 2, Language.EN);
        recipe.id = recipeId;

        // Scaled up by 2 portions (total 4 portions, so 2x amount)
        when(recipeManager.isScaled(recipeId)).thenReturn(true);
        when(recipeManager.getRecipeScale(recipeId)).thenReturn(2);

        List<ShoppingListItem> items = shoppingListManager.getRecipeItems(recipe);

        assertEquals(1, items.size());
        // Original 100g for 2 portions. Scaled to 4 portions -> 0.2kg (normalized).
        // Scale factor = (2 + 2) / 2 = 2.
        // ri.getAmount().scaleAndNormalize(2) -> 100g * 2 = 200g -> 0.2kg.
        assertEquals(0.2, items.get(0).getAmount().quantity(), 0.001);
    }
}
