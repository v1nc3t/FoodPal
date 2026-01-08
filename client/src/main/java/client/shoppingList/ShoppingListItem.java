package client.shoppingList;

import commons.Amount;
import java.util.UUID;

public class ShoppingListItem {
    private UUID ingredientId; // Can be null for manual items
    private String customName; // Used for manual items
    private Amount amount;
    private UUID sourceRecipeId; // Can be null if the item was added manually

    public ShoppingListItem(UUID ingredientId, Amount amount, UUID sourceRecipeId) {
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.sourceRecipeId = sourceRecipeId;
    }

    public ShoppingListItem(String customName, Amount amount) {
        this.customName = customName;
        this.amount = amount;
        this.ingredientId = null;
        this.sourceRecipeId = null;
    }

    public ShoppingListItem(UUID ingredientId, Amount amount) {
        this(ingredientId, amount, null);
    }

    public ShoppingListItem() {
    }

    public UUID getIngredientId() {
        return ingredientId;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
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
                ", customName='" + customName + '\'' +
                ", amount=" + amount +
                ", sourceRecipeId=" + sourceRecipeId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ShoppingListItem that = (ShoppingListItem) obj;

        boolean sameIngredient = (ingredientId == null && that.ingredientId == null) ||
                (ingredientId != null && ingredientId.equals(that.ingredientId));
        boolean sameName = (customName == null && that.customName == null) ||
                (customName != null && customName.equals(that.customName));

        return sameIngredient && sameName &&
                amount.equals(that.amount) &&
                ((sourceRecipeId == null && that.sourceRecipeId == null)
                        || (sourceRecipeId != null && sourceRecipeId.equals(that.sourceRecipeId)));
    }

    @Override
    public int hashCode() {
        int result = (ingredientId != null ? ingredientId.hashCode() : 0);
        result = 31 * result + (customName != null ? customName.hashCode() : 0);
        result = 31 * result + amount.hashCode();
        result = 31 * result + (sourceRecipeId != null ? sourceRecipeId.hashCode() : 0);
        return result;
    }
}
