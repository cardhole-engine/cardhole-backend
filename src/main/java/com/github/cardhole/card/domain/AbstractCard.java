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

    @Getter
    protected final Player owner;

    protected boolean castWithInstantSpeed;

    public AbstractCard(final Game game, final Player owner, final String name, final Set set, final int setId) {
        this.id = UUID.randomUUID();
        this.game = game;
        this.owner = owner;
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
