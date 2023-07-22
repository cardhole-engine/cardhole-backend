package com.github.cardhole.networking.domain;

public interface MessageHandler<T extends Message> {

    void handleMessage(Session session, T message);

    Class<T> supportedMessage();
}
