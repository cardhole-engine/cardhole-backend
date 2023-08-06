package com.github.cardhole.card.domain.land;

import com.github.cardhole.card.domain.permanent.Permanent;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.Set;

public abstract class LandCard extends PermanentCard implements Permanent {

    public LandCard(final Game game, final Player owner, final String name, final CardSet set, final int setId,
                    final Set<Supertype> supertype, final Set<Type> type, final Set<Subtype> subtype) {
        super(game, owner, name, set, setId, supertype, type, subtype);
    }

    @Override
    public boolean canBeCast() {
        return super.canBeCast() && !owner.getGame().isLandCastedThisTurn();
    }

    @Override
    public void cast(final Target target) {
        game.getGameManager().castLandCardToPlayersBattlefield(this);
    }
}
