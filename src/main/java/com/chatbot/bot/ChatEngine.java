package com.chatbot.bot;

import com.chatbot.knowledge.KnowledgeBase;

import java.security.PrivateKey;
import java.util.Scanner;


/*
 * Controls the conversation flow between the user and the chatbot.
 * Routes user input to the knowledge base and handles unmatched queries.
 */

public class ChatEngine {
	
	private KnowledgeBase knowledgeBase;
	private boolean isRunning;
	
	
    // Constructor: sets up the knowledge base and starts the engine
	public ChatEngine() {
		  this.knowledgeBase = new KnowledgeBase();
		  this.isRunning = true;
	}
	// Main loop: keep the converstion going until the user says goodbye .
	public void start() {
		Scanner scanner = new Scanner(System.in);
		
        System.out.println("========================================");
        System.out.println("   Welcome to AI Customer Support!");
        System.out.println("   Type your question or 'bye' to exit.");
        System.out.println("========================================");
        
        while (isRunning) {
			System.out.print("\nYou: ");
			String userInput = scanner.nextLine();
			
			// check for empty input.
			if (userInput.trim().isEmpty()) {
				System.out.println("Bot: Pleases type something so i can hekp you.");
				continue;
			}
			//check if the user want to exit.
			if (isExitCommand(userInput)) {
				isRunning = false;
				System.out.println("Bot: Goodbye! Thank you for chatting with us.");
				break;
			}
				//try to find a response from the knowledge  base
				String response = knowledgeBase.findResponse(userInput);
				
				if (response != null) {
	                System.out.println("Bot: " + response);
	            } else {
	                // No match found — fallback response for now
	                System.out.println("Bot: I'm not sure about that. Let me connect you with a human agent.");
	                System.out.println("Bot: Is there anything else I can help with?");
	            }
	        }
        
	        scanner.close();
	        
	    }
        
        // Checks if the user wants to end the conversation
        private boolean isExitCommand(String input) {
            String lower = input.toLowerCase().trim();
            return lower.equals("bye") || lower.equals("quit") 
                || lower.equals("exit") || lower.equals("goodbye");
    }
}
