package com.github.cardhole.game.networking.cast;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.cast.domain.CastCardIncomingMessage;
import com.github.cardhole.game.networking.domain.CheatingException;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CastCardIncomingMessageHandler implements MessageHandler<CastCardIncomingMessage> {

    private final GameRegistry gameRegistry;

    @Override
    public void handleMessage(final Session session, final CastCardIncomingMessage message) {
        final Game game = session.getActiveGameId()
                .flatMap(gameRegistry::getGame)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        final Card card = player.getCardInHand(message.cardId())
                .orElseThrow(() -> new CheatingException("Player " + player.getName()
                        + " tired to cast a card that doesn't exist in his/her hand!"));

        if (!card.canBeCast()) {
            return;
        }

        game.getGameManager().castCard(card,
                Target.builder()
                        .id(message.cardId())
                        .type(message.targetType())
                        .build()
        );
    }

    @Override
    public Class<CastCardIncomingMessage> supportedMessage() {
        return CastCardIncomingMessage.class;
    }
}
