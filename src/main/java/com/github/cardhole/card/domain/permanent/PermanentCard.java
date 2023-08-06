package com.github.cardhole.card.domain.permanent;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.AbstractCard;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.type.CardType;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class PermanentCard extends AbstractCard implements Permanent {

    @Setter
    private boolean tapped;
    private final List<ActivatedAbility> activatedAbilities = new LinkedList<>();

    public PermanentCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                         final CardType cardType, final ManaCost manaCost) {
        super(game, owner, name, set, setId, cardType, manaCost);
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
