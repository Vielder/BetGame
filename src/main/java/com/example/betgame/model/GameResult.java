package com.example.betgame.model;

import lombok.Data;

@Data
public class GameResult {
    private double win;
    private int number;
    public GameResult(double win, int number) {
        this.win = win;
        this.number = number;
    }
}
