package com.chatbot.knowledge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages the chatbot's knowledge base by loading Q&A data
 * from a JSON file and matching user input to responses.
 * Handles file loading errors and invalid JSON gracefully.
 */
public class KnowledgeBase {

    private JsonObject data;
    private Random random;
    private boolean isLoaded;

    // Constructor: initializes the knowledge base on creation
    public KnowledgeBase() {
        this.random = new Random();
        this.isLoaded = false;
        loadKnowledgeBase();
    }

    // Reads the JSON file from resources and loads it into memory
    private void loadKnowledgeBase() {
        try {
        	var resource = getClass().getClassLoader().getResourceAsStream("knowledge_base.json");

            if (resource == null) {
                System.out.println("WARNING: knowledge_base.json not found. Using AI only.");
                this.data = new JsonObject();
                return;
            }

            Reader reader = new InputStreamReader(resource);

            this.data = new Gson().fromJson(reader, JsonObject.class);
            reader.close();

            // Verify the data loaded correctly
            if (this.data == null || this.data.size() == 0) {
                System.out.println("WARNING: Knowledge base is empty. Using AI only.");
                this.data = new JsonObject();
                return;
            }

            isLoaded = true;
            System.out.println("Knowledge base loaded with "
                + this.data.size() + " categories!");

        } catch (JsonSyntaxException e) {
            System.out.println("WARNING: Knowledge base JSON is invalid: " + e.getMessage());
            System.out.println("Check your knowledge_base.json for syntax errors.");
            this.data = new JsonObject();

        } catch (Exception e) {
            System.out.println("WARNING: Could not load knowledge base: " + e.getMessage());
            this.data = new JsonObject();
        }
    }

    // Matches user input against patterns and returns the best response
    public String findResponse(String userInput) {
        // If knowledge base didn't load, skip straight to AI
        if (!isLoaded || data.size() == 0) {
            return null;
        }

        // Validate input
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }

        String input = userInput.toLowerCase().trim();

        String bestCategory = null;
        int bestMatchCount = 0;

        // Loop through each category and count pattern matches
        try {
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                JsonObject category = entry.getValue().getAsJsonObject();
                List<String> patterns = getPatterns(category);

                int matchCount = 0;
                for (String pattern : patterns) {
                    if (input.matches(".*\\b" + pattern + "\\b.*")) {
                        matchCount++;
                    }
                }

                if (matchCount > bestMatchCount) {
                    bestMatchCount = matchCount;
                    bestCategory = entry.getKey();
                }
            }
        } catch (Exception e) {
            System.out.println("Error matching patterns: " + e.getMessage());
            return null;
        }

        // Return a random response from the best matching category
        if (bestCategory != null) {
            try {
                JsonObject category = data.getAsJsonObject(bestCategory);
                List<String> responses = getResponses(category);
                if (!responses.isEmpty()) {
                    return responses.get(random.nextInt(responses.size()));
                }
            } catch (Exception e) {
                System.out.println("Error getting response: " + e.getMessage());
                return null;
            }
        }

        // No match found — returns null so the bot can fall back to AI
        return null;
    }

    // Extracts the patterns array from a category
    private List<String> getPatterns(JsonObject category) {
        List<String> patterns = new ArrayList<>();
        try {
            for (JsonElement element : category.getAsJsonArray("patterns")) {
                patterns.add(element.getAsString());
            }
        } catch (Exception e) {
            System.out.println("Error reading patterns: " + e.getMessage());
        }
        return patterns;
    }

    // Extracts the responses array from a category
    private List<String> getResponses(JsonObject category) {
        List<String> responses = new ArrayList<>();
        try {
            for (JsonElement element : category.getAsJsonArray("responses")) {
                responses.add(element.getAsString());
            }
        } catch (Exception e) {
            System.out.println("Error reading responses: " + e.getMessage());
        }
        return responses;
    }
}