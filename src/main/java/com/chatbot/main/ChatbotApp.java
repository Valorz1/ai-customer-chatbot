package com.chatbot.main;

import com.chatbot.bot.ChatEngine;

/**
 * Entry point for the AI Customer Service Chatbot.
 * Initialises and starts the chat engine.
 */
public class ChatbotApp {

    public static void main(String[] args) {
        System.out.println("Starting AI Customer Service Chatbot...\n");

        // Create and start the chat engine
        ChatEngine engine = new ChatEngine();
        engine.start();

        System.out.println("\nChatbot shut down. Thank you!");
    }
}