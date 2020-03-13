package parameterestimation;

import java.util.*;


public class ParameterEstimator {

    private PassengerPickUpEstimator passengerPickUpEstimator;
    private PassengerDestinationEstimator passengerDestinationEstimator;

    private ArrayList<TaxiTrip> taxiTrips;
    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> passengerDestinationProbability;
    private Set<Integer> timeIntervals;


    public ParameterEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
        this.passengerPickUpEstimator = new PassengerPickUpEstimator(taxiTrips);
        this.passengerDestinationEstimator = new PassengerDestinationEstimator(taxiTrips);
        estimateParameters();
    }


    private void estimateParameters(){
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


    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }
}
