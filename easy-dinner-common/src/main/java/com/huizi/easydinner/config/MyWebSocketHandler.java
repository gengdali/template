package com.huizi.easydinner.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PROJECT_NAME:
 * @DESCRIPTION:
 * @AUTHOR: 12615
 * @DATE: 2023/5/13 13:32
 */
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("错误信息");
    }

    public void sendProgressUpdate(int progress) {
        sessions.values().forEach(session -> {
            try {
                if (session != null && session.isOpen()) {
                    session.sendMessage(new TextMessage(String.valueOf(progress)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Integer getSessionsSize() {
        return sessions.size();
    }
}
