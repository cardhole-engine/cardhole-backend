package com.github.cardhole.networking.domain;

import lombok.Builder;

@Builder
public record MessageTypeRegister(

        String type,
        Class<? extends Message> domainClass
) {
}
