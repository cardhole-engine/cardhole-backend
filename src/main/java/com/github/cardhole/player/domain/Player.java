package com.github.cardhole.player.domain;

import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Player {

    @Getter
    private final Session session;

    public String getName() {
        return session.getName();
    }
}
