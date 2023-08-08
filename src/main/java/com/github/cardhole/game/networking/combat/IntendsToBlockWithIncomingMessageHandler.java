package com.github.cardhole.game.networking.combat;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.combat.domain.IntendsToBlockWithIncomingMessage;
import com.github.cardhole.game.networking.domain.CheatingException;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class IntendsToBlockWithIncomingMessageHandler implements MessageHandler<IntendsToBlockWithIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;

    @Override
    public void handleMessage(final Session session, final IntendsToBlockWithIncomingMessage message) {
        final Game game = gameRegistry.getGame(session)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        final PermanentCard card = game.getBattlefield().getCardOnBattlefield(message.cardId())
                .orElseThrow(() -> new CheatingException("Unknown card with id: " + message.cardId() + "!"));

        if (!card.isControlledBy(player)) {
            throw new CheatingException("Player tried to attack with a card that he/she doesn't control!");
        }

        if (card.isTapped()) {
            throw new CheatingException("Player tried to attack with a card that is tapped!");
        }

        if (game.getStep() == Step.BLOCK && !game.isActivePlayer(player) && game.isWaitingForBlockers()) {
            gameManager.refreshWhatCanBlockedBy(card);
        }
    }

    @Override
    public Class<IntendsToBlockWithIncomingMessage> supportedMessage() {
        return IntendsToBlockWithIncomingMessage.class;
    }
}
