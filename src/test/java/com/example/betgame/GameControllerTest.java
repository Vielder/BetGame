package com.example.betgame;

import com.example.betgame.controller.GameController;
import com.example.betgame.model.GameRequest;
import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPlayGame() {
        double bet = 50.0;
        int playerNumber = 7;
        GameRequest request = new GameRequest(bet, playerNumber);

        double winAmount = 100.0;
        int resultNumber = 5;
        GameResult expectedResult = new GameResult(winAmount, resultNumber);
        when(gameService.playGame(bet, playerNumber)).thenReturn(expectedResult);

        GameResult actualResult = gameController.playGame(request);

        verify(gameService, times(1)).playGame(bet, playerNumber);
        assertEquals(expectedResult, actualResult);
    }
}
