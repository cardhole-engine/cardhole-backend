package com.github.cardhole.card.domain.permanent;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.AbstractCard;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public abstract class PermanentCard extends AbstractCard implements Permanent {

    private boolean tapped;
    private final List<ActivatedAbility> abilities;

    public PermanentCard(final Game game, final Player owner, final String name, final Set set, final int setId,
                         final List<ActivatedAbility> abilities) {
        super(game, owner, name, set, setId);

        this.abilities = abilities;
    }

    @Override
    public boolean hasActivatedAbility() {
        return !abilities.isEmpty();
    }

    @Override
    public List<ActivatedAbility> getAbilities() {
        return abilities;
    }

    public boolean isTapped() {
        return tapped;
    }

    public boolean isUntapped() {
        return !tapped;
    }
}
