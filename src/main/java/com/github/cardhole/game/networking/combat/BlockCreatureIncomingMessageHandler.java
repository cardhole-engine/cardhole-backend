package com.github.cardhole.game.networking.combat;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.combat.domain.BlockCreatureIncomingMessage;
import com.github.cardhole.game.networking.combat.domain.ResetBlockerOutgoingMessage;
import com.github.cardhole.game.networking.domain.CheatingException;
import com.github.cardhole.game.networking.message.domain.ShowSingleQuestionGameMessageOutgoingMessage;
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
    private final GameNetworkingManipulator gameNetworkingManipulator;

    @Override
    public void handleMessage(final Session session, final BlockCreatureIncomingMessage message) {
        final Game game = gameRegistry.getGame(session)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        final Card blockWith = game.getBattlefield().getGameObject(message.blockWith())
                .orElseThrow();
        final Card blockWhat = game.getBattlefield().getGameObject(message.blockWhat())
                .orElseThrow();

        if (!blockWith.hasAspect(CreatureAspect.class) || !blockWhat.hasAspect(CreatureAspect.class)) {
            throw new CheatingException("The blocker or the blocked is not a creature!");
        }

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

            gameNetworkingManipulator.sendMessageToPlayer(player,
                    ShowSingleQuestionGameMessageOutgoingMessage.builder()
                            .question("Declare blockers.")
                            .responseOneId("DECLARE_BLOCKERS")
                            .buttonOneText("Ok")
                            .build()
            );

            gameNetworkingManipulator.sendMessageToPlayer(player,
                    ResetBlockerOutgoingMessage.builder()
                            .build()
            );

            gameManager.refreshWhatCanBlock(player);
        }
    }

    @Override
    public Class<BlockCreatureIncomingMessage> supportedMessage() {
        return BlockCreatureIncomingMessage.class;
    }
}
