package com.github.cardhole.login.networking.domain.message;

import com.github.cardhole.networking.domain.Message;
import lombok.Builder;

import java.util.List;

@Builder
public record InitializeHomePageMessage(

        List<RunningGame> games
) implements Message {

    public String type() {
        return "InitializeHomePage";
    }

    @Builder
    public record RunningGame(

            String name,
            int actualPlayers,
            int maximumPlayers
    ) {
    }
}
