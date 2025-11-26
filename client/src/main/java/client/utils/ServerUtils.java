package client.utils;

import commons.Ingredient;
import commons.Recipe;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.net.ConnectException;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils implements IServerUtils {

    public static final String SERVER = "http://localhost:8080/";

    public List<Ingredient> getIngredients() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/ingredients")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Ingredient>>() {
                });
    }

    public List<Recipe> getRecipes() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/recipes")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Recipe>>() {
                });
    }

    public Recipe addRecipe(Recipe recipe) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/recipes")
                .request(APPLICATION_JSON)
                .post(Entity.entity(recipe, APPLICATION_JSON), Recipe.class);
    }

    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(SERVER) //
                    .request(APPLICATION_JSON) //
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }
}