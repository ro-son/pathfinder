package maputil;

import java.util.*;

public class Graph {
    public List<Node> nodes;
    public Map<Long, Integer> indices;
    public int nodeCount;

    public Graph() {
        nodes = new ArrayList<>();
        indices = new HashMap<>();
        nodeCount = 0;
    }

    public void addNode(Node n) {
        nodes.add(n);
        indices.put(n.id, nodeCount);
        nodeCount++;
    }

    public void addTwoWayEdge(long firstId, long secondId, double length, double time) {
        Node n1 = this.getNodeById(firstId);
        Node n2 = this.getNodeById(secondId);

        if (n1 != null && n2 != null) {
            n1.addNeighbour(new Edge(n2, length, time));
            n2.addNeighbour(new Edge(n1, length, time));
        }
    }

    public Node getNodeById(long id) {
        if (indices.containsKey(id)) return nodes.get(indices.get(id));
        else return null;
    }

    public Node getNodeByCoordinates(double latitude, double longitude) {
        for (Node n : nodes) {
            if (n.latitude == latitude && n.longitude == longitude) return n;
        }
        return null;
    }

    public Node closestNode(double latitude, double longitude) {
        double dx, dy, distance = Double.MAX_VALUE;
        Node closest = null;

        for (Node n : nodes) {
            dx = n.latitude - latitude;
            dy = n.longitude - longitude;
            if (Math.sqrt(dx*dx + dy*dy) < distance) {
                distance = Math.sqrt(dx*dx + dy*dy);
                closest = n;
            }
        }

        return closest;
    }

    // Removes all nodes without neighbours
    public void clean() {
        nodeCount = 0;
        indices = new HashMap<>();
        nodes.removeIf((Node n) -> n.neighbours.isEmpty());

        for (Node n : nodes) {
            indices.put(n.id, nodeCount);
            nodeCount++;
        }
    }

    // Reduces graph to largest connected component
    public void reduce() {
        if (nodes.isEmpty()) return;

        List<Node> component, largestComponent, visited = new ArrayList<>();
        List<List<Node>> components = new ArrayList<>();
        Stack<Node> nodeStack;
        Node cur, dest;

        for (Node n : nodes) {
            if (!visited.contains(n)) {
                component = new ArrayList<>();
                nodeStack = new Stack<>();
                component.add(n);
                nodeStack.push(n);

                while (!nodeStack.isEmpty()) {
                    cur = nodeStack.pop();
                    for (Edge e : cur.neighbours) {
                        dest = e.destination;
                        if (!visited.contains(dest)) {
                            visited.add(dest);
                            component.add(dest);
                            nodeStack.push(dest);
                        }
                    }
                }

                components.add(component);
            }
        }

        largestComponent = components.get(0);
        for (int i = 1; i < components.size(); i++) {
            if (components.get(i).size() > largestComponent.size()) {
                largestComponent = components.get(i);
            }
        }

        final List<Node> largest = largestComponent;

        nodeCount = 0;
        indices = new HashMap<>();
        nodes.removeIf((Node n) -> !largest.contains(n));

        for(Node n : nodes) {
            indices.put(n.id, nodeCount);
            nodeCount++;
        }
    }

    public List<Node> shortestPath(double startLat, double startLon, double destLat, double destLon) {
        List<Node> path = new ArrayList<>();
        Node start = this.getNodeByCoordinates(startLat, startLon);
        Node destination = this.getNodeByCoordinates(destLat, destLon);

        if (start == null || destination == null) return path;

        // Dijkstra's algorithm
        Map<Node, Double> pathWeight = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        List<Node> remaining = new ArrayList<>();

        for (Node n : nodes) {
            if (n == start) pathWeight.put(n, 0.0);
            else pathWeight.put(n, Double.MAX_VALUE);

            remaining.add(n);
        }

        while (!remaining.isEmpty()) {
            Node cur = this.minNode(remaining, pathWeight);
            double curTime = pathWeight.get(cur);

            for (Edge neighbour : cur.neighbours) {
                if (curTime < pathWeight.get(neighbour.destination) - neighbour.time) {
                    pathWeight.put(neighbour.destination, curTime + neighbour.time);
                    previous.put(neighbour.destination, cur);
                }
            }

            remaining.remove(cur);
        }

        if (previous.containsKey(destination)) {
            Node retrace = destination;
            while (retrace != start) {
                path.add(retrace);
                retrace = previous.get(retrace);
            }
            path.add(start);
        }

        return path;
    }

    public Node minNode(List<Node> remaining, Map<Node, Double> pathWeight) {
        Node min = null;
        for (Node n : remaining) {
            if (min == null || pathWeight.get(n) < pathWeight.get(min)) {
                min = n;
            }
        }
        return min;
    }
}
