package com.example.ChessTournamentBot.service.handlers;

import com.example.ChessTournamentBot.util.Randomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MessageHandler {

    /**
     * Не для гитхаба
     */

    private static final Map<Integer, String> jokesMap = new HashMap<>();
    private static final Map<Integer, String> wisdomMap = new HashMap<>();
    public BotApiMethod<?> answer(Message message) {

        long chatId = message.getChatId();
        int replayId = message.getMessageId();
        String textMessage = message.getText();

        switch (textMessage){
            case "анекдот", "Анекдот", "прикол", "Шутка", "шутка" -> {
                return sendJoke(chatId, replayId);
            }
            case "Леган", "леган" -> {
                return sendLeganText(chatId, replayId);
            }
            case "мудрость", "Мудрость" -> {
                return sendWisdom(chatId, replayId);
            }
            default -> {
                return null;
            }
        }
    }

    private BotApiMethod<?> sendWisdom(long chatId, int replayId) {

        int randomWisdomNumber = Randomizer.generateRandomNumber(wisdomMap.size());
        String randomWisdom = wisdomMap.get(randomWisdomNumber);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .replyToMessageId(replayId)
                .text(randomWisdom)
                .build();
    }

    public SendAudio sendSeregaMusic(long chatId, int replayId) {

        return SendAudio.builder()
                .replyToMessageId(replayId)
                .audio(new InputFile(new File("D:\\Github\\JavaProject\\ChessTournamentBot\\src\\main\\resources\\music\\" + Randomizer.generateRandomNumber(27) + ".mp3")))
                .chatId(chatId)
                .caption("Как вам такое например?")
                .build();
    }

    // /home/ubuntu/ChessTournamentBot/src/main/resources/jokes.txt

    private BotApiMethod<?> sendLeganText(long chatId, int replayId) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyToMessageId(replayId)
                .text("""
                        Призрак какой-то неведомой силы,
                        Ты ль, указавший законы судьбе,
                        Ты ль, император, во мраке могилы
                        Хочешь, чтоб я говорил о тебе?""")
                .build();
    }

    // Метод для отправки случайного анекдота
    private BotApiMethod<?> sendJoke(long chatId, int replayId) {

        int randomJokeNumber = Randomizer.generateRandomNumber(jokesMap.size());
        String randomJoke = jokesMap.get(randomJokeNumber);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .replyToMessageId(replayId)
                .text(randomJoke)
                .build();
    }

    // Метод для загрузки из файлов
    static {
        loadJokesFromFile();
        loadWisdomFromFile();
        log.info("Complete read files");
    }

    //ubuntu@3.79.105.101/home/ubuntu/ChessTournamentBot/src/main/resources/jokes.txt

    // Метод для загрузки анекдотов из файла
    private static synchronized void loadJokesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\Github\\JavaProject\\ChessTournamentBot\\src\\main\\resources\\jokes.txt"))) {
            String line;
            int jokeNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.contains("----------")) {
                    jokeNumber++;
                } else {
                    jokesMap.put(jokeNumber, jokesMap.getOrDefault(jokeNumber, "") + line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void loadWisdomFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\Github\\JavaProject\\ChessTournamentBot\\src\\main\\resources\\wisdoms.txt"))) {
            String line;
            int wisdomNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.contains("----------")) {
                    wisdomNumber++;
                } else {
                    wisdomMap.put(wisdomNumber, wisdomMap.getOrDefault(wisdomNumber, "") + line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
