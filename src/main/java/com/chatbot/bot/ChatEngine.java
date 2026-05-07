package com.chatbot.bot;

import com.chatbot.knowledge.KnowledgeBase;
import com.chatbot.history.ChatHistory;

import java.util.Scanner;

/**
 * Controls the conversation flow between the user and the chatbot.
 * Routes user input to the knowledge base, falls back to AI for unknown queries,
 * and saves all messages to history.
 */
public class ChatEngine {

    private KnowledgeBase knowledgeBase;
    private ChatHistory chatHistory;
    private AIService aiService;
    private boolean isRunning;

    // Constructor: sets up the knowledge base, chat history, starts the engine, AI Server.
    public ChatEngine() {
        this.knowledgeBase = new KnowledgeBase();
        this.chatHistory = new ChatHistory();
        this.aiService = new AIService();
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
            String userInput = scanner.nextLine();

            // Check for empty input
            if (userInput.trim().isEmpty()) {
                System.out.println("Bot: Please type something so I can help you.");
                continue;
            }

            // Check if user wants to see chat history
            if (userInput.trim().equalsIgnoreCase("history")) {
                chatHistory.showRecentHistory();
                continue;
            }

            // Save the user's message to history
            chatHistory.saveMessage("User", userInput);

            // Check if the user wants to exit
            if (isExitCommand(userInput)) {
                String farewell = "Goodbye! Thanks for chatting with us.";
                System.out.println("Bot: " + farewell);
                chatHistory.saveMessage("Bot", farewell);
                isRunning = false;
                break;
            }

            // Try the knowledge base first
            String response = knowledgeBase.findResponse(userInput);

            if (response != null) {
                // Knowledge base had an answer
                System.out.println("Bot: " + response);
                chatHistory.saveMessage("Bot", response);
            } else {
                // No match — ask the AI for a response
                System.out.println("Bot: Let me think about that...");
                response = aiService.getResponse(userInput);
                System.out.println("Bot: " + response);
                chatHistory.saveMessage("Bot", response);
            }
        }

        // Clean up resources
        scanner.close();
        chatHistory.close();
    }

    // Checks if the user wants to end the conversation
    private boolean isExitCommand(String input) {
        String lower = input.toLowerCase().trim();
        return lower.equals("bye") || lower.equals("quit")
            || lower.equals("exit") || lower.equals("goodbye");
    }
}