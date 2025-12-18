package client.shoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import commons.Amount;
import commons.Recipe;
import commons.RecipeIngredient;

public class ShoppingList {
    private List<ShoppingListItem> items = new ArrayList<>();

    public void addItem(ShoppingListItem item) {
        items.add(item);
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    public void addManualItem(UUID ingredientId, Amount amount) {
        items.add(new ShoppingListItem(ingredientId, amount, null));
    }

    public void addRecipeItems(Recipe recipe) {
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        for (RecipeIngredient ingredient : ingredients) {
            items.add(new ShoppingListItem(
                ingredient.getIngredientRef(),
                ingredient.getAmount(),
                recipe.getId()
            ));
        }
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShoppingList that = (ShoppingList) obj;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
