package com.github.cardhole.game.networking.create;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.create.domain.CreateGameMessage;
import com.github.cardhole.game.service.container.GameContainer;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import com.github.cardhole.player.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateGameMessageHandler implements MessageHandler<CreateGameMessage> {

    private final GameContainer gameContainer;

    @Override
    public void handleMessage(final Session session, final CreateGameMessage message) {
        gameContainer.registerGame(new Game(message.name(), new Player(session)));

        session.setInGame(true);

        //TODO: Send game joined message to the owner
    }

    @Override
    public Class<CreateGameMessage> supportedMessage() {
        return CreateGameMessage.class;
    }
}
