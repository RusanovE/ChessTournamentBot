package com.example.ChessTournamentBot.util;

import java.util.Random;

public final class  Randomizer {

    private static final Random random = new Random();

    public static int generateRandomNumber(int num) {
        return random.nextInt(num+1);
    }
}
