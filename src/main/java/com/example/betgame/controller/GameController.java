package com.example.betgame.controller;

import com.example.betgame.model.GameRequest;
import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/play")
    public GameResult playGame(GameRequest request) {
        return gameService.playGame(request.getBet(), request.getPlayerNumber());
    }

}
