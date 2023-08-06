package com.github.cardhole.game.networking.battlefiled;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CardEnterToBattlefieldOutgoingMessage(

        //TODO: Other card data should be added!

        UUID id,
        String name,
        UUID ownerId,
        String set,
        int setId,
        List<ActivatedActivity> activatedAbilities
) implements Message {

    @Builder
    public record ActivatedActivity(
            UUID id
    ) {
    }
}
