import java.util.Scanner;

public class Node {
    private final Peers peers;
    private final MessageHistory history;
    private final Receiver receiver;
    private final String username;
    private final int listenPort;
    
    public Node(String username, int port) {
        this.username = username;
        this.listenPort = port;
        this.peers = new Peers();
        this.history = new MessageHistory(port);
        this.receiver = new Receiver(port, history);
    }
    
    public void start() {
        // Start receiver in background
        Thread receiverThread = new Thread(receiver);
        receiverThread.setDaemon(true);
        receiverThread.start();
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🎯 P2P CHAT NODE: " + username + " (Port: " + listenPort + ")");
        System.out.println("=".repeat(40));
        System.out.println("📋 Commands:");
        System.out.println("  /connect <ip> <port>  - Connect to a peer");
        System.out.println("  /msg <peerId> <text>  - Send message");
        System.out.println("  /peers                - List connected peers");
        System.out.println("  /exit                 - Exit program");
        System.out.println("=".repeat(40));
        
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            showPrompt();
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                handleCommand(line);
            }
        }
    }
    
    private void showPrompt() {
        System.out.print("\n[" + username + ":" + listenPort + "] > ");
    }
    
    private void handleCommand(String input) {
        String[] parts = input.split("\\s+", 3);
        
        if (parts.length == 0) return;
        
        String command = parts[0].toLowerCase();
        
        switch (command) {
            case "/connect":
                if (parts.length < 3) {
                    System.out.println("❌ Usage: /connect <ip> <port>");
                    return;
                }
                try {
                    String ip = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    connectToPeer(ip, port);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid port number");
                }
                break;
                
            case "/msg":
                if (parts.length < 3) {
                    System.out.println("❌ Usage: /msg <peerId> <message>");
                    return;
                }
                sendMessage(parts[1], parts[2]);
                break;
                
            case "/peers":
                peers.printPeers();
                break;
                
            case "/exit":
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
                
            case "/help":
                showHelp();
                break;
                
            default:
                System.out.println("❌ Unknown command. Type /help for commands.");
        }
    }
    
    private void showHelp() {
        System.out.println("\n💡 Available Commands:");
        System.out.println("  /connect <ip> <port>  - Connect to another peer");
        System.out.println("  /msg <peerId> <text>  - Send message to peer");
        System.out.println("  /peers                - Show connected peers");
        System.out.println("  /help                 - Show this help");
        System.out.println("  /exit                 - Exit the program");
        System.out.println("\n📝 Example:");
        System.out.println("  /connect 127.0.0.1 5001");
        System.out.println("  /msg 127.0.0.1:5001 Hello!");
    }
    
    private void connectToPeer(String ip, int port) {
        if (ip.equals("127.0.0.1") && port == listenPort) {
            System.out.println("❌ Cannot connect to yourself!");
            return;
        }
        
        Peer p = new Peer(ip, port);
        peers.addPeer(p);
        System.out.println("✅ Connected to " + ip + ":" + port);
        history.add("Connected to " + ip + ":" + port);
    }
    
    private void sendMessage(String peerId, String msg) {
        Peer p = peers.getPeer(peerId);
        if (p == null) {
            System.out.println("❌ Peer not found: " + peerId);
            System.out.println("   Use /peers to see connected peers");
            return;
        }
        
        // FIXED: Simple format - "Username: Message"
        String fullMessage = username + ": " + msg;
        
        try {
            Sender.sendMessage(p, fullMessage);
            history.add("Me → " + peerId + ": " + msg);
            System.out.println("📤 Sent to " + peerId + ": " + msg);
        } catch (Exception e) {
            System.out.println("❌ Failed to send: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("🚀 Starting P2P Chat Node...");
        System.out.print("Enter your username: ");
        String name = sc.nextLine().trim();
        
        System.out.print("Enter port to listen on (e.g., 5000): ");
        int port;
        try {
            port = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Invalid port. Using default 5000");
            port = 5000;
        }
        
        System.out.println("\n⏳ Starting node...");
        Node node = new Node(name, port);
        node.start();
    }
}








/*import java.util.Scanner;

public class Node {
    private final Peers peers;
    private final MessageHistory history;
    private final Receiver receiver;
    private final String username;
    private final int listenPort;
    
    public Node(String username, int port) {
        this.username = username;
        this.listenPort = port;
        this.peers = new Peers();
        this.history = new MessageHistory(port);
        this.receiver = new Receiver(port, history);
    }
    
    public void start() {
        // Start receiver in background
        Thread receiverThread = new Thread(receiver);
        receiverThread.setDaemon(true);
        receiverThread.start();
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🎯 P2P CHAT NODE: " + username + " (Port: " + listenPort + ")");
        System.out.println("=".repeat(40));
        System.out.println("📋 Commands:");
        System.out.println("  /connect <ip> <port>  - Connect to a peer");
        System.out.println("  /msg <peerId> <text>  - Send message");
        System.out.println("  /peers                - List connected peers");
        System.out.println("  /exit                 - Exit program");
        System.out.println("=".repeat(40));
        
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            showPrompt();
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                handleCommand(line);
            }
        }
    }
    
    private void showPrompt() {
        System.out.print("\n[" + username + ":" + listenPort + "] > ");
    }
    
    private void handleCommand(String input) {
        String[] parts = input.split("\\s+", 3); // Split by whitespace
        
        if (parts.length == 0) return;
        
        String command = parts[0].toLowerCase();
        
        switch (command) {
            case "/connect":
                if (parts.length < 3) {
                    System.out.println("❌ Usage: /connect <ip> <port>");
                    return;
                }
                try {
                    String ip = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    connectToPeer(ip, port);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid port number");
                }
                break;
                
            case "/msg":
                if (parts.length < 3) {
                    System.out.println("❌ Usage: /msg <peerId> <message>");
                    return;
                }
                sendMessage(parts[1], parts[2]);
                break;
                
            case "/peers":
                peers.printPeers();
                break;
                
            case "/exit":
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
                
            case "/help":
                showHelp();
                break;
                
            default:
                System.out.println("❌ Unknown command. Type /help for commands.");
        }
    }
    
    private void showHelp() {
        System.out.println("\n💡 Available Commands:");
        System.out.println("  /connect <ip> <port>  - Connect to another peer");
        System.out.println("  /msg <peerId> <text>  - Send message to peer");
        System.out.println("  /peers                - Show connected peers");
        System.out.println("  /help                 - Show this help");
        System.out.println("  /exit                 - Exit the program");
        System.out.println("\n📝 Example:");
        System.out.println("  /connect 127.0.0.1 5001");
        System.out.println("  /msg 127.0.0.1:5001 Hello!");
    }
    
    private void connectToPeer(String ip, int port) {
        if (ip.equals("127.0.0.1") && port == listenPort) {
            System.out.println("❌ Cannot connect to yourself!");
            return;
        }
        
        Peer p = new Peer(ip, port);
        peers.addPeer(p);
        System.out.println("✅ Connected to " + ip + ":" + port);
        history.add("Connected to " + ip + ":" + port);
    }
    
    private void sendMessage(String peerId, String msg) {
        Peer p = peers.getPeer(peerId);
        if (p == null) {
            System.out.println("❌ Peer not found: " + peerId);
            System.out.println("   Use /peers to see connected peers");
            return;
        }
        
        String fullMessage = username + ":" + listenPort + ": " + msg;
        
        try {
            Sender.sendMessage(p, fullMessage);
            history.add("Me → " + peerId + ": " + msg);
            System.out.println("📤 Sent to " + peerId + ": " + msg);
        } catch (Exception e) {
            System.out.println("❌ Failed to send: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("🚀 Starting P2P Chat Node...");
        System.out.print("Enter your username: ");
        String name = sc.nextLine().trim();
        
        System.out.print("Enter port to listen on (e.g., 5000): ");
        int port;
        try {
            port = sc.nextInt();
            sc.nextLine(); // Clear buffer
        } catch (Exception e) {
            System.out.println("❌ Invalid port. Using default 5000");
            port = 5000;
        }
        
        System.out.println("\n⏳ Starting node...");
        Node node = new Node(name, port);
        node.start();
    }
}*/











/*import java.util.Scanner;

public class Node {

    private static final Node Sender = null;
    private final Peers peers;
    private final MessageHistory history;
    private final Receiver receiver;
    private final String username;
    private final int listenPort;

    public Node(String username, int port) {
        this.username = username;
        this.listenPort = port;
        this.peers = new Peers();
        this.history = new MessageHistory(100); // Replace 100 with desired history size
        this.receiver = new Receiver(port, history);
    }

    public void start() {
        new Thread(receiver).start();

        System.out.println("\n=== P2P Chat Started ===");
        System.out.println("Commands:");
        System.out.println("/connect <ip> <port>");
        System.out.println("/msg <peerId> <message>");
        System.out.println("/peers");
        System.out.println("/exit");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            handleCommand(line);
        }
    }

    private void handleCommand(String input) {
        String[] parts = input.split(" ", 3);

        switch (parts[0]) {

            case "/connect":
                if (parts.length < 3) {
                    System.out.println("Usage: /connect <ip> <port>");
                    return;
                }
                connectToPeer(parts[1], Integer.parseInt(parts[2]));
                break;

            case "/msg":
                if (parts.length < 3) {
                    System.out.println("Usage: /msg <peerId> <message>");
                    return;
                }
                sendMessage(parts[1], parts[2]);
                break;

            case "/peers":
                peers.printPeers();
                break;

            case "/exit":
                System.out.println("Closing node...");
                System.exit(0);
                break;

            default:
                System.out.println("Unknown command.");
        }
    }

    private void connectToPeer(String ip, int port) {
        Peer p = new Peer(ip, port);
        peers.addPeer(p);
        System.out.println("Connected to peer " + ip + ":" + port);
    }

    private void sendMessage(String parts, String msg) {
        Peer p = peers.getPeer(parts);
        if (p == null) {
            System.out.println("Peer not found.");
            return;
        }

        Sender.sendMessage(p, username + ": " + msg);
        history.add("Me → " + p.getId() + ": " + msg);
    }

    private void sendMessage(Peer p, String msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        String name = sc.nextLine();

        System.out.print("Port to listen on: ");
        int port = sc.nextInt();

        Node node = new Node(name, port);
        node.start();
    }
}*/
