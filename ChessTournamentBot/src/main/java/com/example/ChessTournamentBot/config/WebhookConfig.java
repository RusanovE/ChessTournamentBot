package com.example.ChessTournamentBot.config;

import com.example.ChessTournamentBot.service.ChessTournamentBot;
import com.example.ChessTournamentBot.service.handlers.CommandHandler;
import com.example.ChessTournamentBot.service.handlers.MessageHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class WebhookConfig {
    private final BotConfig botConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getPath()).build();
    }

    @Bean
    public ChessTournamentBot springWebhookBot(SetWebhook setWebhook,
                                               MessageHandler messageHandler,
                                               CommandHandler commandHandler) {
        ChessTournamentBot bot = new ChessTournamentBot(setWebhook, messageHandler, commandHandler);

        bot.setBotPath(botConfig.getPath());
        bot.setBotUsername(botConfig.getUsername());
        bot.setBotToken(botConfig.getToken());

        return bot;
    }
}

