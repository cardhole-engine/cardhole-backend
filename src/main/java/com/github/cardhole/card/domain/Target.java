package com.github.cardhole.card.domain;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Target(

        UUID id,
        TargetType type
) {
}
