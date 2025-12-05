import java.io.*;
import java.net.*;

public class Sender {
    
    public static void sendMessage(Peer peer, String message) {
        try {
            Socket socket = new Socket(peer.getIp(), peer.getPort());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);
            writer.close();
            socket.close();
            // Success message is now shown in Node.java
        } catch (IOException e) {
            System.err.println("❌ Connection failed to " + peer.getIp() + ":" + peer.getPort());
        }
    }
}