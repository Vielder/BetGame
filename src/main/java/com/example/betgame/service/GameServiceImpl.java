package com.example.betgame.service;

import com.example.betgame.model.GameResult;
import com.example.betgame.util.RandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private final RandomNumberGenerator randomNumberGenerator;
    private final List<GameResult> gameResults;

    @Autowired
    public GameServiceImpl(RandomNumberGenerator randomNumberGenerator, List<GameResult> gameResults) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.gameResults = gameResults;
    }

    @Override
    public GameResult playGame(double bet, int playerNumber){
        int randomNumber = randomNumberGenerator.generateRandomNumber();
        double win;
        if (playerNumber < 100 && playerNumber > 0 && bet > 0) {
            win = playerNumber > randomNumber ? bet * (99.0 / (100 - playerNumber)) : 0.0;
        } else if (playerNumber == 100 && bet > 0) {
            // if it is not possible to calculate (99 / 0) then calculate without this part
            win = playerNumber > randomNumber ? bet * 99.0 : 0.0;
        } else {
            win = 0;
        }
        GameResult gameResult = new GameResult(win, randomNumber);
        return gameResult;
    }

}
