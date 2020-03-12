package utils;

import charging.ChargingStation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.AStarNode;
import domain.TaxiRecommenderDomainGenerator;
import parameterestimation.TaxiTrip;

import java.util.*;

public class DistanceGraphUtils {

    private static Collection<RoadNode> nodes;
    private static Graph<RoadNode, RoadEdge> graph;
    private static HashMap<Integer, HashMap<Integer, Double>> chargingStationDistances;
    private static HashMap<Integer, HashMap<Integer, Double>> chargingStationSpeeds;
    private static List<ChargingStation> chargingStations;



    public static void setNodes(Collection<RoadNode> nodes) {
        DistanceGraphUtils.nodes = nodes;
    }


    public static void setGraph(Graph<RoadNode, RoadEdge> graph) {
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

    public static RoadNode chooseRoadNode(double longitude, double latitude){
        double min = Double.MAX_VALUE;
        RoadNode roadNode = null;

        for (RoadNode node : nodes){
            double distance = getDistance(longitude, latitude, node.getLongitude(), node.getLatitude());

            if (distance < Utils.MAX_NODE_FITTING_DISTANCE && distance < min){
                min = distance;
                roadNode = node;
            }
        }

        return roadNode;
    }


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

    public static HashSet<Integer> getSurroundingNodesToLevel(int node, int numOfLevels) {

        HashMap<Integer, Integer> levels = new HashMap<>();
        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> que = new LinkedList<>();

        que.add(node);
        visited.add(node);
        levels.put(node, 0);

        int current;

        while (que.size() > 0){

            current = que.peek();
            que.remove();

            Set<Integer> neighbours = getNeighbours(current);

            for (Integer neighbour : neighbours) {
                if (!visited.contains(neighbour) && levels.get(current) + 1 <= numOfLevels) {
                    que.add(neighbour);
                    levels.put(neighbour, levels.get(current) + 1);
                    visited.add(neighbour);
                }
            }
        }

        visited.remove(node);

        return visited;
    }



    public static Set<Integer> getNeighbours(int node){
        List<RoadEdge> edges = graph.getOutEdges(node);
        Set<Integer> neighbours = new HashSet<>();
        for (RoadEdge edge : edges){
            neighbours.add(edge.getToId());
        }

        return neighbours;
    }


    public static List<ChargingStation> getChargingStations(){
        return chargingStations;
    }


    public static double getTripTime(int fromNodeId, int toNodeId){
        return (getDistanceBetweenNodes(fromNodeId, toNodeId)/getSpeedBetweenNodes(fromNodeId, toNodeId))*60
                + getDelay(fromNodeId, toNodeId);
    }


    public static double getDistanceBetweenNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }

        RoadEdge edge = graph.getEdge(fromNodeId, toNodeId);
        if (edge != null) {
            return edge.getLength()/1000.;
        } else {
            if (chargingStationDistances.containsKey(toNodeId)){
                HashMap<Integer, Double> nodes = chargingStationDistances.get(toNodeId);
                return nodes.get(fromNodeId);
            } else {
                throw new IllegalArgumentException("No connection between node: " + fromNodeId + " and node: " + toNodeId);
            }
        }
    }


    public static double getEuclideanDistanceBetweenNodes(int fromNodeId, int toNodeId){
        RoadNode fromNode = graph.getNode(fromNodeId);
        RoadNode toNode = graph.getNode(toNodeId);
        return DistanceGraphUtils.getDistance(fromNode.getLongitude(), fromNode.getLatitude(),
                toNode.getLongitude(), toNode.getLatitude());
    }


    // TODO - further development - delay function
    public static double getDelay(int fromNodeId, int toNodeId){
        return 0;
    }



    public static double getSpeedBetweenNodes(int fromNodeId, int toNodeId){
        if (fromNodeId == toNodeId){
            return 0;
        }

        RoadEdge edge = graph.getEdge(fromNodeId, toNodeId);
        if (edge != null){
            return edge.getAllowedMaxSpeedInMpS() * 3.6;
        } else {
            if (chargingStationSpeeds.containsKey(toNodeId)){
                HashMap<Integer, Double> nodes = chargingStationSpeeds.get(toNodeId);
                return nodes.get(fromNodeId);
            } else {
                throw new IllegalArgumentException("No connection between node: " + fromNodeId + " and node: " + toNodeId);
            }
        }
    }


    public static LinkedList<Integer> aStar(RoadNode start, RoadNode goal){
        AStarNode fromNode = new AStarNode(start.getId(), 0., getEuclideanDistanceBetweenNodes(start.getId(), goal.getId()));
        AStarNode toNode = new AStarNode(goal.getId(), Double.MAX_VALUE, 0.);

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

                Set<Integer> neighbors = getNeighbours(current.getNodeId());
                for (Integer neighbor : neighbors) {
                    if (!visited.contains(neighbor) ){

                        double predictedDistance = getEuclideanDistanceBetweenNodes(neighbor, toNode.getNodeId());

                        double neighborDistance = getDistanceBetweenNodes(current.getNodeId(), neighbor);
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
}
