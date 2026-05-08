package com.chatbot.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the AI Customer Service Chatbot.
 * Starts the Spring Boot web server.
 */
@SpringBootApplication(scanBasePackages = "com.chatbot")
public class ChatbotApp {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApp.class, args);
        System.out.println("Chatbot is running at http://localhost:8080");
    }
}