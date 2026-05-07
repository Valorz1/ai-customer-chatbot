package com.chatbot.history;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Store and retrieves chat history using a local SQLite database.
 * Each message is saved with a timestamp and sender information.
 */
public class ChatHistory {

	private Connection connection;
	private DateTimeFormatter formatter;

	// Constructor: connects to the database and creates the table.
	public ChatHistory() {
		this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}
	

	// Creates the SQLite database and chat_history table.
	private void connectDatabase() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:chat_history.db");
			Statement statement = connection.createStatement();
			statement.execute(
					"CREATE TABLE IF NOT EXISTS chat_history (" +
							"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							"sender TEXT NOT NULL, " +
							"message TEXT NOT NULL, " +
							"timestamp TEXT NOT NULL)"
					);
			System.out.println("Chat history database connected!");
		} catch (Exception e) {
			System.out.println("Error connecting to database: " + e.getMessage());
		}
	}
	// Saving a message to the database.
	public void saveMessage(String sender, String message) {
		try {
		String sql = "INSERT INTO chat_history (sender, message, timestamp) VALUES (?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, sender);
		statement.setString(2, message);
		statement.setString(3, LocalDateTime.now().format(formatter));
		statement.executeUpdate();
		} catch (Exception e) {
			System.out.println("Error saving message: " + e.getMessage());
		}
	}
	// Display the last 10 messages from the chat history.
	public void showRecentHistory() {
		try {
			String sql = "SELECT sender, message, timestamp FROM chat_history ORDER BY id DESC LIMIT 10";
			Statement statement = connection.createStatement();
			ResultSet results = statement.executeQuery(sql);
			System.out.println("\n--- Recent Chat History ---");
			
			while (results.next()) {
				System.out.println("[" + results.getString("timestamp") + "] "
						+ results.getString("sender") + ": "
						+ results.getString("message"));
			}
			System.out.println("---------------------------\n");
		} catch (Exception e) {
			System.out.println("Error reading history: " + e.getMessage());
		}
	}
	//Close the database connection
public void close() {
	try {
		if (connection != null) {
			connection.close();
		}
	} catch (Exception e) {
		System.out.println("Error closing database: " + e.getMessage());
	}
}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
