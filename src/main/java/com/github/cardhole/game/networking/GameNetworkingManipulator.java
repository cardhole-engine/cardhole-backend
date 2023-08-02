package com.github.cardhole.game.networking;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.battlefiled.CardEnterToBattlefieldOutgoingMessage;
import com.github.cardhole.game.networking.deck.domain.DeckSizeChangeOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.HandSizeChangeOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.RemoveCardFromHandOutgoingMessage;
import com.github.cardhole.game.networking.log.domain.SendLogOutgoingMessage;
import com.github.cardhole.game.networking.hand.domain.AddCardToHandOutgoingMessage;
import com.github.cardhole.game.networking.mana.domain.RefreshManaPoolOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ResetMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.step.StepChangeOutgoingMessage;
import com.github.cardhole.game.networking.stop.domain.RefreshStopsOutgoingMessage;
import com.github.cardhole.mana.domain.ManaPool;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.player.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameNetworkingManipulator {

    public void sendMessageToPlayer(final Player player, final Message message) {
        player.getSession().sendMessage(message);
    }

    public void broadcastLogMessage(final Game game, final String message) {
        sendMessageToEveryone(game,
                SendLogOutgoingMessage.builder()
                        .message(message)
                        .build()
        );
    }

    public void broadcastLogMessageExceptTo(final Game game, final Player exception, final String log) {
        sendMessageToEveryoneExceptTo(game, exception,
                SendLogOutgoingMessage.builder()
                        .message(log)
                        .build()
        );
    }

    public void sendGameMessageToPlayer(final Player player, final String gameMessage) {
        sendMessageToPlayer(player,
                ShowSimpleGameMessageOutgoingMessage.builder()
                        .message(gameMessage)
                        .build()
        );
    }

    public void broadcastGameMessageExceptTo(final Game game, final Player exception, final String gameMessage) {
        sendMessageToEveryoneExceptTo(game, exception,
                ShowSimpleGameMessageOutgoingMessage.builder()
                        .message(gameMessage)
                        .build()
        );
    }

    public void resetGameMessageForEveryone(final Game game) {
        sendMessageToEveryone(game,
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

    public void sendMessageToEveryone(final Game game, final Message message) {
        game.getPlayers().stream()
                .map(Player::getSession)
                .forEach(session -> session.sendMessage(message));
    }

    public void sendMessageToEveryoneExceptTo(final Game game, final Player exception, final Message message) {
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
        sendMessageToEveryone(player.getGame(),
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
        sendMessageToEveryone(player.getGame(),
                DeckSizeChangeOutgoingMessage.builder()
                        .playerId(player.getId())
                        .deckSize(player.getCardCountInDeck())
                        .build()
        );
    }

    public void broadcastStepChangeMessage(final Game game, final Step newActiveStep) {
        sendMessageToEveryone(game,
                StepChangeOutgoingMessage.builder()
                        .activeStep(newActiveStep)
                        .build()
        );
    }

    public void broadcastCardEnterToBattlefield(final Card card) {
        sendMessageToEveryone(card.getGame(),
                CardEnterToBattlefieldOutgoingMessage.builder()
                        .id(card.getId())
                        .name(card.getName())
                        .ownerId(card.getController().getId())
                        .build()
        );
    }

    public void broadcastRefreshManaPool(final Player player) {
        final ManaPool playersManaPool = player.getManaPool();

        sendMessageToEveryone(player.getGame(),
                RefreshManaPoolOutgoingMessage.builder()
                        .whiteMana(playersManaPool.getWhiteMana())
                        .blueMana(playersManaPool.getBlueMana())
                        .blackMana(playersManaPool.getBlackMana())
                        .redMana(playersManaPool.getRedMana())
                        .greenMana(playersManaPool.getGreenMana())
                        .colorlessMana(playersManaPool.getColorlessMana())
                        .build()
        );
    }

    public void sendStopRefresh(final Player player) {
        sendMessageToPlayer(player,
                RefreshStopsOutgoingMessage.builder()
                        .stopAtStepInMyTurn(player.getStopAtStepInMyTurn())
                        .stopAtStepInOpponentTurn(player.getStopAtStepInOpponentTurn())
                        .build()
        );
    }
}
