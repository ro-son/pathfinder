package maputil;

public class Edge {
    public Node destination;
    public double length;
    public double time;

    public Edge(Node destination, double length, double time) {
        this.destination = destination;
        this.length = length;
        this.time = time;
    }

    @Override
    public String toString() {
        return "DEST: " + destination.id;
    }
}
