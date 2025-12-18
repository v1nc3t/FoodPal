package server.websocket;

public record WebSocketResponse(
    WebSocketTypes type,
    String topic,
    String message
) {}