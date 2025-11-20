package commons;

import java.util.Collection;

/// A state snapshot of all `Recipe`'s and `Ingredient`'s
public record RecipeState(Collection<Recipe> recipes, Collection<Ingredient> ingredients) {
}
