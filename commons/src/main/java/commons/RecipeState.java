package commons;

import java.util.Collection;
import java.util.HashSet;

/// A state snapshot of all `Recipe`'s and `Ingredient`'s
public record RecipeState(Collection<Recipe> recipes, Collection<Ingredient> ingredients) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeState that = (RecipeState) o;

        // check if all .recipes are in that.recipes, and vice-versa
        // + same with .ingredients and that.ingredients
        return new HashSet<>(recipes).equals(new HashSet<>(that.recipes)) &&
                new HashSet<>(ingredients).equals(new HashSet<>(that.ingredients));
    }
}
