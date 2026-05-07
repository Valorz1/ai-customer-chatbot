package com.chatbot.bot;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Handles communication with the local Ollama AI model.
 * Used as a fallback when the knowledge base cannot answer a question.
 * Inculde error handling for conection failures, timeouts, and invalid input.
 */
public class AIService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama3.2";
    private static final int MAX_INPUT_LENGTH = 500;
    
    private OkHttpClient client;
    private Gson gson;
    private boolean isAvailable;

    // Constructor: sets up the HTTP client with timeout settings
    public AIService() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
        this.isAvailable = checkConnection();
    }

    // Checks if Ollama is running on startup
    private boolean checkConnection() {
        try {
            Request request = new Request.Builder()
                .url("http://localhost:11434")
                .get()
                .build();
            Response response = client.newCall(request).execute();
            response.close();
            System.out.println("AI Service connected to Ollama!");
            return true;
        } catch (Exception e) {
            System.out.println("WARNING: Ollama is not running. AI responses will be unavailable.");
            System.out.println("Start Ollama with: ollama run llama3.2");
            return false;
        }
    }
    
    private String validateInput(String userMessage) {
    	if (userMessage == null || userMessage.trim().isEmpty()) {
			return null;
		}
        // Trim long messages to prevent overloading the AI
    	if (userMessage.length() > MAX_INPUT_LENGTH) {
			userMessage = userMessage.substring(0, MAX_INPUT_LENGTH);
		}
    	return userMessage.trim();
    }
    
    // Sends the user's message to the local AI and returns the response
    public String getResponse(String userMessage) {
        
    	if (!isAvailable) {
			isAvailable = checkConnection();
			if (isAvailable) {
				return "Out AI assistant is currently offline. " + 
			"Pleases email support@Brunel.com for help.";
			}
		}
    	String validatedInput = validateInput(userMessage);
    	if (validatedInput == null) {
			return "I didn't catch that. Could you pleases rephrase your question?";
		}
    	
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

            // Check if the response was successful
            if (!response.isSuccessful()) {
                System.out.println("AI returned error code: " + response.code());
                return "I'm having trouble processing that. Can you try asking differently?";
            }

            String responseBody = response.body().string();

            // Parse the AI's response
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("response")) {
                String aiResponse = jsonResponse.get("response").getAsString().trim();
                // Guard against empty AI responses
                if (aiResponse.isEmpty()) {
                    return "I couldn't generate a response. Could you rephrase your question?";
                }
                return aiResponse;
            } else {
                return "I received an unexpected response. Please try again.";
            }

        } catch (SocketTimeoutException e) {
            System.out.println("AI Service timeout: " + e.getMessage());
            return "The response is taking too long. Please try a shorter question.";

        } catch (ConnectException e) {
            System.out.println("AI Service connection failed: " + e.getMessage());
            isAvailable = false;
            return "Our AI assistant is currently offline. "
                + "Please email support@company.com for help.";

        } catch (Exception e) {
            System.out.println("AI Service error: " + e.getMessage());
            return "Something went wrong. Please email support@company.com for help.";
        }
    }
}