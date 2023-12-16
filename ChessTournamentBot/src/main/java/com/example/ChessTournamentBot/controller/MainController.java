package com.example.ChessTournamentBot.controller;

import com.example.ChessTournamentBot.service.ChessTournamentBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@AllArgsConstructor
public class MainController {
    private final ChessTournamentBot chessTournamentBot;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return chessTournamentBot.onWebhookUpdateReceived(update);
    }
}