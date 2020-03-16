package parameterestimation;

import utils.Utils;

import java.util.*;

import static utils.DistanceGraphUtils.getSurroundingNodesToLevel;

public class ParameterEstimationUtils {


    public static final int NUM_OF_LEVELS_IN_DROP_OFF_SEARCH = 3;


    public static HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> getNumberOfTripsToDestinationNodes(HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> tripsToDestinationNode = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, HashMap<Integer, Integer>> intervalTripsToDestination = new HashMap<>();

            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalTripsToDestination.containsKey(taxiTrip.getPickUpNode().getId())){
                    HashMap<Integer, Integer> tripsFromNode = intervalTripsToDestination.get(taxiTrip.getPickUpNode().getId());
                    if (tripsFromNode.containsKey(taxiTrip.getDestinationNode().getId())){
                        tripsFromNode.replace(taxiTrip.getDestinationNode().getId(), tripsFromNode.get(taxiTrip.getDestinationNode().getId()) + 1);
                    } else {
                        tripsFromNode.put(taxiTrip.getDestinationNode().getId(), 1);
                    }
                } else {
                    HashMap<Integer, Integer> toNodeTrips = new HashMap<>();
                    toNodeTrips.put(taxiTrip.getDestinationNode().getId(), 1);
                    intervalTripsToDestination.put(taxiTrip.getPickUpNode().getId(), toNodeTrips);
                }
            }
            tripsToDestinationNode.put(entry.getKey(), intervalTripsToDestination);
        }

        return tripsToDestinationNode;
    }


    public static void addSurroundingsNodesTripsToDestination(HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> tripsToDestination){

        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Integer>>> timeInterval : tripsToDestination.entrySet()){

            for (Map.Entry<Integer, HashMap<Integer, Integer>> node : timeInterval.getValue().entrySet()){
                Set<Integer> surroundingNodes = getSurroundingNodesToLevel(node.getKey(), ParameterEstimationUtils.NUM_OF_LEVELS_IN_DROP_OFF_SEARCH);
                HashMap<Integer, Integer> summedTripsToDestination = new HashMap<>();

                for (Map.Entry<Integer, Integer> entry : node.getValue().entrySet()){
                    int sumOfDropOffs = entry.getValue();

                    for(Integer neighbour : surroundingNodes){
                        if (timeInterval.getValue().containsKey(neighbour) && timeInterval.getValue().get(neighbour).containsKey(entry.getKey())){
                            sumOfDropOffs += 1;
                        }
                    }
                    summedTripsToDestination.put(entry.getKey(), sumOfDropOffs);
                }
                tripsToDestination.get(timeInterval.getKey()).replace(node.getKey(), summedTripsToDestination);
            }
        }
    }


    public static void addSurroundingNodeActions(HashMap<Integer, HashMap<Integer, Integer>> actionsInOSMNodes){

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : actionsInOSMNodes.entrySet()){
            HashMap<Integer, Integer> summedPickups = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){
                Set<Integer> neighbours = getSurroundingNodesToLevel(node.getKey(), ParameterEstimationUtils.NUM_OF_LEVELS_IN_DROP_OFF_SEARCH);
                int sumOfPickUps = node.getValue();

                for(Integer neighbour : neighbours){
                    if (timeInterval.getValue().containsKey(neighbour)){
                        sumOfPickUps += timeInterval.getValue().get(neighbour);
                    }
                }
                summedPickups.put(node.getKey(), sumOfPickUps);
            }
            actionsInOSMNodes.replace(timeInterval.getKey(), summedPickups);
        }
    }


    public static HashMap<Integer, HashMap<Integer, Integer>> getPickUpsInNodes(HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, Integer> intervalPickUps = new HashMap<>();

            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalPickUps.containsKey(taxiTrip.getPickUpNode().getId())){
                    intervalPickUps.replace(taxiTrip.getPickUpNode().getId(), intervalPickUps.get(taxiTrip.getPickUpNode().getId()) + 1 );
                } else {
                    intervalPickUps.put(taxiTrip.getPickUpNode().getId(), 1);
                }
            }
            pickupsInOSMNodes.put(entry.getKey(), intervalPickUps);
        }

        return pickupsInOSMNodes;
    }


    public static HashMap<Integer, HashMap<Integer, Integer>> getDropOffsInNodes(HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, Integer> intervalDropOffs = new HashMap<>();

            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalDropOffs.containsKey(taxiTrip.getDestinationNode().getId())){
                    intervalDropOffs.replace(taxiTrip.getDestinationNode().getId(), intervalDropOffs.get(taxiTrip.getDestinationNode().getId()) + 1 );
                } else {
                    intervalDropOffs.put(taxiTrip.getDestinationNode().getId(), 1);
                }
            }
            dropOffsInOSMNodes.put(entry.getKey(), intervalDropOffs);
        }

        return dropOffsInOSMNodes;
    }


    public static HashMap<Integer, ArrayList<TaxiTrip>> getTimeSortedTrips(ArrayList<TaxiTrip> taxiTrips){
        int estimationTime = Utils.SHIFT_START_TIME;
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = new HashMap<>();
        ArrayList<TaxiTrip> trips = new ArrayList<>();

        Collections.sort(taxiTrips);

        for (TaxiTrip taxiTrip : taxiTrips){
            if (beforeTripTime(taxiTrip, estimationTime)){
                continue;
            } else if (afterTripTime(taxiTrip, estimationTime) && beforeTripTime(taxiTrip, estimationTime + Utils.ESTIMATION_EPISODE_LENGTH)){
                trips.add(taxiTrip);
            } else {
                if (!trips.isEmpty()){
                    timeSortedTaxiTrips.put(estimationTime, trips);
                }

                trips = new ArrayList<>();
                estimationTime += Utils.ESTIMATION_EPISODE_LENGTH;
                if (estimationTime >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME){
                    return timeSortedTaxiTrips;
                }

                trips.add(taxiTrip);

            }
            if (afterTripTime(taxiTrip, Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH + 1)) {
                break;
            }
        }

        return timeSortedTaxiTrips;
    }


    public static boolean beforeTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() <= time;
    }


    public static boolean afterTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() >= time;
    }
}
