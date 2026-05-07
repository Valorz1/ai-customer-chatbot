package com.chatbot.bot;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

/**
 * Handles communication with the local Ollama AI model.
 * Used as a fallback when the knowledge base cannot answer a question.
 */
public class AIService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama3.2";

    private OkHttpClient client;
    private Gson gson;

    // Constructor: sets up the HTTP client with timeout settings
    public AIService() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    // Sends the user's message to the local AI and returns the response
    public String getResponse(String userMessage) {
        try {
            // Build the request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", MODEL);
            requestBody.addProperty("prompt",
                "You are a helpful customer service assistant. " +
                "Keep responses short, friendly, and professional. " +
                "If you cannot help, suggest contacting support at support@company.com.\n\n" +
                "Customer: " + userMessage + "\n\nAssistant:");
            requestBody.addProperty("stream", false);

            // Make the API call to local Ollama
            RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(OLLAMA_URL)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parse the AI's response
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.get("response").getAsString().trim();

        } catch (Exception e) {
            System.out.println("AI Service error: " + e.getMessage());
            return "I'm having trouble connecting to the AI. Please email support@company.com for help.";
        }
    }
}