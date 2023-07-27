package com.github.cardhole.game.networking;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.hand.domain.HandSizeChangeOutgoingMessage;
import com.github.cardhole.game.networking.log.domain.SendLogOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.AddCardToHandOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ResetMessageOutgoingMessage;
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

    public void sendNewCardToPlayerHand(final Player player, final Card card) {
        player.getSession().sendMessage(
                AddCardToHandOutgoingMessage.builder()
                        .id(card.getId())
                        .name(card.getName())
                        .build()
        );
    }

    public void broadcastPlayerHandSize(final Game game, final Player player) {
        sendToEveryone(game,
                HandSizeChangeOutgoingMessage.builder()
                        .playerId(player.getId())
                        .handSize(player.getCardCountInDeck())
                        .build()
        );
    }

    public void resetGameMessageForEveryone(final Game game) {
        sendToEveryone(game,
                ResetMessageOutgoingMessage.builder()
                        .build()
        );
    }
}
