package com.github.cardhole.card.domain.permanent;

import com.github.cardhole.ability.ActivatedAbility;

import java.util.List;

public interface Permanent {

    boolean hasActivatedAbility();

    List<ActivatedAbility> getAbilities();
}
