public class Peer {
    private final String id;
    private String ip;
    private int port;

    /**
     * Constructor that derives id from ip and port
     */
    public Peer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.id = ip + ":" + port;
    }

    /**
     * Constructor with explicit id
     */
    public Peer(String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return id.equals(peer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " (" + ip + ":" + port + ")";
    }
}

