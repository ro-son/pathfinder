package pathfinder;

import maputil.MapLoader;
import maputil.Graph;
import maputil.Node;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PathfinderController {
    Graph graph;

    @RequestMapping("/")
    public String mappage() {
        return "mappage.html";
    }

    @PostMapping("/loadmap")
    public ResponseEntity<?> load() {
        String osmFilepath = "map.osm";

        graph = new Graph();
        MapLoader loader = new MapLoader(graph);

        loader.load(osmFilepath);

        graph.clean();
        graph.reduce();

        double[][] coordinates = new double[graph.nodes.size()][2];
        for (int i = 0; i < graph.nodes.size(); i++) {
            coordinates[i][0] = graph.nodes.get(i).latitude;
            coordinates[i][1] = graph.nodes.get(i).longitude;
        }

        return ResponseEntity.ok(coordinates);
    }

    @GetMapping("/findpath")
    @ResponseBody
    public double[][] shortestPath(@RequestParam double firstlat,
                                   @RequestParam double firstlon,
                                   @RequestParam double secondlat,
                                   @RequestParam double secondlon) {
        Node start = graph.closestNode(firstlat, firstlon);
        Node destination = graph.closestNode(secondlat, secondlon);

        List<Node> path = graph.shortestPath(start.latitude, start.longitude, destination.latitude, destination.longitude);
        double[][] pathCoords = new double[path.size()][2];

        for (int i = 0; i < path.size(); i++) {
            pathCoords[i][0] = path.get(i).latitude;
            pathCoords[i][1] = path.get(i).longitude;
        }

        return pathCoords;
    }
}
