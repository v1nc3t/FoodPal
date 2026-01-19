package client.services;

import client.config.ConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.websocket.*;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ClientEndpoint
@Singleton
public class WebSocketService {

    private final ConfigManager configManager;
    private final ObjectMapper mapper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Session session;

    @Inject
    public WebSocketService(ConfigManager configManager) {
        this.configManager = configManager;
        this.mapper = new ObjectMapper();
    }

    /**
     * Connects to the WebSocket server.
     */
    public void connect() {
        // TODO: Implement connection logic
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        // TODO: Implement message handling and routing to subscribers
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
