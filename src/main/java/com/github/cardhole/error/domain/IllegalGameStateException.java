package com.github.cardhole.error.domain;

import com.github.cardhole.object.domain.GameObject;

public class IllegalGameStateException extends RuntimeException {

    public IllegalGameStateException(final String message, final GameObject... causedBy) {
        super(message);
    }
}
