package com.example.betgame.service;

import com.example.betgame.model.GameResult;

public interface GameService {
    GameResult playGame(double bet, int playerNumber);
}
