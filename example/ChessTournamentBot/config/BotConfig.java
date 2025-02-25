package com.example.ChessTournamentBot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@Configuration
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {

//    @Value("${bot.path}")
//    String path;

    @Value("${bot.username}")
    String username;

    @Value("${bot.token}")
    String token;
}
