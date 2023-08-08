package com.github.cardhole.game.networking.combat;

import com.github.cardhole.game.networking.combat.domain.BlockCreatureIncomingMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockCreatureIncomingMessageHandler implements MessageHandler<BlockCreatureIncomingMessage> {

    @Override
    public void handleMessage(final Session session, final BlockCreatureIncomingMessage message) {
        //TODO: Handle the assigning of blockers
    }

    @Override
    public Class<BlockCreatureIncomingMessage> supportedMessage() {
        return BlockCreatureIncomingMessage.class;
    }
}
