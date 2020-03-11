package utils;

import cz.agents.multimodalstructures.nodes.RoadNode;

import java.util.Collection;

public class DistanceGraphUtils {

    private static Collection<RoadNode> nodes;


    public static void setNodes(Collection<RoadNode> nodes) {
        DistanceGraphUtils.nodes = nodes;
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
}
