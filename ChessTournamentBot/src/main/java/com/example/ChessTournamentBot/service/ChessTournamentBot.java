package com.example.ChessTournamentBot.service;

import com.example.ChessTournamentBot.config.BotConfig;
import com.example.ChessTournamentBot.service.handlers.CommandHandler;
import com.example.ChessTournamentBot.service.handlers.MessageHandler;
import com.example.ChessTournamentBot.util.Emoji;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@Getter
@Setter
public class ChessTournamentBot extends TelegramLongPollingBot {

    final BotConfig botConfig;
    final MessageHandler messageHandler;
    final CommandHandler commandHandler;
    final UpdateDispatcher updateDispatcher;


    public ChessTournamentBot(BotConfig botConfig, MessageHandler messageHandler, CommandHandler commandHandler) {
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.updateDispatcher = new UpdateDispatcher(messageHandler, commandHandler);
        List<BotCommand> listOfChessBotCommands = new ArrayList<>();
        listOfChessBotCommands.add(new BotCommand("/help","for description all methods of bot"));
        listOfChessBotCommands.add(new BotCommand("/need_tournament","for create poll about new tournament"));
        listOfChessBotCommands.add(new BotCommand("/get_all_players","for get list of registered players with all info "));
        listOfChessBotCommands.add(new BotCommand("/set_new_player","for registration new player"));
        listOfChessBotCommands.add(new BotCommand("/update_player_info","for update info about registered player"));
        listOfChessBotCommands.add(new BotCommand("/delete_player","for delete registered player"));
        listOfChessBotCommands.add(new BotCommand("/get_target_file","for get list of registered players with all info as Excel file"));
        try {
            execute(new SetMyCommands(listOfChessBotCommands, new BotCommandScopeDefault(),null));
        }catch (TelegramApiException e){
            log.error("error with list of command" + e.getMessage());
        }
    }

    @Scheduled(cron = "${cron}")
    public void schedule(){
        long chatId = 1433161568;
        SendMessage sendMessage =  SendMessage.builder()
                .chatId("-100"+chatId)
                .text("С новым годом ,людишки!!!" + Emoji.BOT_FACE.getValue())
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.debug("Schedule error " + e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.getMessage().getText().equalsIgnoreCase("Серега сыграй") || update.getMessage().getText().equalsIgnoreCase("Серёга сыграй")) {
                execute(messageHandler.sendSeregaMusic(update.getMessage().getChatId(), update.getMessage().getMessageId()));

            } else if (update.getMessage().getText().contains("/get_target_file")){

                execute(commandHandler.receivingGetTargetFileCommand(update.getMessage().getChatId(),update.getMessage().getMessageId()));

            }else execute(updateDispatcher.distribute(update));

        } catch (IllegalArgumentException e) {
            log.debug("BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE " + e);
        } catch (Exception e) {
            log.debug("BotMessageEnum.EXCEPTION_WHAT_THE_FUCK " + e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}