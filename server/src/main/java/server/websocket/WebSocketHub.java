package server.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketHub {

    private final List<WebSocketSession> titleSubscribers = new ArrayList<>();
    private final Map<Long, List<WebSocketSession>> recipeSubscribers = new HashMap<>();

    public synchronized void subscribeTitles(WebSocketSession session) {
        if (!titleSubscribers.contains(session)) {
            titleSubscribers.add(session);
        }
    }

    public synchronized void subscribeRecipe(WebSocketSession session, long recipeId) {
        if (!recipeSubscribers.containsKey(recipeId)) {
            recipeSubscribers.put(recipeId, new ArrayList<>());
        }

        List<WebSocketSession> sessions = recipeSubscribers.get(recipeId);
        if (!sessions.contains(session)) {
            sessions.add(session);
        }
    }

    public synchronized void removeSessionEverywhere(WebSocketSession session) {
        titleSubscribers.remove(session);

        for (Long id : recipeSubscribers.keySet()) {
            List<WebSocketSession> sessions = recipeSubscribers.get(id);
            sessions.remove(session);
        }

        recipeSubscribers.values().removeIf(List::isEmpty);
    }
}