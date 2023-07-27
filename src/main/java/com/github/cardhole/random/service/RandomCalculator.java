package com.github.cardhole.random.service;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Service
public class RandomCalculator {

    private final Random random = new Random();

    public int randomIntBetween(final int min, final int max) {
        return random.nextInt(max - min) + min;
    }

    public <T> T randomEntryFromList(final List<T> list) {
        return list.get(randomIntBetween(0, list.size() - 1));
    }
}
