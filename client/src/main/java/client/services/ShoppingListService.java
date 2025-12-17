package client.services;

import java.util.List;
import java.util.UUID;

import client.shoppingList.ShoppingList;
import client.shoppingList.ShoppingListItem;
import commons.Amount;
import commons.Recipe;
import commons.RecipeIngredient;

public class ShoppingListService {

    private final ShoppingList shoppingList;

    public ShoppingListService(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void addManualItem(UUID ingredientId, Amount amount) {
        shoppingList.addItem(
            new ShoppingListItem(ingredientId, amount, null)
        );
    }

    public void addRecipeItems(Recipe recipe) {
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        for (RecipeIngredient ingredient : ingredients) {
            shoppingList.addItem(
                new ShoppingListItem(
                    ingredient.getIngredientRef(),
                    ingredient.getAmount(),
                    recipe.getId()
                )
            );
        }
    }

    public void reset() {
        shoppingList.clear();
    }

}
