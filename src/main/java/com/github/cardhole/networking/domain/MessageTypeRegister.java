package com.github.cardhole.networking.domain;

import lombok.Builder;

@Builder
public record MessageTypeRegister(

        Class<? extends Message> domainClass
) {
}
