package com.example.betgame;

import com.example.betgame.handlers.GameWebSocketHandler;
import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
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

    @Test
    public void testHandleMessage_InvalidInput() throws Exception {
        double invalidBet = -10.0;
        int invalidPlayerNumber = 0;
        Map<String, Object> invalidMessageData = new HashMap<>();
        invalidMessageData.put("bet", invalidBet);
        invalidMessageData.put("playerNumber", invalidPlayerNumber);
        String invalidPayload = objectMapper.writeValueAsString(invalidMessageData);
        TextMessage invalidInputMessage = new TextMessage(invalidPayload);

        GameWebSocketHandler gameWebSocketHandler = new GameWebSocketHandler(mock(GameService.class));
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        ArgumentCaptor<TextMessage> textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        gameWebSocketHandler.handleMessage(webSocketSession, invalidInputMessage);

        Map<String, Object> expectedErrorData = new HashMap<>();
        expectedErrorData.put("error", "Invalid input");
        expectedErrorData.put("message", "bet and playerNumber should be valid values");
        String expectedErrorJson = objectMapper.writeValueAsString(expectedErrorData);

        verify(webSocketSession).sendMessage(textMessageCaptor.capture());
        String actualErrorJson = textMessageCaptor.getValue().getPayload();

        JsonNode actualErrorNode = objectMapper.readTree(actualErrorJson);
        JsonNode expectedErrorNode = objectMapper.readTree(expectedErrorJson);
        assertThat(actualErrorNode).isEqualTo(expectedErrorNode);
    }

    @Test
    public void testHandleMessage_NullInput() throws Exception {
        Double nullBet = null;
        Integer nullPlayerNumber = null;
        Map<String, Object> nullMessageData = new HashMap<>();
        nullMessageData.put("bet", nullBet);
        nullMessageData.put("playerNumber", nullPlayerNumber);
        String nullPayload = objectMapper.writeValueAsString(nullMessageData);
        TextMessage nullInputMessage = new TextMessage(nullPayload);

        GameWebSocketHandler gameWebSocketHandler = new GameWebSocketHandler(mock(GameService.class));
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        ArgumentCaptor<TextMessage> textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        gameWebSocketHandler.handleMessage(webSocketSession, nullInputMessage);

        Map<String, Object> expectedErrorData = new HashMap<>();
        expectedErrorData.put("error", "Invalid input");
        expectedErrorData.put("message", "bet and playerNumber should not be null");
        String expectedErrorJson = objectMapper.writeValueAsString(expectedErrorData);

        verify(webSocketSession).sendMessage(textMessageCaptor.capture());
        String actualErrorJson = textMessageCaptor.getValue().getPayload();

        JsonNode actualErrorNode = objectMapper.readTree(actualErrorJson);
        JsonNode expectedErrorNode = objectMapper.readTree(expectedErrorJson);
        assertThat(actualErrorNode).isEqualTo(expectedErrorNode);
    }

    @Test
    public void testHandleMessage_MissingRequiredKeys() throws Exception {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("invalidKey", "invalidValue"); // No "bet" and "playerNumber" keys
        String payload = objectMapper.writeValueAsString(messageData);
        TextMessage inputMessage = new TextMessage(payload);

        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        GameWebSocketHandler gameWebSocketHandler = new GameWebSocketHandler(mock(GameService.class));
        gameWebSocketHandler.handleMessage(webSocketSession, inputMessage);

        Map<String, Object> expectedResultData = new HashMap<>();
        expectedResultData.put("error", "Invalid input (null values)");
        expectedResultData.put("message", "bet and playerNumber are required fields");
        String expectedResultJson = new ObjectMapper().writeValueAsString(expectedResultData);

        ArgumentCaptor<TextMessage> textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(webSocketSession).sendMessage(textMessageCaptor.capture());
        String actualResultJson = textMessageCaptor.getValue().getPayload();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actualResultNode = objectMapper.readTree(actualResultJson);
        JsonNode expectedResultNode = objectMapper.readTree(expectedResultJson);
        assertThat(actualResultNode).isEqualTo(expectedResultNode);

        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));
    }


}
