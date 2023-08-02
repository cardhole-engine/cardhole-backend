package com.github.cardhole.game.networking.stop;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.create.domain.CreateGameIncomingMessage;
import com.github.cardhole.game.networking.stop.domain.ChangeStopIncomingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeStopIncomingMessageHandler implements MessageHandler<ChangeStopIncomingMessage> {

    private final GameRegistry gameRegistry;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    @Override
    public void handleMessage(final Session session, final ChangeStopIncomingMessage message) {
        final Game game = session.getActiveGameId()
                .flatMap(gameRegistry::getGame)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        if (message.myTurn()) {
            player.getStopAtStepInMyTurn().put(message.step(), message.newValue());
        } else {
            player.getStopAtStepInOpponentTurn().put(message.step(), message.newValue());
        }

        gameNetworkingManipulator.sendStopRefresh(player);
    }

    @Override
    public Class<ChangeStopIncomingMessage> supportedMessage() {
        return ChangeStopIncomingMessage.class;
    }
}
