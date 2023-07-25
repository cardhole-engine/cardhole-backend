package com.github.cardhole.login.networking.domain.message;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RefreshHomePageOutgoingMessage(

        List<RunningGame> games
) implements Message {

    @Builder
    public record RunningGame(

            UUID id,
            String name,
            int actualPlayers,
            int maximumPlayers
    ) {
    }
}
