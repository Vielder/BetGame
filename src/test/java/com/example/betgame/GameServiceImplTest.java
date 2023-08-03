package com.example.betgame;
import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import com.example.betgame.service.GameServiceImpl;
import com.example.betgame.util.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameServiceImplTest {

    @Mock
    private RandomNumberGenerator randomNumberGenerator;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlayGame_PlayerNumberInRange() {
        double bet = 50.0;
        int playerNumber = 5;
        int randomNumber = 3;
        when(randomNumberGenerator.generateRandomNumber()).thenReturn(randomNumber);

        GameResult result = gameService.playGame(bet, playerNumber);

        double expectedWin = bet * (99.0 / (100 - playerNumber));
        assertEquals(randomNumber, result.getNumber());
        assertEquals(expectedWin, result.getWin());
    }

    @Test
    public void testPlayGame_PlayerNumberEqualTo100() {
        double bet = 50.0;
        int playerNumber = 100;
        int randomNumber = 60;
        when(randomNumberGenerator.generateRandomNumber()).thenReturn(randomNumber);

        GameResult result = gameService.playGame(bet, playerNumber);

        double expectedWin = bet * 99.0;
        assertEquals(randomNumber, result.getNumber());
        assertEquals(expectedWin, result.getWin());
    }

    @Test
    public void testPlayGame_PlayerNumberGreaterThan100() {
        double bet = 50.0;
        int playerNumber = 120;
        int randomNumber = 60;
        when(randomNumberGenerator.generateRandomNumber()).thenReturn(randomNumber);

        GameResult result = gameService.playGame(bet, playerNumber);

        assertEquals(randomNumber, result.getNumber());
        assertEquals(0.0, result.getWin());
    }

    @Test
    public void testPlayGame_PlayerNumberLessThan1() {
        double bet = 50.0;
        int playerNumber = -5;
        int randomNumber = -30;
        when(randomNumberGenerator.generateRandomNumber()).thenReturn(randomNumber);

        GameResult result = gameService.playGame(bet, playerNumber);

        assertEquals(randomNumber, result.getNumber());
        assertEquals(0.0, result.getWin());
    }

    @Test
    public void testPlayGame_betNotInRange() {
        double bet = -8;
        int playerNumber = 40;
        int randomNumber = 60;
        when(randomNumberGenerator.generateRandomNumber()).thenReturn(randomNumber);

        GameResult result = gameService.playGame(bet, playerNumber);

        assertEquals(randomNumber, result.getNumber());
        assertEquals(0.0, result.getWin());
    }
}
