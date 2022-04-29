# pathfinder

Pathfinder is a Spring Boot Java application that finds the shortest travel route between selected points on a map.

## Installation

The project only requires that all provided files be in one directory to run. One can start using the program by running the file **PathfinderApplication.java**. The path to this file is `src/main/java/pathfinder/PathfinderApplication`.

Once the project is running, users will find a page with a map accompanied by a brief description on how to use it.

## How to Use

Upon selecting two points on the map, a red path will be drawn representing the shortest route between them. The provided map file contains data on a portion of downtown Toronto. Users can provide new map data by deleting the given **map.osm** and replacing it with a new OSM file. The new file's name must be **map.osm**.

## Screenshot

Below is an example of what the map should look like during use. Selecting two new points will draw a new path.

<p align="center">
  <img width="473" alt="map-screenshot" src="https://user-images.githubusercontent.com/92644044/165891805-086c41aa-f739-41c7-ac3d-d2d27b497046.png">
</p>
