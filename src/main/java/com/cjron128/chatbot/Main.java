package com.cjron128.chatbot;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cjron128.chatbot.service.AIService;

public class Main{
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Initialize AIService
        AIService aiService = new AIService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nNow chatting with gpt-4o-mini. Type 'exit' to end the conversation.\n");

        while (true) {
            System.out.println("\nType Message and hit 'enter': ");
            String userInput = scanner.nextLine();

            // Exit the loop if the user types 'exit'
            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("exiting...");
                break;
            }

            try {
                // Use AIService to get a response from the AI API
                String response = aiService.getAIResponse(userInput);

                System.out.println("\nUser: " + userInput);
                System.out.println("\nBot: " + response);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error occurred while fetching AI response: ", e);
            }
        }

        // Clean up resources
        scanner.close();
    }
}
