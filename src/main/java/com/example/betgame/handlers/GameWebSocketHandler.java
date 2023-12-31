package com.example.betgame.handlers;

import com.example.betgame.model.GameResult;
import com.example.betgame.service.GameService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class GameWebSocketHandler implements WebSocketHandler {

    @Autowired
    private GameService gameService;

    @Autowired
    public GameWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        ObjectMapper objectMapper = null;
        if (message instanceof TextMessage textMessage) {
            String payload = textMessage.getPayload();
            objectMapper = new ObjectMapper();
            Map<String, Object> messageData = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });

            if (!messageData.containsKey("bet") || !messageData.containsKey("playerNumber")) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Invalid input (null values)");
                errorData.put("message", "bet and playerNumber are required fields");
                String errorJson = objectMapper.writeValueAsString(errorData);
                session.sendMessage(new TextMessage(errorJson));
                return;
            }


            if (Objects.isNull(messageData.get("bet")) || Objects.isNull(messageData.get("playerNumber")) || messageData.get("bet")=="" || messageData.get("playerNumber")=="") {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Invalid input");
                errorData.put("message", "bet and playerNumber should not be null");
                String errorJson = objectMapper.writeValueAsString(errorData);
                session.sendMessage(new TextMessage(errorJson));
                return;
            }

            Double bet = (Double) messageData.get("bet");
            Integer playerNumber = (Integer) messageData.get("playerNumber");
            if (bet <= 0 || playerNumber <= 0 || playerNumber > 100) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Invalid input");
                errorData.put("message", "bet and playerNumber should be valid values");
                String errorJson = objectMapper.writeValueAsString(errorData);
                session.sendMessage(new TextMessage(errorJson));
                return;
            }

            // Processing the bet and getting the results of the game
            GameResult result = gameService.playGame(bet, playerNumber);

            // Convert game results to JSON and send back to client
            String resultJson = objectMapper.writeValueAsString(result);
            session.sendMessage(new TextMessage(resultJson));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}