package com.chatbot.knowledge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manage the chatbot's knowledge base by loading Q&A data 
 * from a Json file and matching user input to responses.
 */



public class KnowledgeBase {

    private JsonObject data;
    private Random random;

    
    // Constructor: initializse the knowledge base on creation.
    public KnowledgeBase() {
        this.random = new Random();
        loadKnowledgeBase();
    }
// Read the Json file from resource and loads it into memory.
    private void loadKnowledgeBase() {
        try {
            Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("data/knowledge_base.json")
            );
            this.data = new Gson().fromJson(reader, JsonObject.class);
            reader.close();
            System.out.println("Knowledge base loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error loading knowledge base: " + e.getMessage());
            this.data = new JsonObject();
        }
    }
// mathcing user input each category and count pattern matches
    public String findResponse(String userInput) {
        String input = userInput.toLowerCase().trim();

        String bestCategory = null;
        int bestMatchCount = 0;

        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            JsonObject category = entry.getValue().getAsJsonObject();
            List<String> patterns = getPatterns(category);

            int matchCount = 0;
            for (String pattern : patterns) {
                if (input.contains(pattern)) {
                    matchCount++;
                }
            }

            if (matchCount > bestMatchCount) {
                bestMatchCount = matchCount;
                bestCategory = entry.getKey();
            }
        }

        //return a random response from the best matching category
        if (bestCategory != null) {
            JsonObject category = data.getAsJsonObject(bestCategory);
            List<String> responses = getResponses(category);
            return responses.get(random.nextInt(responses.size()));
        }
        // No match found it return null so that the bot can fall back to AI
        return null;
    }
    //extracts the patterns array from a category 
    private List<String> getPatterns(JsonObject category) {
        List<String> patterns = new ArrayList<>();
        for (JsonElement element : category.getAsJsonArray("patterns")) {
            patterns.add(element.getAsString());
        }
        return patterns;
    }
    // Extracts the responses array from a category
    private List<String> getResponses(JsonObject category) {
        List<String> responses = new ArrayList<>();
        for (JsonElement element : category.getAsJsonArray("responses")) {
            responses.add(element.getAsString());
        }
        return responses;
    }
}