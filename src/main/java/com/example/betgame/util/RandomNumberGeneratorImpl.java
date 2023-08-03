package com.example.betgame.util;

import org.springframework.stereotype.Component;

@Component
public class RandomNumberGeneratorImpl implements RandomNumberGenerator {
    @Override
    public int generateRandomNumber() {
        return (int) (Math.random() * 99) + 1;
    }
}
