package com.example.ChessTournamentBot.entity;

import com.example.ChessTournamentBot.util.Emoji;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    long chatId;

    @Column(nullable = false)
    String usernameTg;

    @Column(nullable = false)
    String nickTg;

    @Column(nullable = false)
    String chess_nickname;

    @Column(nullable = false)
    String control;

    @Column(nullable = false)
    String f_date;

   public PlayerEntity(){

   }

    public PlayerEntity(long chatId,String nickTg, String usernameTg, String chess_nickname, String control, String f_date){
        this.chatId = chatId;
        this.nickTg = nickTg;
        this.usernameTg = usernameTg;
        this.chess_nickname = chess_nickname;
        this.control = control;
        this.f_date = f_date;
    }

    @Override
    public String toString(){
        return "\n "+ Emoji.getRandom() + "   " + "@" + usernameTg + " | "+ chess_nickname + " | " + control + " | " + f_date + "   \n---------------------------------------------------------------" ;
    }
}
