import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Receiver implements Runnable {
    private int port;
    private MessageHistory history;
    private ServerSocket serverSocket;
    private boolean running;
    private ExecutorService threadPool;
    
    public Receiver(int port, MessageHistory history) {
        this.port = port;
        this.history = history;
        this.running = true;
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("✅ Listening on port " + port);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, history));
            }
            
        } catch (IOException e) {
            if (running) {
                System.err.println("Error in receiver: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }
    
    public void stop() {
        running = false;
        threadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private MessageHistory history;
        
        public ClientHandler(Socket socket, MessageHistory history) {
            this.socket = socket;
            this.history = history;
        }
        
        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
                );
                
                // Read ONE message
                String message = reader.readLine();
                if (message != null && !message.trim().isEmpty()) {
                    String senderInfo = socket.getInetAddress().getHostAddress();
                    int senderPort = socket.getPort();
                    
                    // DISPLAY MESSAGE ON SCREEN
                    System.out.println("\n" + "═".repeat(50));
                    System.out.println("📩 NEW MESSAGE");
                    System.out.println("From: " + senderInfo + ":" + senderPort);
                    System.out.println("Content: " + message);
                    System.out.println("═".repeat(50));
                    
                    // Save to history
                    history.add("From " + senderInfo + ":" + senderPort + ": " + message);
                }
                
                reader.close();
                socket.close();
                
            } catch (IOException e) {
                // Ignore connection errors
            }
        }
    }
}
