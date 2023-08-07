package com.github.cardhole.game.networking.combat;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.combat.domain.DeclareAttackerIncomingMessage;
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
public class DeclareAttackerIncomingMessageHandler implements MessageHandler<DeclareAttackerIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;

    @Override
    public void handleMessage(final Session session, final DeclareAttackerIncomingMessage message) {
        final Game game = session.getActiveGameId()
                .flatMap(gameRegistry::getGame)
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

        if (game.getStep() == Step.ATTACK && game.isActivePlayer(player) && game.isWaitingForAttackers()) {
            game.addAttacker(card);

            gameManager.tapCardOnBattlefield(card);
            gameManager.refreshWhatCanAttack(player);
            //TODO: Send a packet that shows that the card is attacking on the UI for everyone
        } else {
            throw new CheatingException("It is not the time to declare attackers!");
        }
    }

    @Override
    public Class<DeclareAttackerIncomingMessage> supportedMessage() {
        return DeclareAttackerIncomingMessage.class;
    }
}
