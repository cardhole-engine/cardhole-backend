package com.github.cardhole.home.service;

import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.login.networking.domain.message.RefreshHomePageOutgoingMessage;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.session.service.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeRefresherService {

    private final GameRegistry gameRegistry;
    private final SessionRegistry sessionRegistry;

    public void refreshHomeForSessions() {
        sessionRegistry.listSessions()
                .forEach(this::refreshHomeForSession);
    }

    public void refreshHomeForSession(final Session session) {
        if (session.isInGame()) {
            return;
        }

        session.sendMessage(
                RefreshHomePageOutgoingMessage.builder()
                        .games(
                                gameRegistry.listGames().stream()
                                        .map(game -> RefreshHomePageOutgoingMessage.RunningGame.builder()
                                                .id(game.getId())
                                                .name(game.getName())
                                                .actualPlayers(game.getPlayerCount())
                                                .maximumPlayers(2)
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
        );
    }
}
