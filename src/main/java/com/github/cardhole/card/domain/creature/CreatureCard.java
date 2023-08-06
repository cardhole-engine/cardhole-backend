package com.github.cardhole.card.domain.creature;

import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.cost.ManaCost;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

@Setter
@Getter
public abstract class CreatureCard extends PermanentCard {

    protected int attack;
    protected int defense;

    public CreatureCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                        final Set<Subtype> subtype, final ManaCost manaCost, final int attack, final int defense) {
        this(game, owner, name, set, setId, Collections.emptySet(), Set.of(Type.CREATURE), subtype, manaCost, attack,
                defense);
    }

    public CreatureCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                        final Set<Supertype> supertype, final Set<Type> type, final Set<Subtype> subtype,
                        final ManaCost manaCost, final int attack, final int defense) {
        super(game, owner, name, set, setId, supertype, type, subtype, manaCost);

        this.attack = attack;
        this.defense = defense;
    }

    @Override
    public void cast(final Target target) {
    }
}
