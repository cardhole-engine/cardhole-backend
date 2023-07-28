package com.github.cardhole.game.networking;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.deck.domain.DeckSizeChangeOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.HandSizeChangeOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.RemoveCardFromHandOutgoingMessage;
import com.github.cardhole.game.networking.log.domain.SendLogOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.AddCardToHandOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ResetMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.step.StepChangeOutgoingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.player.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameNetworkingManipulator {

    private final GameRegistry gameRegistry;

    public void sendMessageToPlayer(final Player player, final Message message) {
        player.getSession().sendMessage(message);
    }

    public void broadcastLogMessage(final Game game, final String message) {
        sendToEveryone(game,
                SendLogOutgoingMessage.builder()
                        .message(message)
                        .build()
        );
    }

    public void broadcastLogMessageExceptTo(final Game game, final Player exception, final String log) {
        sendToEveryoneExceptTo(game, exception,
                SendLogOutgoingMessage.builder()
                        .message(log)
                        .build()
        );
    }

    public void broadcastGameMessageExceptTo(final Game game, final Player exception, final String log) {
        sendToEveryoneExceptTo(game, exception,
                ShowSimpleGameMessageOutgoingMessage.builder()
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

    public void sendRemoveCardFromPlayerHand(final Player player, final UUID cardId) {
        player.getSession().sendMessage(
                RemoveCardFromHandOutgoingMessage.builder()
                        .id(cardId)
                        .build()
        );
    }

    /**
     * Sends a player's hand size to every participant in the game.
     *
     * @param player the player to update the hand size for
     */
    public void broadcastPlayerHandSize(final Player player) {
        final Game game = gameRegistry.getGame(player.getGameId())
                .orElseThrow();

        sendToEveryone(game,
                HandSizeChangeOutgoingMessage.builder()
                        .playerId(player.getId())
                        .handSize(player.getCardCountInHand())
                        .build()
        );
    }

    /**
     * Sends a player's deck size to every participant in the game.
     *
     * @param player the player to update the deck size for
     */
    public void broadcastPlayerDeckSize(final Player player) {
        final Game game = gameRegistry.getGame(player.getGameId())
                .orElseThrow();

        sendToEveryone(game,
                DeckSizeChangeOutgoingMessage.builder()
                        .playerId(player.getId())
                        .deckSize(player.getCardCountInHand())
                        .build()
        );
    }

    public void resetGameMessageForEveryone(final Game game) {
        sendToEveryone(game,
                ResetMessageOutgoingMessage.builder()
                        .build()
        );
    }

    public void resetGameMessageForPlayer(final Player player) {
        player.getSession().sendMessage(
                ResetMessageOutgoingMessage.builder()
                        .build()
        );
    }

    public void broadcastStepChangeMessage(final Game game, final Step newActiveStep) {
        sendToEveryone(game,
                StepChangeOutgoingMessage.builder()
                        .activeStep(newActiveStep)
                        .build()
        );
    }
}
