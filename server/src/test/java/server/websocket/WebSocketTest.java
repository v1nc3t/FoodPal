package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketTest {

    private static final String WEB_SOCKET_URL = "ws://localhost:8080/ws";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSubscribeToRecipeTitles() throws Exception {
        CompletableFuture<String> serverResponse = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                serverResponse.complete(message.getPayload());
            }
        };

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.execute(handler, WEB_SOCKET_URL).get(5, TimeUnit.SECONDS);

        Map<String, String> request = new HashMap<>();
        request.put("type", "SUBSCRIBE");
        request.put("topic", "recipe-titles");

        session.sendMessage(new TextMessage(mapper.writeValueAsString(request)));

        String result = serverResponse.get(5, TimeUnit.SECONDS);

        assertTrue(result.contains("SUBSCRIBED"));
        assertTrue(result.contains("recipe-titles"));

        session.close();
    }
}