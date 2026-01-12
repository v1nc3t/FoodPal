package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RecipeIngredient {
    public UUID ingredientRef;
    public Amount amount;

    // an empty constructor for object mappers
    public RecipeIngredient() {

    }

    @JsonCreator
    public RecipeIngredient(@JsonProperty("ingredientRef") UUID ingredientRef,
                            @JsonProperty("amount") Amount amount) {
        this.ingredientRef = ingredientRef;
        this.amount = amount;
    }

    public RecipeIngredient(RecipeIngredient other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot copy null RecipeIngredient");
        }
        this.ingredientRef = other.ingredientRef;
        this.amount = other.amount;
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
        return ingredientRef.equals(that.ingredientRef) && amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientRef, amount);
    }
    public RecipeIngredient scale(double factor) {
        return new RecipeIngredient(
                this.ingredientRef,
                this.amount.scale(factor)
        );
    }
    public double getGrams() {
        return amount.toGrams();
    }

}
