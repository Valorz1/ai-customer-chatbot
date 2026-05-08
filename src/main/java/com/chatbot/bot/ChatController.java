package com.chatbot.bot;

import com.chatbot.knowledge.KnowledgeBase;
import com.chatbot.history.ChatHistory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller that handles chat messages from the web interface.
 * Receives user messages via HTTP and returns bot responses.
 */
@RestController
public class ChatController {

    private KnowledgeBase knowledgeBase;
    private ChatHistory chatHistory;
    private AIService aiService;
    private Gson gson;

    // Constructor: initializes all components
    public ChatController() {
        this.gson = new Gson();

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
    @PostMapping(value = "/chat", consumes = "application/json", produces = "application/json")
    public String chat(@RequestBody String body) {
        try {
            // Parse the request manually with Gson
            JsonObject request = gson.fromJson(body, JsonObject.class);
            String userMessage = request.get("message").getAsString();

            // Validate input
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return buildResponse("Please type something so I can help you.");
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

            return buildResponse(response);

        } catch (Exception e) {
            System.out.println("Chat error: " + e.getMessage());
            return buildResponse("Something went wrong. Please try again.");
        }
    }

    // Builds a JSON response string
    private String buildResponse(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("response", message);
        return gson.toJson(response);
    }
}