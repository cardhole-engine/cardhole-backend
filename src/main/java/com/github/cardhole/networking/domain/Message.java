package com.github.cardhole.networking.domain;

public interface Message {

    default String type() {
        return this.getClass().getSuperclass().getSimpleName();
    }
}
