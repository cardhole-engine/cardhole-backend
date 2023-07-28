package com.github.cardhole.game.networking.step;

import com.github.cardhole.game.domain.Step;
import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record StepChangeOutgoingMessage(

        Step activeStep
) implements Message {
}
