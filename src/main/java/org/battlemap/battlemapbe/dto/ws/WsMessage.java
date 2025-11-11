package org.battlemap.battlemapbe.dto.ws;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage<T> {

    public enum Type { SCORE_UPDATED, LEADERBOARD_SNAPSHOT, ERROR }

    private final Type type;
    private final T payload;
    private final String code;     // ERROR 시
    private final String message;  // ERROR 시

    private WsMessage(Type type, T payload, String code, String message) {
        this.type = type; this.payload = payload; this.code = code; this.message = message;
    }

    public static <T> WsMessage<T> ok(Type type, T payload) {
        return new WsMessage<>(type, payload, null, null);
    }
    public static <T> WsMessage<T> error(String code, String message) {
        return new WsMessage<>(Type.ERROR, null, code, message);
    }

    public Type getType() { return type; }
    public T getPayload() { return payload; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
