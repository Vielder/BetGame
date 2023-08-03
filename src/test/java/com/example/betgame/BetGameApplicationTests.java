package com.example.betgame;

import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import com.example.betgame.service.GameServiceImpl;
import com.example.betgame.util.RandomNumberGenerator;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({Math.class})
class BetGameApplicationTests {

    @Autowired
    private GameService gameService;
    private List<GameResult> gameResult;

    @Test
    public void testGameService() {
        RandomNumberGenerator mockRandomNumberGenerator = () -> 42;

        GameService gameService = new GameServiceImpl(mockRandomNumberGenerator, gameResult);

        // Test case 1: Player's number is greater than the random number
        GameResult result1 = gameService.playGame(40.5, 50);
        assertEquals(80.19, result1.getWin(), 0.01);

        // Test case 2: Player's number is less than the random number
        GameResult result2 = gameService.playGame(50, 30);
        assertEquals(0, result2.getWin(), 0.01);
    }


    @Ignore
    @Test
    public void testRtpCalculation() throws InterruptedException, ExecutionException {
        int numberOfRounds = 1000000;
        int numberOfThreads = 24;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CompletionService<Double> completionService = new ExecutorCompletionService<>(executorService);

        double totalSpent = 0;
        double totalWon = 0;

        // Submit tasks to play the rounds in parallel
        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfRounds; i++) {
            futures.add(completionService.submit(() -> playRound()));
        }

        // Collect the results from the completed tasks
        for (Future<Double> future : futures) {
            double result = future.get();
            totalWon += result;
            totalSpent += 1;
        }

        executorService.shutdown();

        if (totalSpent > 0) {
            double rtp = (totalWon / totalSpent) * 100;
            System.out.println();
            System.out.println( "Total won: " + totalWon + "\n" +
                                "Total spent: " + totalSpent + "\n" +
                                "RTP: " + rtp + "%");
        } else {
            System.out.println("No rounds played. RTP cannot be calculated.");
        }
    }

    private double playRound() {
        // Simulate a single round of the game
        double bet = 1;
        int playerNumber = (int) (Math.random() * 99) + 1;

        return gameService.playGame(bet, playerNumber).getWin();
    }

}
