package com.github.cardhole.step.damage;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.step.GameStepProcessor;
import org.springframework.stereotype.Service;

/**
 * @see <a href="https://yawgatog.com/resources/magic-rules/#R510">510. Combat Damage Step</a>
 */
@Service
public class DamageGameStepProcessor implements GameStepProcessor {

    @Override
    public void processStep(final Game game) {
        final GameManager gameManager = game.getGameManager();

        //TODO: This logic will go elsewhere because first the players decide how to assign combat damage.
        // That's what should be here normally!

        final Player opponent = game.getPlayers().stream()
                .filter(player -> !player.equals(game.getActivePlayer()))
                .findFirst()
                .orElseThrow();

        // Unblocked creatures damage the opponent player
        game.getAttackers().stream()
                .filter(attacker -> !game.isBlocked(attacker))
                .forEach(attacker -> gameManager.dealCombatDamageToPlayer(attacker, opponent));

        // TODO: Do damage to the cards, destroy the ones that were killed

        gameManager.movePriority(game);
    }

    @Override
    public Step forStep() {
        return Step.DAMAGE;
    }
}
