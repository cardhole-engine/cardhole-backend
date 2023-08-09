package com.github.cardhole.game.networking.ability;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.ability.domain.UseActivatedAbilityIncomingMessage;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UseActivatedAbilityIncomingMessageHandler implements MessageHandler<UseActivatedAbilityIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;

    @Override
    public void handleMessage(final Session session,
                              final UseActivatedAbilityIncomingMessage useActivatedAbilityIncomingMessage) {
        final Game game = session.getActiveGameId()
                .flatMap(gameRegistry::getGame)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        final ActivatedAbility activatedAbility = game.getBattlefield().getCards().stream()
                .filter(card -> card.getOwner().getSession().equals(session))
                .flatMap(card -> card.getAspect(PermanentAspect.class).getActivatedAbilities().stream())
                .filter(ability -> ability.getId().equals(useActivatedAbilityIncomingMessage.abilityId()))
                .findFirst()
                .orElseThrow();

        if (activatedAbility.canBeActivated()) {
            activatedAbility.activate();

            gameManager.refreshWhatCanBeCastOrActivated(player);
        }
    }

    @Override
    public Class<UseActivatedAbilityIncomingMessage> supportedMessage() {
        return UseActivatedAbilityIncomingMessage.class;
    }
}
