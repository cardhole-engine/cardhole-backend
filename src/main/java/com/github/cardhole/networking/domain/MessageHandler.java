package com.github.cardhole.networking.domain;

import com.github.cardhole.session.domain.Session;

public interface MessageHandler<T extends Message> {

    void handleMessage(Session session, T message);

    Class<T> supportedMessage();
}
