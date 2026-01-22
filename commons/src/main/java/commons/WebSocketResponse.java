package commons;

public record WebSocketResponse(
        WebSocketTypes type,
        String topic,
        Object data) {
}
