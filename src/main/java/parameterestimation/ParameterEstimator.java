package parameterestimation;

import utils.Utils;

import java.util.*;

import static utils.DistanceGraphUtils.getIntervalStart;


public class ParameterEstimator {

    private PassengerPickUpEstimator passengerPickUpEstimator;
    private PassengerDestinationEstimator passengerDestinationEstimator;

    private ArrayList<TaxiTrip> taxiTrips;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> taxiTripLengths;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripDistances;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripConsumptions;

    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> passengerDestinationProbability;
    private Set<Integer> timeIntervals;



    public ParameterEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
        this.passengerPickUpEstimator = new PassengerPickUpEstimator(taxiTrips);
        this.passengerDestinationEstimator = new PassengerDestinationEstimator(taxiTrips);
        this.taxiTripLengths = computeTaxiTripsLengths();
        this.taxiTripDistances = computeTaxiTripsDistances();
        this.taxiTripConsumptions = computeTaxiTripsConsumptions();
    }

    
    public void estimateParameters(){
        passengerPickUpProbability = passengerPickUpEstimator.estimatePickUpProbability();
        passengerDestinationProbability = passengerDestinationEstimator.estimatePickUpProbability();

        timeIntervals = passengerPickUpEstimator.getTimeIntervals();
    }


    public PassengerPickUpEstimator getPassengerPickUpEstimator() {
        return passengerPickUpEstimator;
    }


    public ArrayList<TaxiTrip> getTaxiTrips() {
        return taxiTrips;
    }


    public HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability() {
        return passengerPickUpProbability;
    }

    public double getPickUpProbabilityInNode(int nodeId, double timeStamp){
        return passengerPickUpProbability.get(getIntervalStart(timeStamp)).get(nodeId);
    }

    public HashMap<Integer, Double> getDestinationProbabilitiesInNode(int nodeId, double timeStamp){
        return passengerDestinationProbability.get(getIntervalStart(timeStamp)).get(nodeId);
    }


    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> getTaxiTripLengths() {
        return taxiTripLengths;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getTaxiTripConsumptions() {
        return taxiTripConsumptions;
    }

    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getTaxiTripDistances() {
        return taxiTripDistances;
    }

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> computeTaxiTripsLengths(){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();


        for (TaxiTrip taxiTrip : taxiTrips) {
            int intervalStart = getIntervalStart(taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes());

            if (result.containsKey(intervalStart)) {
                if (result.get(intervalStart).containsKey(taxiTrip.getPickUpNode().getId())){
                    if (result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).containsKey(taxiTrip.getDestinationNode().getId())){
                        long num = result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());
                        int size = nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());

                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), num + taxiTrip.getTripLength());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), size + 1);

                    } else {
                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripLength());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), 1);
                    }
                } else {
                    HashMap<Integer, Long> lengths = new HashMap<>();
                    HashMap<Integer, Integer> toDestNums = new HashMap<>();
                    lengths.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripLength());
                    toDestNums.put(taxiTrip.getDestinationNode().getId(), 1);

                    result.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), lengths);
                    nums.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), toDestNums);
                }
            } else {
                HashMap<Integer, Long> nodeTrips = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Long>> tripLengths = new HashMap<>();
                HashMap<Integer, Integer> nodeTripNums = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> tripLengthNums= new HashMap<>();

                nodeTrips.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripLength());
                tripLengths.put(taxiTrip.getPickUpNode().getId(), nodeTrips);

                nodeTripNums.put(taxiTrip.getDestinationNode().getId(), 1);
                tripLengthNums.put(taxiTrip.getPickUpNode().getId(), nodeTripNums);

                result.put(intervalStart, tripLengths);
                nums.put(intervalStart, tripLengthNums);
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Long>>> timeInterval : result.entrySet()){
            for (Map.Entry<Integer, HashMap<Integer, Long>> node : timeInterval.getValue().entrySet()){
                for (Map.Entry<Integer, Long> length : node.getValue().entrySet()){
                    length.setValue(length.getValue()/nums.get(timeInterval.getKey()).get(node.getKey()).get(length.getKey()));
                }
            }
        }

        return result;
    }


    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> computeTaxiTripsDistances(){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();


        for (TaxiTrip taxiTrip : taxiTrips){
            int intervalStart = getIntervalStart(taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes());

            if (result.containsKey(intervalStart)) {
                if (result.get(intervalStart).containsKey(taxiTrip.getPickUpNode().getId())){
                    if (result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).containsKey(taxiTrip.getDestinationNode().getId())){
                        double num = result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());
                        int size = nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());

                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), num + taxiTrip.getDistance());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), size + 1);
                    } else {
                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), taxiTrip.getDistance());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), 1);
                    }
                } else {
                    HashMap<Integer, Double> distances = new HashMap<>();
                    distances.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getDistance());
                    result.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), distances);

                    HashMap<Integer, Integer> toDestNums = new HashMap<>();
                    toDestNums.put(taxiTrip.getDestinationNode().getId(), 1);
                    nums.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), toDestNums);
                }
            } else {
                HashMap<Integer, Double> nodeTrips = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Double>> tripDistances = new HashMap<>();
                nodeTrips.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getDistance());
                tripDistances.put(taxiTrip.getPickUpNode().getId(), nodeTrips);
                result.put(intervalStart, tripDistances);

                HashMap<Integer, Integer> nodeTripNums = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> tripLengthNums= new HashMap<>();

                nodeTripNums.put(taxiTrip.getDestinationNode().getId(), 1);
                tripLengthNums.put(taxiTrip.getPickUpNode().getId(), nodeTripNums);
                nums.put(intervalStart, tripLengthNums);
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Double>>> timeInterval : result.entrySet()){
            for (Map.Entry<Integer, HashMap<Integer, Double>> node : timeInterval.getValue().entrySet()){
                for (Map.Entry<Integer, Double> distance : node.getValue().entrySet()){
                    distance.setValue(distance.getValue()/nums.get(timeInterval.getKey()).get(node.getKey()).get(distance.getKey()));
                }
            }
        }

        return result;

    }

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> computeTaxiTripsConsumptions(){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();


        for (TaxiTrip taxiTrip : taxiTrips){
            int intervalStart = getIntervalStart(taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes());

            if (result.containsKey(intervalStart)) {
                if (result.get(intervalStart).containsKey(taxiTrip.getPickUpNode().getId())){
                    if (result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).containsKey(taxiTrip.getDestinationNode().getId())){
                        double num = result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());
                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), num + taxiTrip.getTripEnergyConsumption());

                        int size = nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).get(taxiTrip.getDestinationNode().getId());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).replace(taxiTrip.getDestinationNode().getId(), size + 1);

                    } else {
                        result.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripEnergyConsumption());
                        nums.get(intervalStart).get(taxiTrip.getPickUpNode().getId()).put(taxiTrip.getDestinationNode().getId(), 1);
                    }
                } else {
                    HashMap<Integer, Double> consumptions = new HashMap<>();
                    consumptions.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripEnergyConsumption());
                    result.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), consumptions);

                    HashMap<Integer, Integer> toDestNums = new HashMap<>();
                    toDestNums.put(taxiTrip.getDestinationNode().getId(), 1);
                    nums.get(intervalStart).put(taxiTrip.getPickUpNode().getId(), toDestNums);
                }
            } else {
                HashMap<Integer, Double> nodeTrips = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Double>> tripConsumptions = new HashMap<>();
                nodeTrips.put(taxiTrip.getDestinationNode().getId(), taxiTrip.getTripEnergyConsumption());
                tripConsumptions.put(taxiTrip.getPickUpNode().getId(), nodeTrips);
                result.put(intervalStart, tripConsumptions);

                HashMap<Integer, Integer> nodeTripNums = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> tripLengthNums= new HashMap<>();

                nodeTripNums.put(taxiTrip.getDestinationNode().getId(), 1);
                tripLengthNums.put(taxiTrip.getPickUpNode().getId(), nodeTripNums);
                nums.put(intervalStart, tripLengthNums);
            }
        }


        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Double>>> timeInterval : result.entrySet()){
            for (Map.Entry<Integer, HashMap<Integer, Double>> node : timeInterval.getValue().entrySet()){
                for (Map.Entry<Integer, Double> consumption : node.getValue().entrySet()){
                    consumption.setValue(consumption.getValue()/nums.get(timeInterval.getKey()).get(node.getKey()).get(consumption.getKey()));
                }
            }
        }

        return result;
    }
}
