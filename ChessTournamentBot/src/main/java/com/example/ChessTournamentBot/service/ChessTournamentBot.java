package com.example.ChessTournamentBot.service;

import com.example.ChessTournamentBot.repos.PlayerRepository;
import com.example.ChessTournamentBot.service.handlers.CommandHandler;
import com.example.ChessTournamentBot.service.handlers.MessageHandler;
import com.example.ChessTournamentBot.util.Emoji;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
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
public class ChessTournamentBot extends TelegramWebhookBot {
    String botPath;
    String botUsername;
    String botToken;

    final MessageHandler messageHandler;
    final CommandHandler commandHandler;
    final UpdateDispatcher updateDispatcher;

    @Autowired
    PlayerRepository playerRepository;

    public ChessTournamentBot(SetWebhook setWebhook, MessageHandler messageHandler,CommandHandler commandHandler) {
        super(String.valueOf(setWebhook));
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.updateDispatcher = new UpdateDispatcher(messageHandler, commandHandler);
        List<BotCommand> listOfChessBotCommands = new ArrayList<>();
        listOfChessBotCommands.add(new BotCommand("/help","for description oll methods of bot"));
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

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            System.out.println(update.getMessage().getChatId());
            return updateDispatcher.distribute(update);
        } catch (IllegalArgumentException e) {
            log.debug("BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getMessage() " + e);
            return null;
        } catch (Exception e) {
            log.debug("BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage() " + e);
            return null;
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


/**
 * Version for LongPolling model
 *
//    public void onUpdateReceived(Update update) {
//        if(update.hasMessage() && update.getMessage().hasText()) {
//            long chatId = update.getMessage().getChatId();
//            if (lastId == chatId && lastCommand.equals(update.getMessage().getText())){
//                return;
//            }else {
//                lastId = chatId;
//                lastCommand = update.getMessage().getText();
//            }
//            if (!update.getMessage().getChat().getType().equals("private")){
//                if ( update.getMessage().getText().contains("@tournament_chess_bot") ||
//                update.getMessage().getText().contains("/set_new_player") || update.getMessage().getText().contains("/update_player_info")) {
//
//                        analiseRU(Parser.parseCommand(update.getMessage().getText()), update.getMessage().getFrom().getUserName(), chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getMessageId());
//            }} else{
//
//                    analiseRU(Parser.parseCommand(update.getMessage().getText()), update.getMessage().getFrom().getUserName(), chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getMessageId());
//            }
//        }
//    }
*/

}