package com.example.betgame;

import com.example.betgame.handlers.GameWebSocketHandler;
import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import com.example.betgame.service.GameServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@SpringBootTest
public class GameWebSocketHandlerTest {
    @Mock
    private WebSocketSession webSocketSession;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        when(webSocketSession.isOpen()).thenReturn(true);
        doNothing().when(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleMessage_ValidInput() throws Exception {
        double bet = 50.0;
        int playerNumber = 3;
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("bet", bet);
        messageData.put("playerNumber", playerNumber);
        String payload = objectMapper.writeValueAsString(messageData);
        TextMessage inputMessage = new TextMessage(payload);

        double win = 80.19;
        int number = 5;
        GameResult gameResult = new GameResult(win, number);
        GameService gameService = mock(GameService.class);
        when(gameService.playGame(anyDouble(), anyInt())).thenReturn(gameResult);

        GameWebSocketHandler gameWebSocketHandler = new GameWebSocketHandler(gameService);
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        gameWebSocketHandler.handleMessage(webSocketSession, inputMessage);

        Map<String, Object> expectedResultData = new HashMap<>();
        expectedResultData.put("number", number);
        expectedResultData.put("win", win);
        String expectedResultJson = new ObjectMapper().writeValueAsString(expectedResultData);

        ArgumentCaptor<TextMessage> textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(webSocketSession).sendMessage(textMessageCaptor.capture());
        String actualResultJson = textMessageCaptor.getValue().getPayload();

        // Compare the JSON content of the actual and expected TextMessages using JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actualResultNode = objectMapper.readTree(actualResultJson);
        JsonNode expectedResultNode = objectMapper.readTree(expectedResultJson);
        assertThat(actualResultNode).isEqualTo(expectedResultNode);

        // Verify that the gameService's playGame method is called with the correct arguments
        verify(gameService).playGame(eq(bet), eq(playerNumber));

        // Verify that the sendMessage method is called exactly once
        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));

    }

}
