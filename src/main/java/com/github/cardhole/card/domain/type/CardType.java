package com.github.cardhole.card.domain.type;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
@Builder
public class CardType {

    @Singular("supertype")
    protected final Set<Supertype> supertype;

    @Singular("type")
    protected final Set<Type> type;

    @Singular("subtype")
    protected final Set<Subtype> subtype;
}
