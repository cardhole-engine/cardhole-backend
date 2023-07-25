package com.github.cardhole.networking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.session.service.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

    private final ObjectMapper objectMapper;
    private final SessionRegistry sessionRegistry;
    private final Map<Class, MessageHandler> messageHandlers;

    public ServerWebSocketHandler(final ObjectMapper objectMapper,
                                  final SessionRegistry sessionRegistry,
                                  final List<MessageHandler<?>> messageHandlers) {
        this.objectMapper = objectMapper;
        this.sessionRegistry = sessionRegistry;
        this.messageHandlers = messageHandlers.stream()
                .collect(Collectors.toMap(MessageHandler::supportedMessage, Function.identity()));
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        log.info("Server connection opened!");

        sessionRegistry.registerSession(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        log.info("Server connection closed: {}.", status);

        sessionRegistry.removeSession(session);
    }

    @Scheduled(fixedRate = 10000)
    void sendPeriodicMessages() throws IOException {
        //TODO: Send heartbeat/ping or something similar here
        //for (WebSocketSession session : sessions) {
        //if (session.isOpen()) {
        //String broadcast = "server periodic message " + LocalTime.now();
        //log.info("Server sends: {}", broadcast);
        //session.sendMessage(new TextMessage(broadcast));
        //}
        //}
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) throws Exception {
        final Message message = objectMapper.readValue(textMessage.getPayload(), Message.class);

        log.info("Got message: {}", message);

        messageHandlers.get(message.getClass())
                .handleMessage(sessionRegistry.getSession(session), message);
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
