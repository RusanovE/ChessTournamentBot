package com.example.ChessTournamentBot.repos;

import com.example.ChessTournamentBot.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity,Long> {
    boolean existsByChatId(long chatId);

    boolean existsByNickTg(String nickTg);

    @Transactional
    void deleteByChatId(long chatId);

    PlayerEntity findByChatId(long chatId);
}
