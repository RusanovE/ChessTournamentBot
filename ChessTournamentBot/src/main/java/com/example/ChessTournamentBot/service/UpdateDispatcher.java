package com.example.ChessTournamentBot.service;
import com.example.ChessTournamentBot.service.handlers.CommandHandler;
import com.example.ChessTournamentBot.service.handlers.MessageHandler;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDispatcher {
    final MessageHandler messageHandler;
    final CommandHandler commandHandler;

    @Autowired
    public UpdateDispatcher(MessageHandler messageHandler, CommandHandler commandHandler) {
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
    }

    public BotApiMethod<?> distribute(Update update) {
        if (update.hasMessage() ) {
            if (update.getMessage().hasText() && update.getMessage().getText().charAt(0) == '/' ) {
                return commandHandler.answer(update);
            } else{
                return messageHandler.answer(update.getMessage());
            }
        } else{
            return messageHandler.answer(update.getMessage());
        }

    }

}