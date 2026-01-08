package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testSubscribeToRecipeTitles() throws Exception {
        // use two futurs: confirminig subscription and recieving updates
        CompletableFuture<String> subConfirmation = new CompletableFuture<>();
        CompletableFuture<String> dataUpdate = new CompletableFuture<>();

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
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

        // update on server, simulating other user editing recipe
        String updatedTitle = "Test Recipe Title";
        hub.broadcastRecipeUpdate(recipeId, updatedTitle);

        // check if client got the notification
        String updateResult = dataUpdate.get(5, TimeUnit.SECONDS);
        assertTrue(updateResult.contains("UPDATE"), "Client should receive an UPDATE message");
        assertTrue(updateResult.contains(updatedTitle), "Message should contain the new data");

        session.close();
    }
}