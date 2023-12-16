package com.example.ChessTournamentBot.service.handlers;

import com.example.ChessTournamentBot.entity.PlayerEntity;
import com.example.ChessTournamentBot.repos.PlayerRepository;
import com.example.ChessTournamentBot.util.Emoji;
import com.example.ChessTournamentBot.util.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommandHandler {

    @Autowired
    private PlayerRepository playerRepository;
    public BotApiMethod<?> answer(Update  update) {
        String command = update.getMessage().getText();
        String[] parsedInfo = Parser.parseCommand(command);
        if (command.contains("@tournament_chess_bot")){
            command = command.replace("@tournament_chess_bot","");
        }
        if (command.contains("/set_new_player") && parsedInfo.length == 4){
            return receivingSetNewPlayerCommand(
                    update.getMessage().getChatId(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getUserName(),
                    parsedInfo[1], parsedInfo[2],parsedInfo[3],
                    update.getMessage().getMessageId());
        } else if (command.contains("/update_player_info") && parsedInfo.length == 4){
            return receivingUpdatePlayerInfoCommand(
                    update.getMessage().getChatId(),
                    parsedInfo[1],parsedInfo[2],parsedInfo[3],
                    update.getMessage().getMessageId());

        }

        switch (command) {
            case ("/start") -> {
                return receivingStartCommand(
                        update.getMessage().getChatId(),
                        update.getMessage().getMessageId(),
                        update.getMessage().getFrom().getFirstName());
            }
            case ("/help") -> {
                return receivingHelpCommand(update.getMessage().getChatId(),update.getMessage().getMessageId());
            }
            case ("/need_tournament") -> {
                return  receivingNeedTournamentCommand(update.getMessage().getChatId(),update.getMessage().getMessageId());
            }
            case ("/get_all_players") -> {
                return receivingGetAllRegisteredPlayersCommand(update.getMessage().getChatId(),update.getMessage().getMessageId());
            }
            case "/set_new_player" ->{
                return  SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("""
                                Не верный синтаксис команды!!!

                                Вид команды должен быть таким -- /set_new_player "Ваш шахматный ник "Предпочитаемый контроль "Удобное для вас время игры\s
                                
                                """)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build();
            }
            case "/update_player_info" ->{
                return  SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("""
                                Не верный синтаксис команды!!!

                                Вид команды должен быть таким -- /update_player_info "Ваш шахматный ник "Предпочитаемый контроль "Удобное для вас время игры\s
                                
                                """)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build();
            }
            case ("/delete_player") -> {
                return receivingDeletePlayerCommand(
                        update.getMessage().getChatId(),
                        update.getMessage().getMessageId());
            }
            case ("/get_target_file") -> {
                return receivingGetTargetFileCommand(update.getMessage().getChatId(),update.getMessage().getMessageId());
            }
            default -> {
                return null;
            }
        }
    }
    public BotApiMethod<?> receivingStartCommand(long chatId, int messageId, String name){
        String answer = "Здравствуй, " + name + ", Я начал работать";

        return  SendMessage.builder()
                .chatId(chatId)
                .text(answer)
                .replyToMessageId(messageId)
                .build();
    }

    public BotApiMethod<?> receivingHelpCommand(long chatId, int messageId){
        String upperCaseTitle;
        String mainText;
        upperCaseTitle = "Бот помогает в проведении регистрации на турниры и собрании информации про участников\n\n";
        mainText = """
                Вы можете оперировать такими командами:

                Системные команды:
                /start - для запуска бота
                /help - для помощи в понимании управления ботом

                Основные команды:
                /set_new_player - для регистрации на турнир
                /get_all_players - для получения списка зарегистрированных (Ввиде сообщения)
                /update_player_info - для обновления информации про участника
                /delete_player - для удаления участника из списка

                Дополнительные команды:
                /get_target_file - для получения списка зарегистрированных (Excel file)

                """;


       return SendMessage.builder()
               .chatId(chatId)
               .text(upperCaseTitle.toUpperCase()+mainText)
               .replyToMessageId(messageId)
               .build();
    }

    public BotApiMethod<?> receivingNeedTournamentCommand(long chatId, int messageId){
        List<String> listOfOptionsOfPol = new ArrayList<>();

        listOfOptionsOfPol.add("Дайте два!");
        listOfOptionsOfPol.add("Обойдусь как-нибудь");
        listOfOptionsOfPol.add("Неуверен");

        SendPoll sendPoll = new SendPoll();

        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setQuestion("Не хотите ли турнирчик?");
        sendPoll.setOptions(listOfOptionsOfPol);
        sendPoll.setIsAnonymous(false);
        sendPoll.setReplyToMessageId(messageId);

        return sendPoll;
    }

    public BotApiMethod<?> receivingGetAllRegisteredPlayersCommand (long chatId, int messageId) {
        try {
            List<?> allPlayers = playerRepository.findAll();

            if (allPlayers.isEmpty()){
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Пробил по базе. Людишек не наблюдается " + Emoji.SCULL.getValue())
                        .replyToMessageId(messageId)
                        .build();
            }else return SendMessage.builder()
                    .chatId(chatId)
                    .text(allPlayers.toString())
                    .replyToMessageId(messageId)
                    .build();

        } catch (Exception e) {
            log.error("Can`t get info about all players " + e.getMessage());
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Произошла ошибка. Попробуйте позже...")
                    .replyToMessageId(messageId)
                    .build();
        }
    }

    public BotApiMethod<?> receivingSetNewPlayerCommand(long chatId, String tg_nick , String tg_username, String chess_nickname, String control, String f_date, int messageId) {
      try {
          if (playerRepository.existsByChatId(chatId)){
              return SendMessage.builder()
                      .chatId(chatId)
                      .text("Произошла ошибка. Участник уже был добавлен")
                      .replyToMessageId(messageId)
                      .build();
          }else {
              PlayerEntity newPlayer = new PlayerEntity(chatId, tg_nick, tg_username,chess_nickname,control,f_date);
              playerRepository.save(newPlayer);

              log.info("New player registered successfully");
              return SendMessage.builder()
                      .chatId(chatId)
                      .text("Новый участник успешно добавлен")
                      .replyToMessageId(messageId)
                      .build();
          }

      } catch (Exception e) {
          log.error("Can`t set information about new player " + e.getMessage());
          return SendMessage.builder()
                  .chatId(chatId)
                  .text("Произошла ошибка. Участник не был добавлен")
                  .replyToMessageId(messageId)
                  .build();
      }
    }

    public BotApiMethod<?> receivingUpdatePlayerInfoCommand(long chatId, String chess_nickname, String control, String f_date, int messageId){

        try {
            if (!playerRepository.existsByChatId(chatId)){
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Произошла ошибка. Как можно обновить данные об участнике который не зарегистрировался. Ну головой думай... "
                                + Emoji.CRIPPLE.getValue())
                        .replyToMessageId(messageId)
                        .build();
            }else {
                PlayerEntity updatePlayer = playerRepository.findByChatId(chatId);
                updatePlayer.setChess_nickname(chess_nickname);
                updatePlayer.setControl(control);
                updatePlayer.setF_date(f_date);
                playerRepository.save(updatePlayer);

                log.info("updated successfully");
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Информация успешно обновлена")
                        .replyToMessageId(messageId)
                        .build();
            }
           } catch (Exception e) {
            log.error("Can`t update info about player" + e.getMessage());
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Произошла ошибка. Обновление информации невозможно")
                    .replyToMessageId(messageId)
                    .build();
        }
       }

    public BotApiMethod<?> receivingDeletePlayerCommand(long chatId, int messageId){

        try {
            if (playerRepository.existsByChatId(chatId)){
                playerRepository.deleteByChatId(chatId);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Участник успешно удален")
                        .replyToMessageId(messageId)
                        .build();
            }else {
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Участник не найден")
                        .replyToMessageId(messageId)
                        .build();
            }
        } catch (Exception e) {
            log.error("Can`t delete current player " + e.getMessage());
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Произошла ошибка. Такого участника нет")
                    .replyToMessageId(messageId)
                    .build();
        }
    }

        public BotApiMethod<?> receivingGetTargetFileCommand (long chatId, int messageId) {
        // TODO sendDocument
//        return SendDocument.builder()
//                .chatId(chatId)
//                .document(new InputFile(ExcelFileCreator.createExcelFile(playerRepository.findAll())))
//                .replyToMessageId(messageId)
//                .build();
             return SendMessage.builder()
                    .chatId(chatId)
                    .text("Еще не сделано до конца")
                    .replyToMessageId(messageId)
                    .build();
    }
}
