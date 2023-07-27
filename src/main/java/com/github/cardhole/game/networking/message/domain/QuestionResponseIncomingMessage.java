package com.github.cardhole.game.networking.message.domain;

import com.github.cardhole.networking.domain.Message;

public record QuestionResponseIncomingMessage(

        String type,
        String response
) implements Message {
}
