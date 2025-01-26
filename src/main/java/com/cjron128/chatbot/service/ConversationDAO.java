package com.cjron128.chatbot.service;

import com.cjron128.chatbot.config.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversationDAO {
    public void saveMessage(UUID conversationId, String role, String message) {
        String query = "INSERT INTO conversations (conversation_id, role, message) VALUES (?, ?, ?)";
        try (Connection connection = Config.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, conversationId);
            statement.setString(2, role);
            statement.setString(3, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getConversation(UUID conversationId) {
        String query = "SELECT role, message FROM conversations WHERE conversation_id = ? ORDER BY timestamp ASC";
        List<String> conversation = new ArrayList<>();
        try (Connection connection = Config.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, conversationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String role = resultSet.getString("role");
                String message = resultSet.getString("message");
                conversation.add(role + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversation;
    }

    public UUID getActiveConversationId() {
        String query = "SELECT conversation_id FROM active_conversation LIMIT 1";
        try (Connection connection = Config.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return (UUID) resultSet.getObject("conversation_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if something goes wrong
    }

    public void resetConversationId() {
        String query = "UPDATE active_conversation SET conversation_id = gen_random_uuid(), updated_at = CURRENT_TIMESTAMP";
        try (Connection connection = Config.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void wipeDatabase() {
        try (Connection connection = Config.getConnection()) {
            // Disable foreign key checks if necessary
            connection.prepareStatement("TRUNCATE TABLE conversations RESTART IDENTITY CASCADE").executeUpdate();
            connection.prepareStatement("TRUNCATE TABLE active_conversation RESTART IDENTITY CASCADE").executeUpdate();

            // Optionally, reset the active conversation with a new conversation ID
            connection.prepareStatement(
                    "INSERT INTO active_conversation (conversation_id) VALUES (gen_random_uuid())").executeUpdate();

            // Clear the conversation log file
            clearConversationLog();

            System.out.println("Database wiped successfully. Active conversation reset.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to wipe the database.");
        }
    }

    private void clearConversationLog() {
        File logFile = new File("conversation.log");
        try (FileWriter writer = new FileWriter(logFile, false)) {
            // Overwrite the file with an empty content
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to clear the conversation log.");
        }
    }
}
