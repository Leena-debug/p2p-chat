import java.util.ArrayList;
import java.util.List;

public class Peers {
    private List<Peer> peerList;

    public Peers() {
        this.peerList = new ArrayList<>();
    }

    public void addPeer(Peer peer) {
        peerList.add(peer);
    }

    public Peer getPeer(String id) {
        for (Peer p : peerList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void removePeer(String id) {
        peerList.removeIf(p -> p.getId().equals(id));
    }

    public void printPeers() {
        if (peerList.isEmpty()) {
            System.out.println("No peers connected.");
            return;
        }
        System.out.println("Connected peers:");
        for (Peer p : peerList) {
            System.out.println(p.getId() + " - " + p.getIp() + ":" + p.getPort());
        }
    }

    public List<Peer> getAllPeers() {
        return new ArrayList<>(peerList);
    }

    public int size() {
        return peerList.size();
    }
}