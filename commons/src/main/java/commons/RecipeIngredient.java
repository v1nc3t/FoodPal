package commons;

import java.util.Objects;
import java.util.UUID;

public class RecipeIngredient {
    private UUID ingredientRef;
    private Amount amount;

    public RecipeIngredient(UUID ingredientRef, Amount amount) {
        this.ingredientRef = ingredientRef;
        this.amount = amount;
    }

    public UUID getIngredientRef() {
        return ingredientRef;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "ingredientRef=" + getIngredientRef().toString() +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecipeIngredient that = (RecipeIngredient) obj;
        return ingredientRef == that.ingredientRef && amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientRef, amount);
    }
    
}
