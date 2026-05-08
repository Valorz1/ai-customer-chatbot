# AI Customer Service Chatbot

An AI-powered customer service chatbot built with Java, Spring Boot, and local AI integration. The chatbot handles common customer queries using a knowledge base and falls back to an AI model for complex questions.

## Features

- **Smart Knowledge Base** — Pattern matching system that recognises common customer queries (orders, returns, payments, hours, contact info)
- **AI-Powered Responses** — Integrates with Ollama (LLaMA 3.2) running locally to handle any question the knowledge base can't answer
- **Chat History** — All conversations stored in a SQLite database with timestamps
- **Modern Web Interface** — Dark mode dashboard UI with animated typing indicators, quick reply buttons, notification sounds, and session stats
- **Error Handling** — Graceful fallbacks when AI is offline, database fails, or input is invalid
- **Unit Tested** — 10 JUnit 5 tests covering pattern matching, edge cases, and input validation

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.3
- **AI Model:** Ollama with LLaMA 3.2 (runs locally, no API key needed)
- **Database:** SQLite via JDBC
- **HTTP Client:** OkHttp 4.12
- **JSON Parsing:** Gson 2.11
- **Testing:** JUnit 5
- **Frontend:** HTML, CSS, JavaScript
- **Build Tool:** Maven

## Project Structure
src/main/java/
├── com.chatbot.main          → Application entry point (Spring Boot)
├── com.chatbot.bot           → Chat engine, AI service, REST controller
├── com.chatbot.knowledge     → Knowledge base with JSON pattern matching
└── com.chatbot.history       → SQLite chat history storage
src/main/resources/
├── static/index.html         → Web-based chat interface
└── knowledge_base.json       → Q&A data for pattern matching
src/test/java/
└── com.chatbot.knowledge     → JUnit tests for knowledge base

## Prerequisites

- Java 17 or higher
- Maven
- Ollama (https://ollama.com)

## Setup & Run

1. **Clone the repository**
git clone https://github.com/Valorz1/ai-customer-chatbot.git
cd ai-customer-chatbot

2. **Install and start Ollama**
ollama pull llama3.2
ollama run llama3.2

3. **Build and run the application**
mvn clean compile
mvn spring-boot:run

4. **Open in browser**
http://localhost:8080


## How It Works

1. User sends a message through the web interface
2. The message is sent to the Spring Boot REST controller via HTTP POST
3. The controller checks the knowledge base for a pattern match
4. If no match is found, the message is forwarded to the local Ollama AI model
5. The response is saved to the SQLite database and sent back to the browser
6. If both the knowledge base and AI are unavailable, a static fallback response is returned

## Running Tests
mvn test


All 10 tests verify knowledge base functionality including pattern matching, case insensitivity, null/empty input handling, and match prioritisation.

## Screenshots

The web interface features a dark mode dashboard with a sidebar, live chat area, and session info panel.

## Author

**Valorz1** — Built as a portfolio project exploring Java, AI integration, backend development, and real-world problem solving.