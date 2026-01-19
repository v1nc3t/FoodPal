package server.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Ingredient;
import commons.Language;
import commons.NutritionValues;
import commons.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

// AI has been used for the @SpringBootTest because I couldn't figure it out.
// so  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//     @LocalServerPort
//    int port;
//    String WEB_SOCKET_URL = "ws://localhost:"+port+"/ws";
//Were helped by AI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    @LocalServerPort
    int port;

    @Autowired
    private WebSocketHub hub;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSubscribeToRecipe() throws Exception {
        // use two futures: confirming subscription and receiving updates
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> dataUpdate = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    subConfirmation.complete(payload);
                } else if (payload.contains("UPDATE")) {
                    dataUpdate.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        // subscribe to a recipe
        UUID recipeId = UUID.randomUUID();
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "recipe");
        subRequest.put("recipeId", recipeId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        // check if confirmation is got
        String subResult = subConfirmation.get(2, TimeUnit.SECONDS);
        assertTrue(subResult.contains("SUBSCRIBED"));

        // update on server, simulating other users editing a whole recipe
        Recipe updatedRecipe = new Recipe(recipeId, "Test Recipe Change", List.of(), List.of(), 2, Language.EN);
        hub.broadcastRecipeUpdate(recipeId, updatedRecipe);

        // check if the client got the notification
        String updateResult = dataUpdate.get(5, TimeUnit.SECONDS);
        assertTrue(updateResult.contains("UPDATE"), "Client should receive an UPDATE message");

        String expectedRecipeJson = mapper.writeValueAsString(updatedRecipe);
        assertTrue(updateResult.contains(expectedRecipeJson), "Message should contain the new recipe data");

        session.close();
    }

    @Test
    public void testUnsubscribeFromRecipe() throws Exception {
        CompletableFuture<String> unsubConfirmation = new CompletableFuture<>();
        CompletableFuture<String> shouldNotReceive = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("UNSUBSCRIBE")) {
                    unsubConfirmation.complete(payload);
                } else if (payload.contains("UPDATE")) {
                    // if we get an update after unsubscribing, this means it fails
                    shouldNotReceive.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        UUID recipeId = UUID.randomUUID();

        // subscribe
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "recipe");
        subRequest.put("recipeId", recipeId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        // unsubscribe
        Map<String, Object> unsubRequest = new HashMap<>();
        unsubRequest.put("type", "UNSUBSCRIBE");
        unsubRequest.put("topic", "recipe");
        unsubRequest.put("recipeId", recipeId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(unsubRequest)));

        // check for unsubscribe confirmation
        String unsubResult = unsubConfirmation.get(5, TimeUnit.SECONDS);
        assertTrue(unsubResult.contains("UNSUBSCRIBE"));

        // broadcast on server
        Recipe updatedRecipe = new Recipe(recipeId, "Test Recipe Change", List.of(), List.of(), 2, Language.EN);
        hub.broadcastRecipeUpdate(recipeId, updatedRecipe);

        boolean received = true;
        try {
            shouldNotReceive.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            received = false;
        }
        assertFalse(received, "Client should not get updates after unsubscribing from a recipe");

        session.close();
    }

    @Test
    public void testSubscribeToIngredient() throws Exception {
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> dataUpdate = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    subConfirmation.complete(payload);
                } else if (payload.contains("UPDATE")) {
                    dataUpdate.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        // subscribe to an ingredient
        UUID ingredientId = UUID.randomUUID();
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "ingredient");
        subRequest.put("ingredientId", ingredientId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        // check if confirmation is got
        String subResult = subConfirmation.get(5, TimeUnit.SECONDS);
        assertTrue(subResult.contains("SUBSCRIBED"));

        // update on server, simulating other users editing an ingredient
        Ingredient updatedIngredient = new Ingredient(ingredientId, "Test Ingredient Change", new NutritionValues(0, 0, 0));
        hub.broadcastIngredientUpdate(ingredientId, updatedIngredient);

        // check if the client got the notification
        String updateResult = dataUpdate.get(5, TimeUnit.SECONDS);
        assertTrue(updateResult.contains("UPDATE"), "Client should receive an UPDATE message");

        String expectedIngredientJson = mapper.writeValueAsString(updatedIngredient);
        assertTrue(updateResult.contains(expectedIngredientJson), "Message should contain the new ingredient data");

        session.close();
    }

    @Test
    public void testUnsubscribeFromIngredient() throws Exception {
        CompletableFuture<String> unsubConfirmation = new CompletableFuture<>();
        CompletableFuture<String> shouldNotReceive = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("UNSUBSCRIBE")) {
                    unsubConfirmation.complete(payload);
                } else if (payload.contains("UPDATE")) {
                    shouldNotReceive.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        UUID ingredientId = UUID.randomUUID();

        // subscribe
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "ingredient");
        subRequest.put("ingredientId", ingredientId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        // unsubscribe
        Map<String, Object> unsubRequest = new HashMap<>();
        unsubRequest.put("type", "UNSUBSCRIBE");
        unsubRequest.put("topic", "ingredient");
        unsubRequest.put("ingredientId", ingredientId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(unsubRequest)));

        // check for unsubscribe confirmation
        String unsubResult = unsubConfirmation.get(5, TimeUnit.SECONDS);
        assertTrue(unsubResult.contains("UNSUBSCRIBE"));

        // broadcast an ingredient change on the server
        Ingredient updatedIngredient = new Ingredient(ingredientId, "Test Ingredient Change", new NutritionValues(0, 0, 0));
        hub.broadcastIngredientUpdate(ingredientId, updatedIngredient);

        boolean received = true;
        try {
            shouldNotReceive.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            received = false;
        }
        assertFalse(received, "Client should not get updates after unsubscribing from an ingredient");

        session.close();
    }

    @Test
    public void testCleanupOnDisconnect() throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketSession session = client.execute(new TextWebSocketHandler(), url).get(5, TimeUnit.SECONDS);

        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "recipe");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        Thread.sleep(3000);

        session.close();

        Thread.sleep(3000);

        assertEquals(0, hub.getTitleSubscribersCount());
    }

    @Test
    public void testRecipeDeleteNotification() throws Exception {
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> deleteNotification = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    subConfirmation.complete(payload);
                } else if (payload.contains("DELETE")) {
                    deleteNotification.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        UUID recipeId = UUID.randomUUID();

        // Subscribe to recipe
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "recipe");
        subRequest.put("recipeId", recipeId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        // Wait for confirmation
        subConfirmation.get(5, TimeUnit.SECONDS);

        // Simulate deletion on server
        hub.broadcastRecipeDelete(recipeId);

        // Check for delete notification
        String result = deleteNotification.get(5, TimeUnit.SECONDS);
        assertTrue(result.contains("DELETE"));
        assertTrue(result.contains(recipeId.toString()));

        session.close();
    }

    @Test
    public void testStateUpdate() throws Exception {
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> stateUpdate = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    subConfirmation.complete(payload);
                } else if (payload.contains("UPDATE") && payload.contains("recipe-state")) {
                    stateUpdate.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);

        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "recipe-state");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));

        subConfirmation.get(5, TimeUnit.SECONDS);

        hub.broadcastStateUpdate("New state after recipe state change");

        String result = stateUpdate.get(5, TimeUnit.SECONDS);
        assertTrue(result.contains("UPDATE"));
        assertTrue(result.contains("recipe-state"));
        assertTrue(result.contains("New state after recipe state change"));

        session.close();
    }
    @Test
    public void testIngredientDeleteNotification() throws Exception {
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> deleteNotification = new CompletableFuture<>();
        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    subConfirmation.complete(payload);
                } else if (payload.contains("DELETE")) {
                    deleteNotification.complete(payload);
                }
            }
        };
        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, url).get(5, TimeUnit.SECONDS);
        UUID ingredientId = UUID.randomUUID();
        Map<String, Object> subRequest = new HashMap<>();
        subRequest.put("type", "SUBSCRIBE");
        subRequest.put("topic", "ingredient");
        subRequest.put("ingredientId", ingredientId.toString());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subRequest)));
        subConfirmation.get(5, TimeUnit.SECONDS);
        hub.broadcastIngredientDelete(ingredientId);
        String result = deleteNotification.get(5, TimeUnit.SECONDS);
        assertTrue(result.contains("DELETE"));
        assertTrue(result.contains("ingredient"));
        assertTrue(result.contains(ingredientId.toString()));

        session.close();
    }
    @Test
    public void testIngredientStateUpdate() throws Exception {
        CompletableFuture<String> confirmation = new CompletableFuture<>();
        CompletableFuture<String> update = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                if (payload.contains("SUBSCRIBED")) {
                    confirmation.complete(payload);
                } else if (payload.contains("ingredient-state")) {
                    update.complete(payload);
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        WebSocketSession session = new StandardWebSocketClient()
                .execute(handler, url).get(5, TimeUnit.SECONDS);

        Map<String, Object> sub = new HashMap<>();
        sub.put("type", "SUBSCRIBE");
        sub.put("topic", "ingredient-state");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(sub)));

        confirmation.get(5, TimeUnit.SECONDS);

        hub.broadcastIngredientStateUpdate(List.of("test"));

        String result = update.get(5, TimeUnit.SECONDS);
        assertTrue(result.contains("ingredient-state"));

        session.close();
    }
    @Test
    public void testUnsubscribeIngredientState() throws Exception {

        CompletableFuture<String> subscribed = new CompletableFuture<>();
        CompletableFuture<String> forbiddenUpdate = new CompletableFuture<>();

        AtomicBoolean unsubscribed = new AtomicBoolean(false);

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NonNull WebSocketSession session,
                                             TextMessage message) {
                try {
                    JsonNode json = mapper.readTree(message.getPayload());
                    String type = json.get("type").asText();
                    String topic = json.path("topic").asText("");

                    // subscription confirmation
                    if ("SUBSCRIBED".equals(type)
                            && "ingredient-state".equals(topic)) {
                        subscribed.complete(message.getPayload());
                    }

                    else if ("UNSUBSCRIBED".equals(type)
                            && "ingredient-state".equals(topic)) {
                        unsubscribed.set(true);
                    }

                    else if (unsubscribed.get()
                            && "UPDATE".equals(type)
                            && "ingredient-state".equals(topic)) {
                        forbiddenUpdate.complete(message.getPayload());
                    }

                } catch (Exception ignored) {
                }
            }
        };

        String url = "ws://localhost:" + port + "/ws";
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session =
                client.execute(handler, url).get(5, TimeUnit.SECONDS);

        Map<String, Object> subscribe = new HashMap<>();
        subscribe.put("type", "SUBSCRIBE");
        subscribe.put("topic", "ingredient-state");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(subscribe)));

        subscribed.get(5, TimeUnit.SECONDS);

        Map<String, Object> unsubscribe = new HashMap<>();
        unsubscribe.put("type", "UNSUBSCRIBE");
        unsubscribe.put("topic", "ingredient-state");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(unsubscribe)));

        Thread.sleep(300);

        hub.broadcastIngredientStateUpdate(List.of("test-ingredient"));

        boolean receivedForbiddenUpdate = true;
        try {
            forbiddenUpdate.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            receivedForbiddenUpdate = false;
        }

        assertFalse(receivedForbiddenUpdate,
                "Client should NOT receive ingredient-state UPDATE after unsubscribe");

        session.close();
    }


}