# Java AI Chatbot

An AI-powered chatbot built using Java, Maven, and OpenAI API.

## Features
- **Language Model Integration:** Seamless connection to a pre-trained language model (e.g., GPT-3 via API).

## Prerequisites
1. **Java Development Kit (JDK):** Version 11 or later.
2. **Apache Maven:** Dependency management.
3. **PostgreSQL:** Database for storing conversation logs.
4. **AWS CLI:** For Lambda integration.

## Setup
1. Clone the repository:
   git clone <repository-url>
   cd <project-directory>
2. Update main/resources/config.properties with your OpenAI API Key

## Running the Project
1. Build the Project:
   mvn clean install
2. Run the Application:
   mvn exec:java

## Testing - to be added
1. Unit Tests:
   mvn test

## Project Structure
- Main.java: Entry point for the application.
- AIService.java: Handles AI API interactions.
- Config.java: Centralized configuration management.
- pom.xml: Maven project configuration.
    