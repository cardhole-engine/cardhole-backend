package com.github.cardhole.game.networking.combat;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.combat.domain.BlockCreatureIncomingMessage;
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
public class BlockCreatureIncomingMessageHandler implements MessageHandler<BlockCreatureIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;

    @Override
    public void handleMessage(final Session session, final BlockCreatureIncomingMessage message) {
        final Game game = gameRegistry.getGame(session)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        final PermanentCard blockWith = game.getBattlefield().getCardOnBattlefield(message.blockWith())
                .orElseThrow();
        final PermanentCard blockWhat = game.getBattlefield().getCardOnBattlefield(message.blockWhat())
                .orElseThrow();

        if (game.isActivePlayer(player)) {
            throw new CheatingException("The active player tried to assign blockers!");
        }

        if (!blockWith.isControlledBy(player)) {
            throw new CheatingException("Player tried to block with a card he/she does not control!");
        }

        if (blockWhat.isControlledBy(player)) {
            throw new CheatingException("Player tried to block his own attacking card!");
        }

        if (game.getStep() == Step.BLOCK && !game.isActivePlayer(player) && game.isWaitingForBlockers()) {
            game.addBlocker(blockWhat, blockWith);

            gameManager.markCardAsDefending(blockWith, blockWhat);
            gameManager.refreshWhatCanBlock(player);
        }
    }

    @Override
    public Class<BlockCreatureIncomingMessage> supportedMessage() {
        return BlockCreatureIncomingMessage.class;
    }
}
