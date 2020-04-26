package parameterestimation;

import java.io.Serializable;
import java.util.*;

import static utils.DistanceGraphUtils.getIntervalStart;


/**
 * Class responsible for whole parameter estimation, containing process of parameter estimation, and also all estimated
 * parameters
 */
public class ParameterEstimator implements Serializable {

    private final PassengerPickUpEstimator passengerPickUpEstimator;
    private final PassengerDestinationEstimator passengerDestinationEstimator;

    private final HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripLengths;
    private final HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripDistances;
    private final HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripConsumptions;


    private final HashMap<Integer, HashMap<Integer, Double>> taxiTripLengthsComplete;
    private final HashMap<Integer, HashMap<Integer, Double>> taxiTripDistancesComplete;
    private final HashMap<Integer, HashMap<Integer, Double>> taxiTripConsumptionsComplete;


    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private HashMap<Integer, Double> passengerPickUpProbabilityComplete;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> passengerDestinationProbability;
    private HashMap<Integer, HashMap<Integer, Double>> passengerDestinationProbabilityComplete;

    private Set<Integer> timeIntervals;


    public ParameterEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.passengerPickUpEstimator = new PassengerPickUpEstimator(taxiTrips);
        this.passengerDestinationEstimator = new PassengerDestinationEstimator(taxiTrips);
        this.taxiTripLengths = computeTaxiTripsLengths(taxiTrips);
        this.taxiTripDistances = computeTaxiTripsDistances(taxiTrips);
        this.taxiTripConsumptions = computeTaxiTripsConsumptions(taxiTrips);
        this.taxiTripLengthsComplete = computeTaxiTripsLengthsComplete(taxiTrips);
        this.taxiTripDistancesComplete = computeTaxiTripsDistancesComplete(taxiTrips);
        this.taxiTripConsumptionsComplete = computeTaxiTripsConsumptionsComplete(taxiTrips);
    }

    
    public void estimateParameters(){
        passengerPickUpProbability = passengerPickUpEstimator.estimatePickUpProbability();
        passengerPickUpProbabilityComplete = passengerPickUpEstimator.estimatePickUpProbabilityComplete();
        passengerDestinationProbability = passengerDestinationEstimator.estimateDestinationProbability();
        passengerDestinationProbabilityComplete = passengerDestinationEstimator.estimateDestinationProbabilityComplete();
        timeIntervals = passengerPickUpEstimator.getTimeIntervals();
    }


    public PassengerPickUpEstimator getPassengerPickUpEstimator() {
        return passengerPickUpEstimator;
    }


    /*public ArrayList<TaxiTrip> getTaxiTrips() {
        return taxiTrips;
    }*/


    public HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability() {
        return passengerPickUpProbability;
    }



    public double getPickUpProbabilityInNode(int nodeId, double timeStamp){
        Double result = passengerPickUpProbability.get(getIntervalStart(timeStamp)).get(nodeId);
        if (result != null){
            return result;
        }
        return 0;
    }


    public double getPickUpProbabilityInNode(int nodeId){
        Double result = passengerPickUpProbabilityComplete.get(nodeId);
        if (result != null){
            return result;
        }
        return 0;
    }


    public HashMap<Integer, Double> getDestinationProbabilitiesInNode(int nodeId, double timeStamp){
        return passengerDestinationProbability.get(getIntervalStart(timeStamp)).get(nodeId);
    }


    public HashMap<Integer, Double> getDestinationProbabilitiesInNode(int nodeId){
        return passengerDestinationProbabilityComplete.get(nodeId);
    }


    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getTaxiTripLengths() {
        return taxiTripLengths;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getTaxiTripConsumptions() {
        return taxiTripConsumptions;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getTaxiTripDistances() {
        return taxiTripDistances;
    }


    public HashMap<Integer, HashMap<Integer, Double>> getTaxiTripLengthsComplete() {
        return taxiTripLengthsComplete;
    }


    public HashMap<Integer, HashMap<Integer, Double>> getTaxiTripDistancesComplete() {
        return taxiTripDistancesComplete;
    }


    public HashMap<Integer, HashMap<Integer, Double>> getTaxiTripConsumptionsComplete() {
        return taxiTripConsumptionsComplete;
    }


    private HashMap<Integer, HashMap<Integer, Double>> computeTaxiTripsLengthsComplete(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips) {
            if (taxiTrip.getTripLength() != 0){
                addTripParameterComplete(taxiTrip, result, nums, (double)taxiTrip.getTripLength());
            }
        }

        computeMeanComplete(result, nums);

        return result;
    }


    private HashMap<Integer, HashMap<Integer, Double>> computeTaxiTripsDistancesComplete(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips) {
            if (taxiTrip.getTripLength() != 0){
                addTripParameterComplete(taxiTrip, result, nums, (double)taxiTrip.getDistance());
            }
        }

        computeMeanComplete(result, nums);

        return result;
    }


    private HashMap<Integer, HashMap<Integer, Double>> computeTaxiTripsConsumptionsComplete(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips) {
            if (taxiTrip.getTripLength() != 0){
                addTripParameterComplete(taxiTrip, result, nums, (double)taxiTrip.getTripEnergyConsumption());
            }
        }

        computeMeanComplete(result, nums);

        return result;
    }


    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> computeTaxiTripsLengths(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips) {
            if (taxiTrip.getTripLength() != 0){
                addTripParameter(taxiTrip, result, nums, (double)taxiTrip.getTripLength());
            }
        }

        computeMean(result, nums);

        return result;
    }


    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> computeTaxiTripsDistances(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (taxiTrip.getTripLength() != 0){
                addTripParameter(taxiTrip, result, nums, taxiTrip.getDistance());
            }
        }

        computeMean(result, nums);

        return result;

    }


    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> computeTaxiTripsConsumptions(List<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (taxiTrip.getTripLength() != 0){
                addTripParameter(taxiTrip, result, nums, (double)taxiTrip.getTripEnergyConsumption());
            }
        }

        computeMean(result, nums);

        return result;
    }


    private void addTripParameter(TaxiTrip taxiTrip, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                                  HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums, Double parameter){

        int intervalStart = getIntervalStart(taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes());

        if (result.containsKey(intervalStart)) {
            addExistingIntervalTripParameter(intervalStart, taxiTrip, result, nums, parameter);
        } else {
            addNewIntervalTripParameter(intervalStart, taxiTrip, result, nums, parameter);
        }
    }


    private void addTripParameterComplete(TaxiTrip taxiTrip, HashMap<Integer, HashMap<Integer, Double>> result,
                                  HashMap<Integer, HashMap<Integer, Integer>> nums, Double parameter){
        int fromNode = taxiTrip.getFromEnvironmentNode();
        int toNode = taxiTrip.getToEnvironmentNode();
        if (result.containsKey(fromNode)) {
            if (result.get(fromNode).containsKey(toNode)){
                result.get(fromNode).replace(toNode, result.get(fromNode).get(toNode) + parameter);
                nums.get(fromNode).replace(toNode, nums.get(fromNode).get(toNode) + 1);
            } else {
                result.get(fromNode).put(toNode, parameter);
                nums.get(fromNode).put(toNode, 1);
            }
        } else {
            HashMap<Integer, Double> res = new HashMap<>();
            HashMap<Integer, Integer> num = new HashMap<>();

            res.put(toNode, parameter);
            num.put(toNode, 1);

            result.put(fromNode, res);
            nums.put(fromNode, num);
        }
    }


    private void addExistingIntervalTripParameter(int intervalStart, TaxiTrip taxiTrip,
                                                  HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                                                  HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums, Double parameter){

        if (result.get(intervalStart).containsKey(taxiTrip.getFromEnvironmentNode())){
            addExistingPickupNodeTripParameter(intervalStart, taxiTrip, result, nums, parameter);
        } else {
            addNewPickUpNodeTripParameter(intervalStart, taxiTrip, result, nums, parameter);
        }
    }


    private void addNewIntervalTripParameter(int intervalStart, TaxiTrip taxiTrip,
                                             HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                                             HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums, Double parameter){

        HashMap<Integer, Double> nodeTrips = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> tripLengths = new HashMap<>();
        HashMap<Integer, Integer> nodeTripNums = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> tripParameterNums= new HashMap<>();

        nodeTrips.put(taxiTrip.getToEnvironmentNode(), parameter);
        tripLengths.put(taxiTrip.getFromEnvironmentNode(), nodeTrips);

        nodeTripNums.put(taxiTrip.getToEnvironmentNode(), 1);
        tripParameterNums.put(taxiTrip.getFromEnvironmentNode(), nodeTripNums);

        result.put(intervalStart, tripLengths);
        nums.put(intervalStart, tripParameterNums);
    }


    private void addExistingPickupNodeTripParameter(int intervalStart, TaxiTrip taxiTrip,
                                                    HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                                                    HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums, Double parameter){

        if (result.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).containsKey(taxiTrip.getToEnvironmentNode())){
            double num = result.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).get(taxiTrip.getToEnvironmentNode());
            int size = nums.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).get(taxiTrip.getToEnvironmentNode());

            result.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).replace(taxiTrip.getToEnvironmentNode(), num + parameter);
            nums.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).replace(taxiTrip.getToEnvironmentNode(), size + 1);

        } else {
            result.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).put(taxiTrip.getToEnvironmentNode(), parameter);
            nums.get(intervalStart).get(taxiTrip.getFromEnvironmentNode()).put(taxiTrip.getToEnvironmentNode(), 1);
        }
    }


    private void addNewPickUpNodeTripParameter(int intervalStart, TaxiTrip taxiTrip,
                                               HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                                               HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums, Double parameter){

        HashMap<Integer, Double> parameters = new HashMap<>();
        HashMap<Integer, Integer> toDestNums = new HashMap<>();
        parameters.put(taxiTrip.getToEnvironmentNode(), parameter);
        toDestNums.put(taxiTrip.getToEnvironmentNode(), 1);

        result.get(intervalStart).put(taxiTrip.getFromEnvironmentNode(), parameters);
        nums.get(intervalStart).put(taxiTrip.getFromEnvironmentNode(), toDestNums);
    }


    private void computeMean(HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result,
                             HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> nums){

        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Double>>> timeInterval : result.entrySet()){
            for (Map.Entry<Integer, HashMap<Integer, Double>> node : timeInterval.getValue().entrySet()){
                for (Map.Entry<Integer, Double> value : node.getValue().entrySet()){
                    value.setValue(value.getValue()/nums.get(timeInterval.getKey()).get(node.getKey()).get(value.getKey()));
                }
            }
        }
    }


    private void computeMeanComplete(HashMap<Integer, HashMap<Integer, Double>> result,
                             HashMap<Integer, HashMap<Integer, Integer>> nums){

        for (Map.Entry<Integer, HashMap<Integer, Double>> node :result.entrySet()){
            for (Map.Entry<Integer, Double> value : node.getValue().entrySet()){
                value.setValue(value.getValue()/nums.get(node.getKey()).get(value.getKey()));
            }
        }
    }
}
