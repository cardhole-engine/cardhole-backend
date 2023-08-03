package com.github.cardhole.card.domain.land;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.permanent.Permanent;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.List;

public abstract class LandCard extends PermanentCard implements Permanent {

    public LandCard(final Game game, final Player owner, final String name, final Set set, final int setId) {
        super(game, owner, name, set, setId);
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
