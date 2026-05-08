package com.chatbot.bot;

import com.chatbot.knowledge.KnowledgeBase;
import com.chatbot.history.ChatHistory;

import java.util.Scanner;

/**
 * Controls the conversation flow between the user and the chatbot.
 * Routes user input to the knowledge base, falls back to AI for unknown queries,
 * and saves all messages to history. Includes input validation and error handling.
 */
public class ChatEngine {

    private KnowledgeBase knowledgeBase;
    private ChatHistory chatHistory;
    private AIService aiService;
    private boolean isRunning;
    private static final int MAX_INPUT_LENGTH = 500;

    // Constructor: sets up all components with error handling
    public ChatEngine() {
        try {
            this.knowledgeBase = new KnowledgeBase();
        } catch (Exception e) {
            System.out.println("WARNING: Knowledge base failed to load: " + e.getMessage());
            this.knowledgeBase = null;
        }

        try {
            this.chatHistory = new ChatHistory();
        } catch (Exception e) {
            System.out.println("WARNING: Chat history failed to load: " + e.getMessage());
            this.chatHistory = null;
        }

        try {
            this.aiService = new AIService();
        } catch (Exception e) {
            System.out.println("WARNING: AI service failed to load: " + e.getMessage());
            this.aiService = null;
        }

        this.isRunning = true;
    }

    // Main loop: keeps the conversation going until the user says goodbye
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   Welcome to AI Customer Support!");
        System.out.println("   Type your question or 'bye' to exit.");
        System.out.println("   Type 'history' to see recent chats.");
        System.out.println("========================================");

        while (isRunning) {
            System.out.print("\nYou: ");

            String userInput;
            try {
                userInput = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Bot: Something went wrong reading your input. Please try again.");
                continue;
            }

            // Validate the input
            String validatedInput = validateInput(userInput);
            if (validatedInput == null) {
                System.out.println("Bot: Please type something so I can help you.");
                continue;
            }

            // Check if user wants to see chat history
            if (validatedInput.equalsIgnoreCase("history")) {
                if (chatHistory != null) {
                    chatHistory.showRecentHistory();
                } else {
                    System.out.println("Bot: Chat history is currently unavailable.");
                }
                continue;
            }

            // Save the user's message to history
            saveToHistory("User", validatedInput);

            // Check if the user wants to exit
            if (isExitCommand(validatedInput)) {
                String farewell = "Goodbye! Thanks for chatting with us.";
                System.out.println("Bot: " + farewell);
                saveToHistory("Bot", farewell);
                isRunning = false;
                break;
            }

            // Try the knowledge base first
            String response = null;
            if (knowledgeBase != null) {
                response = knowledgeBase.findResponse(validatedInput);
            }

            if (response != null) {
                // Knowledge base had an answer
                System.out.println("Bot: " + response);
                saveToHistory("Bot", response);
            } else if (aiService != null) {
                // No match — ask the AI for a response
                System.out.println("Bot: Let me think about that...");
                response = aiService.getResponse(validatedInput);
                System.out.println("Bot: " + response);
                saveToHistory("Bot", response);
            } else {
                // Both knowledge base and AI are unavailable
                String fallback = "I'm unable to help right now. "
                    + "Please email support@company.com for assistance.";
                System.out.println("Bot: " + fallback);
                saveToHistory("Bot", fallback);
            }
        }

        // Clean up resources
        scanner.close();
        if (chatHistory != null) {
            chatHistory.close();
        }
    }

    // Validates and cleans user input
    private String validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String cleaned = input.trim();

        // Trim long messages
        if (cleaned.length() > MAX_INPUT_LENGTH) {
            cleaned = cleaned.substring(0, MAX_INPUT_LENGTH);
            System.out.println("Bot: Your message was quite long, I'll focus on the first part.");
        }

        return cleaned;
    }

    // Safely saves a message to chat history
    private void saveToHistory(String sender, String message) {
        if (chatHistory != null) {
            chatHistory.saveMessage(sender, message);
        }
    }

    // Checks if the user wants to end the conversation
    private boolean isExitCommand(String input) {
        String lower = input.toLowerCase().trim();
        return lower.equals("bye") || lower.equals("quit")
            || lower.equals("exit") || lower.equals("goodbye");
    }
}