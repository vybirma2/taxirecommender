package utils;

import charging.ChargingStation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.AStarNode;
import domain.environmentrepresentation.EnvironmentEdge;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.environmentrepresentation.EnvironmentNode;


import java.util.*;

/**
 * Util functions computing time, distance...
 */
public class DistanceGraphUtils {

    private static Collection<? extends RoadNode> nodes;
    private static Collection<RoadNode> osmNodes;
    private static EnvironmentGraph<? extends EnvironmentNode, ? extends EnvironmentEdge> graph;
    private static Graph<RoadNode, RoadEdge> osmGraph;
    private static HashMap<Integer, HashMap<Integer, Double>> chargingStationDistances;
    private static HashMap<Integer, HashMap<Integer, Double>> chargingStationSpeeds;
    private static List<ChargingStation> chargingStations;


    public static void setOsmNodes(Collection<RoadNode> osmNodes) {
        DistanceGraphUtils.osmNodes = osmNodes;
    }


    public static void setOsmGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        DistanceGraphUtils.osmGraph = osmGraph;
    }


    public static void setNodes(Collection<? extends EnvironmentNode> nodes) {
        DistanceGraphUtils.nodes = nodes;
    }


    public static void setGraph(EnvironmentGraph<? extends EnvironmentNode, ? extends EnvironmentEdge> graph) {
        DistanceGraphUtils.graph = graph;
    }


    public static void setChargingStationDistances(HashMap<Integer, HashMap<Integer, Double>> chargingStationDistances) {
        DistanceGraphUtils.chargingStationDistances = chargingStationDistances;
    }


    public static void setChargingStationSpeeds(HashMap<Integer, HashMap<Integer, Double>> chargingStationSpeeds) {
        DistanceGraphUtils.chargingStationSpeeds = chargingStationSpeeds;
    }


    public static void setChargingStations(List<ChargingStation> chargingStations) {
        DistanceGraphUtils.chargingStations = chargingStations;
    }


    /**
     * @param nodes
     * @param longitude
     * @param latitude
     * @return the closest node to given longitude/latitude in set of given nodes
     */
    public static RoadNode chooseRoadNode(Collection<? extends  RoadNode> nodes, double longitude, double latitude){
        double min = Double.MAX_VALUE;
        RoadNode roadNode = null;

        for (RoadNode node : nodes){
            double distance = getDistance(longitude, latitude, node.getLongitude(), node.getLatitude());

            if (distance < min){
                min = distance;
                roadNode = node;
            }
        }

        return roadNode;
    }


    public static RoadNode chooseRoadNode(double longitude, double latitude){
        return chooseRoadNode(osmNodes, longitude, latitude);
    }


    public static EnvironmentNode chooseEnvironmentNode(double longitude, double latitude){
        return (EnvironmentNode) chooseRoadNode(nodes, longitude, latitude);
    }


    /**
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return euclidean distance of two longitude latitude defined points
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2){
        longitude1 = Math.toRadians(longitude1);
        latitude1 = Math.toRadians(latitude1);
        longitude2 = Math.toRadians(longitude2);
        latitude2 = Math.toRadians(latitude2);

        double dlon = longitude2 - longitude1;
        double dlat = latitude2 - latitude1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double radius = 6371;

        return (c * radius);
    }


    public static Set<Integer> getOsmNeighbours(int node){
        List<RoadEdge> edges = osmGraph.getOutEdges(node);
        Set<Integer> neighbours = new HashSet<>();
        for (RoadEdge edge : edges){
            neighbours.add(edge.getToId());
        }

        return neighbours;
    }


    public static List<ChargingStation> getChargingStations(){
        return chargingStations;
    }


    public static int getTripTime(int fromNodeId, int toNodeId){
        return (int)Math.ceil((getDistanceBetweenNodes(fromNodeId, toNodeId)/(getSpeedBetweenNodes(fromNodeId, toNodeId)*0.7))*60
                + getDelay(fromNodeId, toNodeId));
    }


    /**
     * @param fromNodeId
     * @param toNodeId
     * @return distance between two points in environment graph or environment node and charging station
     */
    public static double getDistanceBetweenNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }
        EnvironmentEdge edge = graph.getEdge(fromNodeId, toNodeId);

        if (edge != null) {
            return edge.getLength()/1000.;
        } else {
            return getChargingStationParameters(fromNodeId, toNodeId, chargingStationDistances);
        }
    }


    private static double getChargingStationParameters(int fromNodeId, int toNodeId, HashMap<Integer, HashMap<Integer, Double>> parameters){
        if (parameters.containsKey(toNodeId)){
            HashMap<Integer, Double> nodes = parameters.get(toNodeId);
            return nodes.get(fromNodeId);
        } else if (parameters.containsKey(fromNodeId)){
            HashMap<Integer, Double> nodes = parameters.get(fromNodeId);
            return nodes.get(toNodeId);
        } else {
            throw new IllegalArgumentException("No connection between node: " + fromNodeId + " and node: " + toNodeId);
        }
    }


    public static double getDistanceBetweenOsmNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }

        RoadEdge edge = osmGraph.getEdge(fromNodeId, toNodeId);

        return edge.getLength()/1000.;
    }


    public static double getEuclideanDistanceBetweenOsmNodes(int fromNodeId, int toNodeId){
        RoadNode fromNode = osmGraph.getNode(fromNodeId);
        RoadNode toNode = osmGraph.getNode(toNodeId);
        return DistanceGraphUtils.getDistance(fromNode.getLongitude(), fromNode.getLatitude(),
                toNode.getLongitude(), toNode.getLatitude());
    }


    // TODO - further development - delay function
    public static int getDelay(int fromNodeId, int toNodeId){
        return 0;
    }


    public static double getSpeedBetweenOsmNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }

        RoadEdge edge = osmGraph.getEdge(fromNodeId, toNodeId);
        return edge.getAllowedMaxSpeedInMpS() * 3.6;
    }


    /**
     * @param fromNodeId
     * @param toNodeId
     * @return speed between two points in environment graph or environment node and charging station
     */
    public static double getSpeedBetweenNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }

        EnvironmentEdge edge = graph.getEdge(fromNodeId, toNodeId);
        if (edge != null){
            return edge.getAllowedMaxSpeedInMpS() * 3.6;
        } else {
            return getChargingStationParameters(fromNodeId, toNodeId, chargingStationSpeeds);
        }
    }


    /**
     * @param start start node id
     * @param goal destination node id
     * @return linked list of the shortest path from start to destination node
     */
    public static LinkedList<Integer> aStar(Integer start, Integer goal){
        AStarNode fromNode = new AStarNode(start, 0., getEuclideanDistanceBetweenOsmNodes(start, goal));
        AStarNode toNode = new AStarNode(goal, Double.MAX_VALUE, 0.);

        HashMap<Integer,Integer> parentMap = new HashMap<>();
        HashSet<Integer> visited = new HashSet<>();
        Map<Integer, Double> distances = new HashMap<>();

        PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();


        distances.put(fromNode.getNodeId(), 0.);
        priorityQueue.add(fromNode);
        AStarNode current = null;

        while (!priorityQueue.isEmpty()) {
            current = priorityQueue.remove();

            if (!visited.contains(current.getNodeId()) ){
                visited.add(current.getNodeId());

                if (current.getNodeId() == toNode.getNodeId()){
                    return reconstructPath(parentMap, fromNode.getNodeId(), toNode.getNodeId());
                }

                Set<Integer> neighbors = getOsmNeighbours(current.getNodeId());
                for (Integer neighbor : neighbors) {
                    if (!visited.contains(neighbor) ){

                        double predictedDistance = getEuclideanDistanceBetweenOsmNodes(neighbor, toNode.getNodeId());

                        double neighborDistance = getDistanceBetweenOsmNodes(current.getNodeId(), neighbor);
                        double totalDistance = current.getDistanceToStart() + neighborDistance;


                        if(!distances.containsKey(neighbor) || totalDistance + predictedDistance < distances.get(neighbor) ){
                            distances.put(neighbor, totalDistance + predictedDistance);
                            AStarNode neighbourNode = new AStarNode(neighbor, totalDistance, predictedDistance);

                            parentMap.put(neighbourNode.getNodeId(), current.getNodeId());
                            priorityQueue.add(neighbourNode);
                        }
                    }
                }
            }
        }
        return null;
    }


    private static LinkedList<Integer> reconstructPath(HashMap<Integer, Integer> parentMap, Integer fromNodeId, Integer toNodeId){
        LinkedList<Integer> path = new LinkedList<>();
        Integer current = toNodeId;

        while (!current.equals(fromNodeId)){
            path.addFirst(current);
            current = parentMap.get(current);
        }

        path.addFirst(current);

        return path;
    }


    public static int getIntervalStart(double timeStamp){
        int intTime = (int)timeStamp;
        int rest = intTime % Utils.ESTIMATION_EPISODE_LENGTH;
        return intTime - rest;
    }


    public static DistanceSpeedPair getDistanceSpeedPairOfPath(LinkedList<Integer> nodePath){
        double distance = 0;
        double speed = 0;
        Integer current = nodePath.getFirst();

        for (Integer node : nodePath){
            distance += getDistanceBetweenOsmNodes(current, node);
            speed += getSpeedBetweenOsmNodes(current, node);
            current = node;
        }

        return new DistanceSpeedPair(distance, speed/(nodePath.size() - 1));
    }
}
