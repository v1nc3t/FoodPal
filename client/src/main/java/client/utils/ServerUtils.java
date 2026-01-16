package client.utils;

import client.config.ConfigManager;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeState;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.net.ConnectException;
import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils implements IServerUtils {

    // public static final String SERVER = "http://localhost:8080/";

    private final ConfigManager configManager;
    private final Client client;

    @Inject
    public ServerUtils(ConfigManager configManager) {
        this.configManager = configManager;
        this.client = ClientBuilder.newClient(new ClientConfig());
    }

    private String getServerURL() {
        return configManager.getConfig().getServerAddress();
    }

    public List<Recipe> getRecipes() {
        return client.target(getServerURL())
                .path("api/recipes/all")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Recipe>>() {});
    }

    public List<Ingredient> getIngredients() {
        RecipeState state = client.target(getServerURL())
                .path("api/recipes")
                .request(APPLICATION_JSON)
                .get(RecipeState.class);
        return state.ingredients().stream().toList();
    }

    public Recipe setRecipe(Recipe recipe) {
        return client.target(getServerURL())
                .path("api/recipes")
                .request(APPLICATION_JSON)
                .post(Entity.entity(recipe, APPLICATION_JSON), Recipe.class);
    }

    public Ingredient setIngredient(Ingredient ingredient) {
        return client.target(getServerURL())
                .path("api/recipes/ingredient")
                .request(APPLICATION_JSON)
                .post(Entity.entity(ingredient, APPLICATION_JSON), Ingredient.class);
    }

    public void removeRecipe(UUID reciepId) {
        client.target(getServerURL())
                .path("api/recipes/" + reciepId)
                .request(APPLICATION_JSON)
                .delete();
    }

    public void removeIngredient(UUID ingredientId) {
        client.target(getServerURL())
                .path("api/recipes/ingredient/" + ingredientId)
                .request(APPLICATION_JSON)
                .delete();
    }

    public boolean isServerAvailable() {
        try {
            client.target(getServerURL())
                    .request(APPLICATION_JSON)
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }
}