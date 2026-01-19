package server.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;


@Component
public class RecipeWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketHub hub;
    private final ObjectMapper mapper;

    public RecipeWebSocketHandler(WebSocketHub hub, ObjectMapper mapper) {
        this.hub = hub;
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("debug: New user joined. Session ID: " + session.getId());
    }

    @Override
    protected void handleTextMessage(
            @NonNull WebSocketSession session,
            TextMessage message) throws Exception {

        String payload = message.getPayload();
        JsonNode json;

        try {
            json = mapper.readTree(payload);
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid json format");
            return;
        }

        String typeString = json.path("type").asText("");
        String topic = json.path("topic").asText("");

        WebSocketTypes type;
        try{
            type = WebSocketTypes.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(session, "unknown type:" + typeString);
            return;
        }

        if (type == WebSocketTypes.SUBSCRIBE) {
            handleSubscription(session, topic, json);
        } else if (type == WebSocketTypes.UNSUBSCRIBE) {
            handleUnsubscription(session, topic, json);
        } else {
            sendErrorMessage(session, "Command not supported: " + typeString);
        }
    }

    private void handleSubscription(
            WebSocketSession session,
            String topic,
            JsonNode json) throws Exception {

        switch (topic) {
            case "recipe-state" -> {
                hub.subscribeState(session);
                sendSubscribeConfirm(session, "recipe-state");
            }
            case "recipe" -> {
                String idStr = json.path("recipeId").asText(null);
                if (idStr == null || idStr.isEmpty()) {
                    sendErrorMessage(session, "recipeId is missing for subscription");
                } else {
                    try {
                        UUID recipeId = UUID.fromString(idStr);
                        hub.subscribeRecipe(session, recipeId);
                        sendSubscribeConfirm(session, "recipe");
                    } catch (IllegalArgumentException e) {
                        sendErrorMessage(session, "recipeId is not a valid UUID format");
                    }
                }
            }
            case "ingredient" -> {
                String idStr = json.path("ingredientId").asText(null);
                if (idStr == null || idStr.isEmpty()) {
                    sendErrorMessage(session, "ingredientId is missing for subscription");
                } else {
                    try {
                        UUID ingredientId = UUID.fromString(idStr);
                        hub.subscribeIngredient(session, ingredientId);
                        sendSubscribeConfirm(session, "ingredient");
                    } catch (IllegalArgumentException e) {
                        sendErrorMessage(session, "ingredientId is not a valid UUID format");
                    }
                }
            }
            case "ingredient-state" -> {
                hub.subscribeIngredientState(session);
                sendSubscribeConfirm(session, "ingredient-state");
            }

            default -> sendErrorMessage(session, "Unknown topic: " + topic);
        }
    }

    private void handleUnsubscription(
            WebSocketSession session,
            String topic,
            JsonNode json) throws Exception {

        switch (topic) {
            case "recipe-state" -> {
                hub.unsubscribeState(session);
                sendUnsubscribeConfirm(session, "recipe-state");
            }
            case "recipe" -> {
                String idStr = json.path("recipeId").asText(null);
                if (idStr == null || idStr.isEmpty()) {
                    sendErrorMessage(session, "recipeId is missing for unsubscription");
                } else {
                    try {
                        UUID recipeId = UUID.fromString(idStr);
                        hub.unsubscribeRecipe(session, recipeId);
                        sendUnsubscribeConfirm(session, "recipe");
                    } catch (IllegalArgumentException e) {
                        sendErrorMessage(session, "recipeId is not a valid UUID format");
                    }
                }
            }
            case "ingredient" -> {
                String idStr = json.path("ingredientId").asText(null);
                if (idStr == null || idStr.isEmpty()) {
                    sendErrorMessage(session, "ingredientId is missing for unsubscription");
                } else {
                    try {
                        UUID ingredientId = UUID.fromString(idStr);
                        hub.unsubscribeIngredient(session, ingredientId);
                        sendUnsubscribeConfirm(session, "ingredient");
                    } catch (IllegalArgumentException e) {
                        sendErrorMessage(session, "ingredientId is not a valid UUID format");
                    }
                }
            }
            case "ingredient-state" -> {
                hub.unsubscribeIngredientState(session);
                sendUnsubscribeConfirm(session, "ingredient-state");
            }

            default -> sendErrorMessage(session, "Unknown topic: " + topic);
        }
    }

    private void sendSubscribeConfirm(WebSocketSession session, String topic) throws Exception {
        WebSocketResponse response = new WebSocketResponse(WebSocketTypes.SUBSCRIBED, topic, null);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
    }

    private void sendUnsubscribeConfirm(WebSocketSession session, String topic) throws Exception {
        WebSocketResponse response = new WebSocketResponse(WebSocketTypes.UNSUBSCRIBED,
                topic, null);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
    }

    private void sendErrorMessage(WebSocketSession session, String msg) throws Exception {
        WebSocketResponse error = new WebSocketResponse(WebSocketTypes.ERROR, null, msg);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(error)));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
                                      @NonNull CloseStatus status) {
        hub.removeSessionEverywhere(session);
        System.out.println("debug: Connection closed for " + session.getId());
    }
}