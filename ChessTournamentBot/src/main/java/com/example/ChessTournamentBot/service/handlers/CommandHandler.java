package com.example.ChessTournamentBot.service.handlers;

import com.example.ChessTournamentBot.entity.PlayerEntity;
import com.example.ChessTournamentBot.repos.PlayerRepository;
import com.example.ChessTournamentBot.util.Emoji;
import com.example.ChessTournamentBot.util.ExcelFileCreator;
import com.example.ChessTournamentBot.util.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommandHandler {

    @Autowired
    private PlayerRepository playerRepository;
    public BotApiMethod<?> answer(Update  update) {

        // Command
        String command = update.getMessage().getText();
        // ChatId
        long chatId = update.getMessage().getChatId();
        // UserId
        long userId = update.getMessage().getFrom().getId();
        // TgNick
        String tg_nick = update.getMessage().getFrom().getFirstName();
        // TgUsername
        String tg_username = update.getMessage().getFrom().getUserName();
        // MessageId
        int messageId = update.getMessage().getMessageId();

        String[] parsedInfo = Parser.parseCommand(command);
        if (command.contains("@tournament_chess_bot")){
            command = command.replace("@tournament_chess_bot","");
        }
        if (command.contains("/set_new_player") && parsedInfo.length == 4){

            return receivingSetNewPlayerCommand(chatId, userId, tg_nick, tg_username, parsedInfo[1], parsedInfo[2],parsedInfo[3], messageId);

        } else if (command.contains("/update_player_info") && parsedInfo.length == 4){

            return receivingUpdatePlayerInfoCommand(chatId, userId, parsedInfo[1],parsedInfo[2],parsedInfo[3], messageId);
        }

        switch (command) {
            case ("/start") -> {
                return receivingStartCommand(chatId, messageId, tg_nick);
            }
            case ("/help") -> {
                return receivingHelpCommand(chatId,messageId);
            }
            case ("/need_tournament") -> {
                return  receivingNeedTournamentCommand(chatId,messageId);
            }
            case ("/get_all_players") -> {
                return receivingGetAllRegisteredPlayersCommand(chatId,messageId);
            }
            case "/set_new_player" ->{
                return  SendMessage.builder()
                        .chatId(chatId)
                        .text("""
                                Не верный синтаксис команды!!!

                                Вид команды должен быть таким -- /set_new_player "Ваш шахматный ник "Предпочитаемый контроль "Удобное для вас время игры\s

                                """)
                        .replyToMessageId(messageId)
                        .build();
            }
            case "/update_player_info" ->{
                return  SendMessage.builder()
                        .chatId(chatId)
                        .text("""
                                Не верный синтаксис команды!!!

                                Вид команды должен быть таким -- /update_player_info "Ваш шахматный ник "Предпочитаемый контроль "Удобное для вас время игры\s

                                """)
                        .replyToMessageId(messageId)
                        .build();
            }
            case ("/delete_player") -> {
                return receivingDeletePlayerCommand(chatId, userId, messageId, tg_nick);
            }
            case "/a d" -> {   // ToDo add usefully command
                if (tg_nick.toLowerCase().contains("трикстер") && tg_username.contains("Ya_v_domike")){

                playerRepository.deleteAll();
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Готово, сир")
                        .replyToMessageId(messageId)
                        .build();
                } else return SendMessage.builder()
                        .chatId(chatId)
                        .text("Наглый людищка, тебе не доступна магия богов!")
                        .replyToMessageId(messageId)
                        .build();
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

    public BotApiMethod<?> receivingSetNewPlayerCommand(long chatId, long userId, String tg_nick , String tg_username, String chess_nickname, String control, String f_date, int messageId) {
      try {
          if (playerRepository.existsByChatId(userId) || playerRepository.existsByNickTg(tg_nick)){
              return SendMessage.builder()
                      .chatId(chatId)
                      .text("Ты уже регистрировался. Ты слабоумный, да? " + Emoji.PILL.getValue())
                      .replyToMessageId(messageId)
                      .build();
          }else {
              PlayerEntity newPlayer = new PlayerEntity(userId, tg_nick, tg_username, chess_nickname, control, f_date);
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

    public BotApiMethod<?> receivingUpdatePlayerInfoCommand(long chatId, long userId, String chess_nickname, String control, String f_date, int messageId){

        try {
            if (!playerRepository.existsByChatId(userId)){
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Как можно обновить данные об участнике который не зарегистрировался. Ну головой думай... "
                                + Emoji.CRIPPLE.getValue())
                        .replyToMessageId(messageId)
                        .build();
            }else {
                PlayerEntity updatePlayer = playerRepository.findByChatId(userId);
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

    public BotApiMethod<?> receivingDeletePlayerCommand(long chatId, long userId, int messageId, String tg_nick){

        try {
            if (playerRepository.existsByChatId(userId) | playerRepository.existsByNickTg(tg_nick)){
                playerRepository.deleteByChatId(userId);
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
                    .text("Произошла ошибка. Удаление прервано")
                    .replyToMessageId(messageId)
                    .build();
        }
    }

        public SendDocument receivingGetTargetFileCommand (long chatId, int messageId) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(new InputFile(ExcelFileCreator.createExcelFile(playerRepository.findAll())))
                .replyToMessageId(messageId)
                .build();
    }
}
