package com.github.cardhole.login.networking.domain.message;

import com.github.cardhole.networking.domain.Message;

public record LoginMessage(

        String type,
        String name
) implements Message {
}
