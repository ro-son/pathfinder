package maputil;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLoader extends DefaultHandler {
    public Graph graph;
    public List<Long> wayNodes;
    public Map<String, Integer> speeds;
    public boolean inWay = false, isHighway = false;
    public String key, valHighway;

    public MapLoader(Graph g) {
        graph = g;
        wayNodes = new ArrayList<>();

        speeds = new HashMap<>();
        speeds.put("motorway", 110);
        speeds.put("trunk", 110);
        speeds.put("primary", 70);
        speeds.put("secondary", 60);
        speeds.put("tertiary", 50);
        speeds.put("motorway_link", 50);
        speeds.put("trunk_link", 50);
        speeds.put("primary_link", 50);
        speeds.put("secondary_link", 50);
        speeds.put("road", 40);
        speeds.put("unclassified", 40);
        speeds.put("residential", 30);
        speeds.put("unsurfaced", 30);
        speeds.put("living_street", 10);
        speeds.put("service", 5);
    }

    public void load(String filePath) {
        File file = new File(filePath);

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, this);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("node")) {
            long id = Long.parseLong(attributes.getValue("id"));
            double latitude = Double.parseDouble(attributes.getValue("lat"));
            double longitude = Double.parseDouble(attributes.getValue("lon"));
            graph.addNode(new Node(id, latitude, longitude));
        }
        else if (qName.equals("way")) {
            inWay = true;
        }
        else if (qName.equals("nd") && inWay) {
            wayNodes.add(Long.parseLong(attributes.getValue("ref")));
        }
        else if (qName.equals("tag") && inWay) {
            key = attributes.getValue("k");
            if (key.equals("highway")) {
                isHighway = true;
                valHighway = attributes.getValue("v");
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("way") && isHighway && speeds.containsKey(valHighway)) {
            int speed = speeds.get(valHighway);
            long startId, endId;
            double length, time;
            Node startNode, endNode;

            for (int i = 1; i < wayNodes.size(); i++) {
                startId = wayNodes.get(i-1);
                endId = wayNodes.get(i);

                startNode = graph.getNodeById(startId);
                endNode = graph.getNodeById(endId);
                length = Distance.calcDistance(startNode.latitude, startNode.longitude,
                        endNode.latitude, endNode.longitude);
                time = length / speed;

                graph.addTwoWayEdge(startId, endId, length, time);
            }
        }

        if (qName.equals("way")) {
            wayNodes.clear();
            inWay = false;
            isHighway = false;
        }
    }
}
