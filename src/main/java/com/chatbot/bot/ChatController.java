package com.chatbot.bot;

import com.chatbot.knowledge.KnowledgeBase;
import com.chatbot.history.ChatHistory;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller that handles chat messages from the web interface.
 * Receives user messages via HTTP and returns bot responses.
 */
@RestController
public class ChatController {

    private KnowledgeBase knowledgeBase;
    private ChatHistory chatHistory;
    private AIService aiService;

    // Constructor: initializes all components
    public ChatController() {
        try {
            this.knowledgeBase = new KnowledgeBase();
        } catch (Exception e) {
            System.out.println("WARNING: Knowledge base failed: " + e.getMessage());
            this.knowledgeBase = null;
        }

        try {
            this.chatHistory = new ChatHistory();
        } catch (Exception e) {
            System.out.println("WARNING: Chat history failed: " + e.getMessage());
            this.chatHistory = null;
        }

        try {
            this.aiService = new AIService();
        } catch (Exception e) {
            System.out.println("WARNING: AI service failed: " + e.getMessage());
            this.aiService = null;
        }
    }

    // Receives a message from the browser and returns a response
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String userMessage = request.get("message");

        // Validate input
        if (userMessage == null || userMessage.trim().isEmpty()) {
            result.put("response", "Please type something so I can help you.");
            return result;
        }

        // Save user message to history
        if (chatHistory != null) {
            chatHistory.saveMessage("User", userMessage);
        }

        // Try knowledge base first
        String response = null;
        if (knowledgeBase != null) {
            response = knowledgeBase.findResponse(userMessage);
        }

        // Fall back to AI if no match
        if (response == null && aiService != null) {
            response = aiService.getResponse(userMessage);
        }

        // Final fallback
        if (response == null) {
            response = "I'm unable to help right now. Please email support@company.com.";
        }

        // Save bot response to history
        if (chatHistory != null) {
            chatHistory.saveMessage("Bot", response);
        }

        result.put("response", response);
        return result;
    }

    // Returns recent chat history
    @GetMapping("/history")
    public Map<String, String> getHistory() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Chat history available");
        return result;
    }
}