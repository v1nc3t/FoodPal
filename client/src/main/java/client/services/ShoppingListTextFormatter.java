package client.services;

import client.shoppingList.ShoppingListItem;
import commons.Ingredient;
import commons.RecipeIngredient;

import java.util.List;

public class ShoppingListTextFormatter {

    public static String toText(
            List<ShoppingListItem> items,
            RecipeManager recipeManager
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("Shopping List\n\n");

        for (ShoppingListItem item : items) {
            sb.append("- ");

            if (item.getCustomName() != null) {
                sb.append(item.getCustomName());
            } else {
                Ingredient ing = recipeManager.getIngredient(
                        new RecipeIngredient(item.getIngredientId(), item.getAmount())
                );
                sb.append(ing != null ? ing.getName() : "Unknown Ingredient");
            }

            if (item.getAmount() != null) {
                sb.append(" (")
                        .append(item.getAmount().toPrettyString())
                        .append(")");
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}
