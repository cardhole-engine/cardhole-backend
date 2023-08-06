package com.github.cardhole.login.networking.domain.message;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

@Builder
public record LoginResultOutgoingMessage(

        String staticAssetLocation
) implements Message {
}
