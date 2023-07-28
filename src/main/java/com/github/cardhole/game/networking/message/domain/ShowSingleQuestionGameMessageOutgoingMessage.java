package com.github.cardhole.game.networking.message.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record ShowSingleQuestionGameMessageOutgoingMessage(

        String question,
        String responseOneId,
        String buttonOneText
) implements Message {
}
