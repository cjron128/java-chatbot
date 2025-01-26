package com.cjron128.chatbot.service;

import com.cjron128.chatbot.config.Config;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ConversationDAO conversationDAO = new ConversationDAO();

    public UUID getCurrentConversationId() {
        return conversationDAO.getActiveConversationId();
    }

    public void resetConversation() {
        conversationDAO.resetConversationId();
    }

    public String getAIResponse(String userMessage) throws IOException {
        UUID conversationId = getCurrentConversationId();

        logger.info("[User]: {}", userMessage);

        System.out.println("Using conversation ID: " + conversationId);

        // Save user message and retrieve chat history, as implemented previously
        conversationDAO.saveMessage(conversationId, "user", userMessage);

        // Save the user's message to the database
        conversationDAO.saveMessage(conversationId, "user", userMessage);

        // Retrieve the conversation history
        List<String> history = conversationDAO.getConversation(conversationId);

        // Prepare messages array for OpenAI API
        JSONArray messagesArray = new JSONArray();
        for (String message : history) {
            String[] parts = message.split(": ", 2);
            if (parts.length == 2) {
                JSONObject messageObject = new JSONObject();
                messageObject.put("role", parts[0]);
                messageObject.put("content", parts[1]);
                messagesArray.put(messageObject);
            }
        }

        // Define the endpoint and set up the POST request
        String endpoint = "https://api.openai.com/v1/chat/completions";
        HttpPost request = new HttpPost(endpoint);

        // Set headers
        request.setHeader("Authorization", "Bearer " + Config.API_KEY);
        request.setHeader("Content-Type", "application/json");

        // Create the JSON payload
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", "gpt-4");
        jsonPayload.put("messages", messagesArray);
        jsonPayload.put("temperature", 0.7);

        // Set the JSON payload as the request entity
        StringEntity entity = new StringEntity(jsonPayload.toString());
        request.setEntity(entity);

        // Execute the request and get the response
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            String aiResponse = parseResponse(responseBody);

            // Save the AI's response to the database
            conversationDAO.saveMessage(conversationId, "assistant", aiResponse);

            logger.info("[ChatGPT]: {}", aiResponse);
            return aiResponse;
        }
    }

    private String parseResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);

        if (jsonResponse.has("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            String errorMessage = error.optString("message", "Unknown error");
            return "Error: " + errorMessage;
        }

        if (jsonResponse.has("choices")) {
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content");
            }
        }

        return "Unexpected response format.";
    }
}
