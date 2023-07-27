package com.github.cardhole.game.networking.message.domain;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record ShowDualQuestionGameMessage(

        String question,
        String buttonOneText,
        String buttonTwoText,
        String responseOneId,
        String responseTwoId
) implements Message {
}
