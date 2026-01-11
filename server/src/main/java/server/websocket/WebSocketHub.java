package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHub {

    private final ObjectMapper mapper = new ObjectMapper();

    private final CopyOnWriteArrayList<WebSocketSession> titleSubscribers =
            new CopyOnWriteArrayList<>();
    private final Map<UUID, CopyOnWriteArrayList<WebSocketSession>> recipeSubscribers =
            new ConcurrentHashMap<>();
    private final Map<UUID, CopyOnWriteArrayList<WebSocketSession>> ingredientSubscribers =
            new ConcurrentHashMap<>();

    public int getTitleSubscribersCount() {
        return titleSubscribers.size();
    }

    public void subscribeTitles(WebSocketSession session) {
        if (!titleSubscribers.contains(session)) {
            titleSubscribers.add(session);
        }
    }

    public void subscribeRecipe(WebSocketSession session, UUID recipeId) {
        recipeSubscribers.computeIfAbsent(
                recipeId, _ -> new CopyOnWriteArrayList<>()).addIfAbsent(session
        );
    }

    public void subscribeIngredient(WebSocketSession session, UUID ingredientId) {
        ingredientSubscribers.computeIfAbsent(
                ingredientId, _ -> new CopyOnWriteArrayList<>()).addIfAbsent(session
        );
    }

    public void unsubscribeTitles(WebSocketSession session) {
        titleSubscribers.remove(session);
    }

    public void unsubscribeRecipe(WebSocketSession session, UUID recipeId) {
        List<WebSocketSession> sessions = recipeSubscribers.get(recipeId);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) {
                recipeSubscribers.remove(recipeId);
            }
        }
    }

    public void unsubscribeIngredient(WebSocketSession session, UUID ingredientId) {
        List<WebSocketSession> sessions = ingredientSubscribers.get(ingredientId);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) {
                ingredientSubscribers.remove(ingredientId);
            }
        }
    }

    public void broadcastTitleUpdate(Object allRecipes) {
        WebSocketResponse response = new WebSocketResponse(
                WebSocketTypes.UPDATE,
                "recipe-titles",
                allRecipes);
        broadcast(titleSubscribers, response);
    }

    public void broadcastRecipeUpdate(UUID recipeId, Object recipeData) {
        List<WebSocketSession> sessions = recipeSubscribers.get(recipeId);
        if (sessions != null && !sessions.isEmpty()) {
            WebSocketResponse response = new WebSocketResponse(
                    WebSocketTypes.UPDATE,
                    "recipe",
                    recipeData);
            broadcast(sessions, response);
        }
    }

    public void broadcastIngredientUpdate(UUID ingredientId, Object ingredientData) {
        List<WebSocketSession> sessions = ingredientSubscribers.get(ingredientId);
        if (sessions != null && !sessions.isEmpty()) {
            WebSocketResponse response = new WebSocketResponse(
                    WebSocketTypes.UPDATE,
                    "ingredient",
                    ingredientData);
            broadcast(sessions, response);
        }
    }

    private void broadcast(List<WebSocketSession> sessions, WebSocketResponse response) {
        String json;

        try {
            json = mapper.writeValueAsString(response);
        } catch (IOException e) {
            return;
        }

        TextMessage message = new TextMessage(json);

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                } else {
                    removeSessionEverywhere(session);
                }
            } catch (IOException e) {
                removeSessionEverywhere(session);
            }
        }
    }

    public synchronized void removeSessionEverywhere(WebSocketSession session) {
        titleSubscribers.remove(session);

        recipeSubscribers.values().forEach(sessions -> sessions.remove(session));
        recipeSubscribers.values().removeIf(CopyOnWriteArrayList::isEmpty);

        ingredientSubscribers.values().forEach(sessions -> sessions.remove(session));
        ingredientSubscribers.values().removeIf(CopyOnWriteArrayList::isEmpty);
    }
}