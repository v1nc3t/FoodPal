package client.services;

import client.config.ConfigManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.WebSocketResponse;
import commons.WebSocketTypes;
import jakarta.websocket.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ClientEndpoint
@Singleton
public class WebSocketService {

    private final ConfigManager configManager;
    private final ObjectMapper mapper;
    private final Map<String, List<Consumer<WebSocketResponse>>> listeners = new ConcurrentHashMap<>();

    private Session session;

    @Inject
    public WebSocketService(ConfigManager configManager) {
        this.configManager = configManager;
        this.mapper = new ObjectMapper();
    }

    /**
     * Converts the generic data object from a response to a specific type.
     */
    public <T> T convertData(Object data, Class<T> clazz) {
        return mapper.convertValue(data, clazz);
    }

    /**
     * Converts the generic data object from a response to a specific type using
     * TypeReference.
     */
    public <T> T convertData(Object data, com.fasterxml.jackson.core.type.TypeReference<T> typeRef) {
        return mapper.convertValue(data, typeRef);
    }

    /**
     * Connects to the WebSocket server.
     */
    public void connect() {
        String serverAddress = configManager.getConfig().getServerAddress();
        // Convert http(s) to ws(s)
        String wsAddress = serverAddress.replaceFirst("(?i)^http", "ws");
        if (!wsAddress.endsWith("/")) {
            wsAddress += "/";
        }
        wsAddress += "ws";

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(wsAddress));
        } catch (Exception e) {
            System.err.println("Failed to connect to WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to a topic.
     * 
     * @param topic    The topic name.
     * @param id       Optional UUID (e.g. for recipe or ingredient).
     * @param callback Callback to invoke on updates.
     */
    public void subscribe(String topic, UUID id, Consumer<WebSocketResponse> callback) {
        String fullTopic = topic + (id != null ? ":" + id : "");
        listeners.computeIfAbsent(fullTopic, _ -> Collections.synchronizedList(new ArrayList<>()))
                .add(callback);

        if (session != null && session.isOpen()) {
            sendSubscriptionMessage(WebSocketTypes.SUBSCRIBE, topic, id);
        }
    }

    /**
     * Unsubscribes from a topic.
     * 
     * @param topic The topic name.
     * @param id    Optional UUID.
     */
    public void unsubscribe(String topic, UUID id) {
        String fullTopic = topic + (id != null ? ":" + id : "");
        listeners.remove(fullTopic);

        if (session != null && session.isOpen()) {
            sendSubscriptionMessage(WebSocketTypes.UNSUBSCRIBE, topic, id);
        }
    }

    private void sendSubscriptionMessage(WebSocketTypes type, String topic, UUID id) {
        Map<String, Object> message = new java.util.HashMap<>();
        message.put("type", type.toString());
        message.put("topic", topic);
        if (id != null) {
            if ("recipe".equals(topic)) {
                message.put("recipeId", id.toString());
            } else if ("ingredient".equals(topic)) {
                message.put("ingredientId", id.toString());
            }
        }

        try {
            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        } catch (Exception e) {
            System.err.println("Failed to send subscription message: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket: " + session.getId());

        // Re-subscribe to all active topics on reconnection
        for (String fullTopic : listeners.keySet()) {
            String[] parts = fullTopic.split(":");
            String topic = parts[0];
            UUID id = parts.length > 1 ? UUID.fromString(parts[1]) : null;
            sendSubscriptionMessage(WebSocketTypes.SUBSCRIBE, topic, id);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            WebSocketResponse response = mapper.readValue(message, WebSocketResponse.class);
            String topic = response.topic();

            String fullTopic = topic;
            if (response.data() != null && response.data() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.data();
                if (data.containsKey("id")) {
                    fullTopic = topic + ":" + data.get("id");
                }
            } else if (response.type() == WebSocketTypes.DELETE && response.data() instanceof String) {
                fullTopic = topic + ":" + response.data();
            }

            // Notify specific listeners
            notifyListeners(fullTopic, response);

            // Also notify generic topic listeners (if any)
            if (!fullTopic.equals(topic)) {
                notifyListeners(topic, response);
            }

        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse WebSocket message: " + e.getMessage());
        }
    }

    private void notifyListeners(String topicKey, WebSocketResponse response) {
        List<Consumer<WebSocketResponse>> topicListeners = listeners.get(topicKey);
        if (topicListeners != null) {
            synchronized (topicListeners) {
                for (Consumer<WebSocketResponse> listener : topicListeners) {
                    listener.accept(response);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.session = null;
        System.out.println("WebSocket closed: " + closeReason);
    }

    @OnError
    public void onError(Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }
}
