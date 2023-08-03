package com.example.betgame.model;

import lombok.Data;

@Data
public class GameRequest {
    private double bet;
    private int playerNumber;
    public GameRequest(double bet, int playerNumber) {
        this.bet = bet;
        this.playerNumber = playerNumber;
    }
}
