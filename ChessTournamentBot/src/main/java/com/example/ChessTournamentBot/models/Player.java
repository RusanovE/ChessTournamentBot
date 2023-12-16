package com.example.ChessTournamentBot.models;

import com.example.ChessTournamentBot.util.Emoji;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Player {

    int id;

    String tg_username;

    String chess_nickname;

    String control;

    String f_date;
    public Player(int id, String tg_username, String chess_nickname, String control, String f_date){
        this.id = id;
        this.tg_username = tg_username;
        this.chess_nickname = chess_nickname;
        this.control = control;
        this.f_date = f_date;
    }

    @Override
    public String toString(){
        return "\n "+ Emoji.getRandom() + "   " + "@" + tg_username+ " | "+ chess_nickname + " | " + control + " | " + f_date + "   \n---------------------------------------------------------------" ;
    }
}
