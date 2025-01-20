package com.cjron128.chatbot.service;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cjron128.chatbot.config.Config;

import java.io.IOException;

public class AIService {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public String getAIResponse(String userMessage) throws IOException {

        // Define the endpoint and set up the POST request
        String endpoint = "https://api.openai.com/v1/chat/completions";
        HttpPost request = new HttpPost(endpoint);

        // Set headers
        request.setHeader("Authorization", "Bearer " + Config.API_KEY);
        request.setHeader("Content-Type", "application/json");

        // Create the JSON payload
        JSONObject jsonPayload = new JSONObject();

        JSONArray messagesArray = new JSONArray();
        JSONObject messageObject = new JSONObject();
        messageObject.put("role", "user");
        messageObject.put("content", userMessage);
        messagesArray.put(messageObject);

        jsonPayload.put("model", "gpt-4o-mini");
        jsonPayload.put("messages", messagesArray);
        jsonPayload.put("temperature", 0.7);

        // Set the JSON payload as the request entity
        StringEntity entity = new StringEntity(jsonPayload.toString());
        request.setEntity(entity);

        //System.out.println(EntityUtils.toString(request.getEntity())); // uncomment for debugging

        // Execute the request and get the response
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return parseResponse(responseBody);
        }
    }

    private String parseResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);

        // Check if there's an error in the response
        if (jsonResponse.has("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            String errorMessage = error.optString("message", "Unknown error");
            String errorType = error.optString("type", "Unknown type");
            return String.format("Error: %s (Type: %s)", errorMessage, errorType);
        }

        // Extract the response content if there’s no error
        if (jsonResponse.has("choices")) {
            JSONArray choicesArray = jsonResponse.getJSONArray("choices");
            if (choicesArray.length() > 0) {
                JSONObject choice = choicesArray.getJSONObject(0);
                return choice.getJSONObject("message").optString("content", "No content in response.");
            }
        }

        return "Unexpected response format.";
    }
}