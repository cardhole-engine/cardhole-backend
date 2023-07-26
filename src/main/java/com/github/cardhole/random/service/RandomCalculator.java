package com.github.cardhole.random.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomCalculator {

    private final Random random = new Random();

    public int randomIntBetween(final int min, final int max) {
        return random.nextInt(max - min) + min;
    }
}
