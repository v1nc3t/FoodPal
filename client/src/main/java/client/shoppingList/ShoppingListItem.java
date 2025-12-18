package client.shoppingList;

import commons.Amount;
import java.util.UUID;

public class ShoppingListItem {
    private UUID ingredientId;
    private Amount amount;
    private UUID sourceRecipeId; // Can be null if the item was added manually

    public ShoppingListItem(UUID ingredientId, Amount amount, UUID sourceRecipeId) {
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.sourceRecipeId = sourceRecipeId;
    }

    public ShoppingListItem(UUID ingredientId, Amount amount) {
        this(ingredientId, amount, null);
    }

    public ShoppingListItem() {
    }

    public UUID getIngredientId() {
        return ingredientId;
    }

    public Amount getAmount() {
        return amount;
    }

    public UUID getSourceRecipeId() {
        return sourceRecipeId;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ShoppingListItem{" +
                "ingredientId=" + ingredientId +
                ", amount=" + amount +
                ", sourceRecipeId=" + sourceRecipeId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShoppingListItem that = (ShoppingListItem) obj;
        return ingredientId.equals(that.ingredientId) &&
               amount.equals(that.amount) &&
               ((sourceRecipeId == null && that.sourceRecipeId == null) || (sourceRecipeId != null && sourceRecipeId.equals(that.sourceRecipeId)));
    }

    @Override
    public int hashCode() {
        int result = ingredientId.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + (sourceRecipeId != null ? sourceRecipeId.hashCode() : 0);
        return result;
    }
}
