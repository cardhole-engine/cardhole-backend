package com.github.cardhole.networking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.networking.domain.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

    private final ObjectMapper messageDeserializerObjectMapper;
    private final Map<Class, MessageHandler> messageHandlers;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public ServerWebSocketHandler(final ObjectMapper messageDeserializerObjectMapper,
                                  final List<MessageHandler<?>> messageHandlers) {
        this.messageDeserializerObjectMapper = messageDeserializerObjectMapper;
        this.messageHandlers = messageHandlers.stream()
                .collect(Collectors.toMap(MessageHandler::supportedMessage, Function.identity()));
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("Server connection opened");
        sessions.add(session);
        TextMessage message = new TextMessage("one-time message from server");
        log.info("Server sends: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Server connection closed: {}", status);
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 10000)
    void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                log.info("Server sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) throws Exception {
        final Message message = messageDeserializerObjectMapper.readValue(textMessage.getPayload(), Message.class);

        log.info("Got message: {}", message);

        messageHandlers.get(message.getClass()).handleMessage(session, message);
        //session.sendMessage(new TextMessage(response));
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) {
        log.info("Server transport error: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
}
