package com.example.ChessTournamentBot.util;

public class Parser {
    static String[] parsedCommand;
    static public String[] parseCommand(String origText){
            String parsedText = "";
            if (origText != null && origText.contains("/")) parsedText = origText.trim();
            parsedCommand = parsedText.split(" \"");
            return parsedCommand;
    }
}