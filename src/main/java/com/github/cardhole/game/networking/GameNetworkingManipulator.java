package com.github.cardhole.game.networking;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.log.domain.SendLogOutgoingMessage;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.player.domain.Player;
import org.springframework.stereotype.Service;

@Service
public class GameNetworkingManipulator {

    public void broadcastMessage(final Game game, final String message) {
        sendToEveryone(game,
                SendLogOutgoingMessage.builder()
                        .message(message)
                        .build()
        );
    }

    public void broadcastMessageExceptTo(final Game game, final Player exception, final String log) {
        sendToEveryoneExceptTo(game, exception,
                SendLogOutgoingMessage.builder()
                        .message(log)
                        .build()
        );
    }

    public void sendToEveryone(final Game game, final Message message) {
        game.getPlayers().stream()
                .map(Player::getSession)
                .forEach(session -> session.sendMessage(message));
    }

    public void sendToEveryoneExceptTo(final Game game, final Player exception, final Message message) {
        game.getPlayers().stream()
                .filter(player -> !player.equals(exception))
                .map(Player::getSession)
                .forEach(session -> session.sendMessage(message));
    }
}
