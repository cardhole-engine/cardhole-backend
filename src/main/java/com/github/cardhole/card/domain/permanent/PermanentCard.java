package com.github.cardhole.card.domain.permanent;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.AbstractCard;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public abstract class PermanentCard extends AbstractCard implements Permanent {

    @Setter
    private boolean tapped;
    private final List<ActivatedAbility> activatedAbilities = new LinkedList<>();

    public PermanentCard(final Game game, final Player owner, final String name, final Set set, final int setId) {
        super(game, owner, name, set, setId);
    }

    @Override
    public boolean hasActivatedAbility() {
        return !activatedAbilities.isEmpty();
    }

    @Override
    public List<ActivatedAbility> getActivatedAbilities() {
        return activatedAbilities;
    }

    public boolean isTapped() {
        return tapped;
    }

    public boolean isUntapped() {
        return !tapped;
    }

    public void addActivatedAbility(final ActivatedAbility activatedAbility) {
        this.activatedAbilities.add(activatedAbility);
    }
}
