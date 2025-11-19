package commons;

import java.util.Objects;

public class RecipeIngredient {
    private int ingredientRef;
    private Amount amount;

    public RecipeIngredient(int ingredientRef, Amount amount) {
        this.ingredientRef = ingredientRef;
        this.amount = amount;
    }

    public int getIngredientRef() {
        return ingredientRef;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "ingredientRef=" + ingredientRef +
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
