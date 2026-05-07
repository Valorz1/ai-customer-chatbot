package com.chatbot.history;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores and retrieves chat history using a local SQLite database.
 * Includes error handling for database failures so the chatbot
 * keeps working even if history can't be saved.
 */
public class ChatHistory {

    private Connection connection;
    private DateTimeFormatter formatter;
    private boolean isConnected;

    // Constructor: connects to the database and creates the table
    public ChatHistory() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.isConnected = false;
        connectDatabase();
    }

    // Creates the SQLite database and chat_history table
    private void connectDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");

            String dbPath = System.getProperty("user.dir") + "/chat_history.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            Statement statement = connection.createStatement();
            statement.execute(
                "CREATE TABLE IF NOT EXISTS chat_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "timestamp TEXT NOT NULL)"
            );
            isConnected = true;
            System.out.println("Chat history database connected!");

        } catch (ClassNotFoundException e) {
            System.out.println("WARNING: SQLite driver not found. Chat history will not be saved.");
            isConnected = false;

        } catch (Exception e) {
            System.out.println("WARNING: Could not connect to database. Chat history will not be saved.");
            System.out.println("Reason: " + e.getMessage());
            isConnected = false;
        }
    }

    // Attempts to reconnect if the database connection was lost
    private void tryReconnect() {
        System.out.println("Attempting to reconnect to database...");
        connectDatabase();
    }

    // Saves a message to the database
    public void saveMessage(String sender, String message) {
        // Skip if database is not connected
        if (!isConnected) {
            return;
        }

        // Validate input
        if (sender == null || sender.trim().isEmpty()) {
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Trim very long messages before saving
        if (message.length() > 2000) {
            message = message.substring(0, 2000) + "... [trimmed]";
        }

        try {
            String sql = "INSERT INTO chat_history (sender, message, timestamp) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, sender);
            statement.setString(2, message);
            statement.setString(3, LocalDateTime.now().format(formatter));
            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Could not save message: " + e.getMessage());
            // Try reconnecting for next time
            isConnected = false;
            tryReconnect();
        }
    }

    // Displays the last 10 messages from the chat history
    public void showRecentHistory() {
        if (!isConnected) {
            System.out.println("\nChat history is currently unavailable.");
            return;
        }

        try {
            String sql = "SELECT sender, message, timestamp FROM chat_history ORDER BY id DESC LIMIT 10";
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);

            System.out.println("\n--- Recent Chat History ---");

            boolean hasMessages = false;
            while (results.next()) {
                hasMessages = true;
                System.out.println("[" + results.getString("timestamp") + "] "
                    + results.getString("sender") + ": "
                    + results.getString("message"));
            }

            if (!hasMessages) {
                System.out.println("No chat history found.");
            }

            System.out.println("---------------------------\n");

        } catch (Exception e) {
            System.out.println("Could not read chat history: " + e.getMessage());
            isConnected = false;
            tryReconnect();
        }
    }

    // Closes the database connection safely
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (Exception e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }
}