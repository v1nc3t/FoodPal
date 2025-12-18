package client.scenes;

import commons.Language;
import commons.Recipe;

import java.util.Optional;
import java.util.UUID;

public record ListObject(UUID id, String name, Optional<Language> language) {
    public static ListObject fromRecipe(Recipe recipe) {
        return new ListObject(recipe.getId(), recipe.getTitle(), Optional.of(recipe.getLanguage()));
    }
}
