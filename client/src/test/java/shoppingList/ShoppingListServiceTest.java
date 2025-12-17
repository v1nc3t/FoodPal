package shoppingList;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.services.ShoppingListService;
import client.shoppingList.ShoppingList;
import client.shoppingList.ShoppingListItem;
import commons.Amount;
import commons.Ingredient;
import commons.NutritionValues;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;

public class ShoppingListServiceTest {

    private ShoppingList shoppingList;
    private ShoppingListService service;

    @BeforeEach
    public void setup() {
        shoppingList = new ShoppingList();
        service = new ShoppingListService(shoppingList);
    }

    @Test
    public void addManualItemAddsItemWithNullRecipe() {
        UUID ingredientId = UUID.randomUUID();
        Amount amount = new Amount(2.0, Unit.CUP);

        service.addManualItem(ingredientId, amount);

        assertEquals(1, shoppingList.getItems().size());

        ShoppingListItem item = shoppingList.getItems().get(0);
        assertEquals(ingredientId, item.getIngredientId());
        assertEquals(amount, item.getAmount());
        assertNull(item.getSourceRecipeId());
    }

    @Test
    public void addRecipeItemsAddsAllIngredients() {
        NutritionValues nutrition = new NutritionValues(0.0, 0.0, 76.0);
        Ingredient ingredient1 = new Ingredient("Flour", nutrition);
        Ingredient ingredient2 = new Ingredient("Sugar", nutrition);

        RecipeIngredient ri1 =
            new RecipeIngredient(ingredient1.getId(), new Amount(1.0, Unit.CUP));
        RecipeIngredient ri2 =
            new RecipeIngredient(ingredient2.getId(), new Amount(2.0, Unit.CUP));

        Recipe recipe =
            new Recipe(
                "Cake",
                List.of(ri1, ri2),
                List.of("Mix", "Bake"),
                4
            );

        service.addRecipeItems(recipe);

        assertEquals(2, shoppingList.getItems().size());

        for (ShoppingListItem item : shoppingList.getItems()) {
            assertEquals(recipe.getId(), item.getSourceRecipeId());
        }
    }

    @Test
    public void resetClearsShoppingList() {
        service.addManualItem(UUID.randomUUID(), new Amount(1.0, Unit.CUP));
        assertFalse(shoppingList.getItems().isEmpty());

        service.reset();

        assertTrue(shoppingList.getItems().isEmpty());
    }

}
