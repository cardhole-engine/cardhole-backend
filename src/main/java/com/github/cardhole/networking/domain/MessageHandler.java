package com.github.cardhole.networking.domain;

import org.springframework.web.socket.WebSocketSession;

public interface MessageHandler<T extends Message> {

    //TODO: It would be cool if we could hide WSS behind a Session class
    void handleMessage(WebSocketSession webSocketSession, T message);

    Class<T> supportedMessage();
}
