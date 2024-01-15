package com.example.ChessTournamentBot.util;

public class MyTimer {

    private static long startTime;

    public static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public static boolean hasTimeElapsed(int durationMinutes) {
        return hasTimeElapsed(durationMinutes, 0, 0);
    }

    public static boolean hasTimeElapsed(int durationMinutes, int durationHours) {
        return hasTimeElapsed(durationMinutes, durationHours, 0);
    }

    public static boolean hasTimeElapsed(int durationMinutes, int durationHours, int durationDays) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        int elapsedMinutes = (int) (elapsedTime / (1000 * 60)); // переводим миллисекунды в минуты
        int elapsedHours = elapsedMinutes / 60;
        int elapsedDays = elapsedHours / 24;

        return elapsedDays >= durationDays && elapsedHours % 24 >= durationHours && elapsedMinutes % 60 >= durationMinutes;
    }
}
