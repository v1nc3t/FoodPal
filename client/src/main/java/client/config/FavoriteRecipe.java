package client.config;

import java.util.UUID;

/**
 * A record that keeps the favorite recipe
 * @param id the id of the recipe
 * @param name the last updated name of the recipe
 */
public record FavoriteRecipe(UUID id, String name) {
}
