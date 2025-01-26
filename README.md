# Java AI Chatbot

An AI-powered chatbot built using Java, Maven, and OpenAI API.

## Features
- **Language Model Integration:** Seamless connection to a pre-trained language model (e.g., GPT-3 via API).
- **Conversation Persistence:** Stores conversations in a PostgreSQL database.
- **Chat History Management:** Reset conversations or wipe all stored chat data with simple commands.
- **Interactive Logs:** Outputs the conversation to a separate terminal in real-time for clarity.
- **Scalable Architecture:** Easily extendable and configurable to support advanced features.
- **Database-Backed Commands:**
  - reset: Resets the current conversation and starts a new one.
  - wipe: Deletes all conversation history from the database.

## Prerequisites
1. **Java Development Kit (JDK):** Version 11 or later.
2. **Apache Maven:** Dependency management.
3. **PostgreSQL:** Database for storing conversations.
4. **Terminal Emulator:**
   - macOS: Terminal (default)
   - Linux: xterm, gnome-terminal, or similar.
   - Windows: Command Prompt or PowerShell.

## Configure the Database
1. Install PostgreSQL and start the server.
2. Create the database and user:
   ```
   CREATE DATABASE chatbot;
   CREATE USER chatbot_user WITH PASSWORD 'yourpassword';
   GRANT ALL PRIVILEGES ON DATABASE chatbot TO chatbot_user;
   ```
3. Create the required tables:
   ```
   CREATE TABLE conversations (
      id SERIAL PRIMARY KEY,
      conversation_id UUID NOT NULL,
      role TEXT NOT NULL, -- 'user' or 'assistant'
      message TEXT NOT NULL,
      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

   CREATE TABLE active_conversation (
      id SERIAL PRIMARY KEY,
      conversation_id UUID NOT NULL DEFAULT gen_random_uuid(),
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

   INSERT INTO active_conversation (conversation_id) VALUES (gen_random_uuid());
   ```

## Setup
1. Clone the repository:
   git clone <repository-url>
   cd <project-directory>

## Update Configuration
Add your OpenAI API key and database credentials to config.properties in src/main/resources:
   OPENAI_API_KEY=your_openai_api_key
   DB_URL=jdbc:postgresql://localhost:5432/chatbot
   DB_USER=chatbot_user
   DB_PASSWORD=yourpassword

## Running the Project
1. Build the Project:
   mvn clean install
2. Run the Application:
   mvn exec:java
3. View the Conversation Log:
   tail -f conversation.log

## Testing - to be added
1. Unit Tests:
   mvn test

## Project Structure
- Main.java: Entry point for the application.
- AIService.java: Handles AI API interactions.
- Config.java: Centralized configuration management.
- pom.xml: Maven project configuration.
    