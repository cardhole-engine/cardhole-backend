package com.github.cardhole.game.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.card.implementation.m14.PillarfieldOx;
import com.github.cardhole.card.implementation.m14.PlainsI;
import com.github.cardhole.error.domain.IllegalGameStateException;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import com.github.cardhole.session.domain.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    @Mock
    private RandomCalculator randomCalculator;

    @Mock
    private GameNetworkingManipulator gameNetworkingManipulator;

    @InjectMocks
    private GameManager gameManager;

    private Player creator;
    private Session creatorSession;
    private Player joiner;
    private Session joinerSession;
    private Game game;

    @BeforeEach
    void setup() {
        game = new Game(gameManager, "test-game");

        creatorSession = mock(Session.class);
        creator = new Player(creatorSession, game, 20);

        joinerSession = mock(Session.class);
        joiner = new Player(joinerSession, game, 20);
    }

    /*
     * Only creatures can assign combat damage.
     */
    @Test
    void testDealCombatDamageToPlayerWhenCardIsNotACreatureCard() {
        final Card card = new PlainsI(game, creator);

        assertThatThrownBy(() -> gameManager.dealCombatDamageToPlayer(card, joiner))
                .isInstanceOf(IllegalGameStateException.class)
                .hasMessageStartingWith("Only creature cards can deal combat damage!");
    }

    /*
     * 510.1a. states that "Creatures that would assign 0 or less damage this way don't assign combat damage
     * at all.".
     */
    @Test
    void testDealCombatDamageToPlayerWhenCreatureCardHasZeroPower() {
        final Card card = new PlainsI(game, creator);

        card.addAspect(
                CreatureAspect.builder()
                        .power(-1)
                        .toughness(1)
                        .build()
        );

        gameManager.dealCombatDamageToPlayer(card, joiner);

        assertThat(joiner.getLife())
                .isEqualTo(20);
    }

    @Test
    void testDealCombatDamageToPlayer() {
        final Card card = new PillarfieldOx(game, creator);

        gameManager.dealCombatDamageToPlayer(card, joiner);

        assertThat(joiner.getLife())
                .isEqualTo(18);
    }

    @Test
    void testPlayerLoseLife() {
        gameManager.playerLoseLife(joiner, 2);

        assertThat(joiner.getLife())
                .isEqualTo(18);
    }
}
