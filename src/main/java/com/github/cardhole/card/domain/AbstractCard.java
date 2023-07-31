package com.github.cardhole.card.domain;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.player.domain.Player;
import lombok.Getter;

import java.util.UUID;

public abstract class AbstractCard implements Card {

    protected final UUID id;
    protected final Game game;
    protected final String name;
    protected final Set set;
    protected final int setId;

    /*
     * The player who (for purposes of the game) a card, permanent, token, or spell belongs to. See rules 108.3, 110.2,
     * 111.2, and 112.2.
     *
     * 108.3. The owner of a card in the game is the player who started the game with it in their deck. If a card is
     * brought into the game from outside the game rather than starting in a player’s deck, its owner is the player who
     * brought it into the game. If a card starts the game in the command zone, its owner is the player who put it into
     * the command zone to start the game. Legal ownership of a card in the game is irrelevant to the game rules except
     * for the rules for ante. (See rule 407.)
     */
    @Getter
    protected final Player owner;

    /*
     * Words that refer to an object’s controller, its would-be controller (if a player is attempting to cast or
     * activate it), or its owner (if it has no controller). See rule 109.5.
     *
     * 109.5. The words “you” and “your” on an object refer to the object’s controller, its would-be controller
     * (if a player is attempting to play, cast, or activate it), or its owner (if it has no controller). For a static
     * ability, this is the current controller of the object it’s on. For an activated ability, this is the player who
     * activated the ability. For a triggered ability, this is the controller of the object when the ability triggered,
     * unless it’s a delayed triggered ability. To determine the controller of a delayed triggered ability, see rules
     * 603.7d–f.
     */
    @Getter
    protected final Player controller;

    protected boolean castWithInstantSpeed;

    public AbstractCard(final Game game, final Player owner, final String name, final Set set, final int setId) {
        this.id = UUID.randomUUID();
        this.game = game;
        this.owner = owner;
        this.controller = owner;
        this.name = name;
        this.set = set;
        this.setId = setId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set getSet() {
        return set;
    }

    @Override
    public int getSetId() {
        return setId;
    }

    @Override
    public boolean canBeCast() {
        if (castWithInstantSpeed) {
            return true;
        }

        /*
         * 505.6a The main phase is the only phase in which a player can normally cast artifact, creature, enchantment,
         *     planeswalker, and sorcery spells. The active player may cast these spells.
         */
        return game.isActivePlayer(owner) && game.isStepActive(Step.PRECOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                && game.isStackEmpty();
    }
}
