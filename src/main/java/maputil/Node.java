package maputil;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public long id;
    public double latitude, longitude;
    public List<Edge> neighbours;

    public Node(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(Edge e) {
        neighbours.add(e);
    }

    @Override
    public String toString() {
        return "LAT: " + latitude + ", LON: " + longitude;
    }
}