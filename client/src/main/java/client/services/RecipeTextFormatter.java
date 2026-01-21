package client.services;

import commons.Amount;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;

import java.util.UUID;
import java.util.function.Function;

public class RecipeTextFormatter {

    public static String toText(Recipe recipe, Function<UUID, String> ingredientNameResolver) {
        StringBuilder sb = new StringBuilder();

        sb.append(recipe.getTitle()).append("\n\n");

        sb.append("Ingredients:\n");
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                Amount amount = ingredient.getAmount();
                String name = ingredientNameResolver.apply(ingredient.getIngredientRef());

                if (amount != null && amount.unit() != null) {
                    sb.append("- ")
                            .append(name).append(" ")
                            .append(formatQuantity(amount.quantity())).append(" ")
                            .append(formatUnit(amount.unit()))
                            .append("\n");
                } else if (amount != null) {
                    sb.append("- ")
                            .append(name).append(" ")
                            .append(amount.description())
                            .append("\n");
                }
            }
        }

        sb.append("\nPreparation:\n");
        if (recipe.getSteps() != null) {
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                sb.append(i + 1)
                        .append(". ")
                        .append(recipe.getSteps().get(i))
                        .append("\n");
            }
        }

        return sb.toString();
    }

    private static String formatQuantity(double q) {
        if (q == Math.floor(q)) {
            return String.valueOf((int) q);
        }
        return String.format("%.1f", q);
    }

    private static String formatUnit(Unit unit) {
        return switch (unit) {
            case KILOGRAM -> "kg";
            case GRAM -> "g";
            case LITER -> "l";
            case MILLILITER -> "ml";
            default -> unit.name().toLowerCase();
        };
    }
}
