package com.cjron128.chatbot;

import com.cjron128.chatbot.service.AIService;
import com.cjron128.chatbot.service.ConversationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Process logViewerProcess;

    public static void main(String[] args) {
        // Start the conversation log viewer in a new terminal
        startLogViewer();

        AIService aiService = new AIService();
        ConversationDAO conversationDAO = new ConversationDAO();
        Scanner scanner = new Scanner(System.in);

        UUID conversationId = aiService.getCurrentConversationId();
        logger.info("Resuming conversation with ID: {}", conversationId);
        System.out.println("Now chatting with GPT-4. Type 'reset' to start a new conversation, 'wipe' to clear the database, or 'exit' to end.");

        while (true) {
            System.out.print("\nYou: ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Goodbye!");
                logger.info("Conversation ended.");
                closeLogViewer(); // Close the log viewer before exiting
                break;
            }

            if ("reset".equalsIgnoreCase(userInput)) {
                aiService.resetConversation();
                conversationId = aiService.getCurrentConversationId();
                System.out.println("Conversation reset. New conversation ID: " + conversationId);
                logger.info("Conversation reset. New conversation ID: {}", conversationId);
                continue;
            }

            if ("wipe".equalsIgnoreCase(userInput)) {
                conversationDAO.wipeDatabase();
                conversationId = aiService.getCurrentConversationId(); // Fetch the new active conversation ID
                logger.info("Database wiped. New conversation ID: {}", conversationId);
                continue;
            }

            try {
                String response = aiService.getAIResponse(userInput);
                System.out.println("Bot: " + response);
            } catch (Exception e) {
                logger.error("Error occurred while fetching AI response: ", e);
            }
        }

        scanner.close();
    }

    private static void startLogViewer() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows command to open a new terminal and run `tail -f conversation.log`
                logViewerProcess = Runtime.getRuntime().exec(new String[]{
                    "cmd.exe", "/c", "start", "cmd.exe", "/k", "powershell.exe -Command Get-Content conversation.log -Wait -Tail 10"
                });
            } else if (os.contains("mac")) {
                // macOS command to open a new terminal and run `tail -f conversation.log`
                logViewerProcess = Runtime.getRuntime().exec(new String[]{
                    "open", "-a", "Terminal", "tail -f conversation.log"
                });
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux command to open a new terminal and run `tail -f conversation.log`
                logViewerProcess = Runtime.getRuntime().exec(new String[]{
                    "xterm", "-e", "tail -f conversation.log"
                });
            } else {
                System.err.println("Unsupported operating system.");
            }

            logger.info("Conversation log viewer started in a new terminal.");
        } catch (IOException e) {
            logger.error("Failed to start the log viewer: ", e);
        }
    }

    private static void closeLogViewer() {
        if (logViewerProcess != null) {
            logViewerProcess.destroy(); // Terminate the log viewer process
            logger.info("Log viewer process terminated.");
        }
    }
}
