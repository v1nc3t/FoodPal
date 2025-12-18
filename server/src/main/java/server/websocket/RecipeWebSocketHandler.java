package server.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class RecipeWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketHub hub;
    private final ObjectMapper mapper;

    public RecipeWebSocketHandler(WebSocketHub hub) {
        this.hub = hub;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        System.out.println("debug: New user joined. Session ID: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
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
        } else {
            sendErrorMessage(session, "Only subscribe command is allowed");
        }
    }

    private void handleSubscription(WebSocketSession session, String topic, JsonNode json) throws Exception {
        if (topic.equals("recipe-titles")) {
            hub.subscribeTitles(session);
            sendConfirm(session, "recipe-titles");

        } else if (topic.equals("recipe")) {
            long id = json.path("recipeId").asLong(-1);
            if (id == -1) {
                sendErrorMessage(session, "recipeId is missing");
            } else {
                hub.subscribeRecipe(session, id);
                sendConfirm(session, "recipe");
            }
        } else {
            sendErrorMessage(session, "Unknown topic: " + topic);
        }
    }

    private void sendConfirm(WebSocketSession session, String topic) throws Exception {
        WebSocketResponse response = new WebSocketResponse(WebSocketTypes.SUBSCRIBED, topic, null);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
    }

    private void sendErrorMessage(WebSocketSession session, String msg) throws Exception {
      WebSocketResponse error = new WebSocketResponse(WebSocketTypes.ERROR, null,msg);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(error)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        hub.removeSessionEverywhere(session);
        System.out.println("debug: Connection closed for " + session.getId());
    }
}