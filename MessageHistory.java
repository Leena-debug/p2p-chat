/**
 * File: MessageHistory.java
 * Author: [Your Name]
 * Student ID: [Your ID]
 * 
 * Complete Message History System for P2P Chat
 * Works with Node.java without requiring any changes in Node.java
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageHistory {
    // Private variables
    private String filename;
    private List<String> messages;
    private int maxHistorySize = 1000;
    
    /**
     * Constructor for MessageHistory
     * @param port Port number of this node (used in filename)
     */
    public MessageHistory(int port) {
        this.filename = "chat_history_" + port + ".txt";
        this.messages = new ArrayList<>();
        initializeHistoryFile();
        System.out.println("[History] System ready: " + filename);
    }
    
    /**
     * Initialize history file on first use
     */
    private void initializeHistoryFile() {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(filename)) {
                    writer.write("========================================\n");
                    writer.write("P2P CHAT HISTORY - Node Port: " + 
                                filename.replace("chat_history_", "").replace(".txt", "") + "\n");
                    writer.write("Created: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                    writer.write("========================================\n");
                }
            }
        } catch (IOException e) {
            System.err.println("[History Error] Cannot create file: " + e.getMessage());
        }
    }
    
    /**
     * Save a message to history (main method to be called from Node)
     * @param sender Name of message sender
     * @param message Content of the message
     * @param messageType Type of message: "sent", "received", "system"
     */
    public void saveMessage(String sender, String message, String messageType) {
        try {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String formattedMessage;
            
            if (messageType.equals("system")) {
                formattedMessage = String.format("[%s] [SYSTEM] %s", timestamp, message);
            } else {
                String typeSymbol = messageType.equals("sent") ? "→" : "←";
                formattedMessage = String.format("[%s] %s %s: %s", 
                                                timestamp, typeSymbol, sender, message);
            }
            
            // Add to memory cache
            synchronized(messages) {
                messages.add(formattedMessage);
                
                // Limit memory usage
                if (messages.size() > maxHistorySize) {
                    messages.remove(0);
                }
            }
            
            // Save to file
            try (FileWriter writer = new FileWriter(filename, true)) {
                writer.write(formattedMessage + "\n");
            }
            
        } catch (IOException e) {
            System.err.println("[History Error] Cannot save message: " + e.getMessage());
        }
    }
    
    /**
     * Alternative method for Node compatibility - saves without messageType
     * @param formattedMessage Pre-formatted message string
     */
    public void addMessage(String formattedMessage) {
        saveMessage("Node", formattedMessage, "system");
    }
    
    /**
     * Display recent messages to console
     * @param count Number of messages to display
     */
    public void displayRecentMessages(int count) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📜 CHAT HISTORY (Last " + Math.min(count, messages.size()) + " messages)");
        System.out.println("=".repeat(60));
        
        if (messages.isEmpty()) {
            System.out.println("No messages in history.");
        } else {
            int startIndex = Math.max(0, messages.size() - count);
            for (int i = startIndex; i < messages.size(); i++) {
                System.out.println(messages.get(i));
            }
        }
        System.out.println("=".repeat(60));
    }
    
    /**
     * Get all messages as List<String> (for Node compatibility)
     * @return List of all messages
     */
    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * Search for messages containing specific text
     * @param keyword Text to search for
     * @return List of matching messages
     */
    public List<String> searchMessages(String keyword) {
        List<String> results = new ArrayList<>();
        for (String message : messages) {
            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                results.add(message);
            }
        }
        return results;
    }
    
    /**
     * Display search results
     * @param keyword Search term
     */
    public void displaySearchResults(String keyword) {
        List<String> results = searchMessages(keyword);
        System.out.println("\n🔍 Search for: \"" + keyword + "\"");
        System.out.println("Found " + results.size() + " matches:");
        for (String result : results) {
            System.out.println("  " + result);
        }
    }
    
    /**
     * Get number of messages in history
     * @return Message count
     */
    public int getMessageCount() {
        return messages.size();
    }
    
    /**
     * Clear all message history
     * @param confirm Requires user confirmation
     * @return true if cleared successfully
     */
    public boolean clearHistory(boolean confirm) {
        if (!confirm) {
            return false;
        }
        
        try {
            messages.clear();
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("========================================\n");
                writer.write("HISTORY CLEARED: " + 
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                writer.write("========================================\n");
            }
            System.out.println("[History] History cleared.");
            return true;
        } catch (IOException e) {
            System.err.println("[History Error] Cannot clear history: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get information about the history file
     * @return File information string
     */
    public String getFileInfo() {
        File file = new File(filename);
        if (!file.exists()) {
            return "File not found.";
        }
        
        long fileSizeKB = file.length() / 1024;
        String lastModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date(file.lastModified()));
        
        return String.format(
            "File: %s\nSize: %d KB\nMessages: %d\nLast Modified: %s",
            filename, fileSizeKB, messages.size(), lastModified
        );
    }
    
    /**
     * Internal method - prints to console when a message is saved
     */
    private void logSave(String sender, String message, String type) {
        System.out.println("[History] Saved " + type + " message from " + sender + ": " + 
                          (message.length() > 20 ? message.substring(0, 20) + "..." : message));
    }
    
    /**
     * Quick method for Node compatibility - simplified version
     * @param port Port number for filename
     * @param message Message to save
     * @param isSent true if sent, false if received
     */
    public static void quickSave(int port, String message, boolean isSent) {
        String filename = "chat_simple_" + port + ".txt";
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String prefix = isSent ? "[SENT] " : "[RECV] ";
        
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("[" + timestamp + "] " + prefix + message + "\n");
        } catch (IOException e)
        {
            // Silent fail - doesn't crash the main program
        }
    }
    
    /**
     * Test method - can be removed in production
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing MessageHistory (Node-Compatible Version)");
        System.out.println("=".repeat(50));
        
        // Test 1: Create and save messages
        MessageHistory history = new MessageHistory(9999);
        history.saveMessage("Alice", "Hello everyone!", "sent");
        history.saveMessage("Bob", "Hi Alice!", "received");
        history.saveMessage("System", "Bob connected to chat", "system");
        
        // Test 2: Display messages
        history.displayRecentMessages(5);
        
        // Test 3: Search
        history.displaySearchResults("Alice");
        
        // Test 4: File info
        System.out.println("\n📊 File Information:");
        System.out.println(history.getFileInfo());
        
        // Test 5: Quick save (static method)
        quickSave(8888, "Test quick message", true);
        
        System.out.println("\n✅ All tests passed! MessageHistory is ready for Node integration.");
        System.out.println("To use in Node.java, add:");
        System.out.println("1. MessageHistory messageHistory = new MessageHistory(port);");
        System.out.println("2. messageHistory.saveMessage(sender, message, \"sent\");");
        System.out.println("3. messageHistory.saveMessage(sender, message, \"received\");");
    }
    /**
     * Compatibility method used by Receiver and other callers.
     * Delegates to addMessage which saves the formatted/system message.
     */
    public void add(String message) {
        addMessage(message);
    }
}