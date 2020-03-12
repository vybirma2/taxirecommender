package parameterestimation;

import java.util.*;


public class ParameterEstimator {

    private PassengerPickUpEstimator passengerPickUpEstimator;

    private ArrayList<TaxiTrip> taxiTrips;
    private HashMap<Integer, HashMap<Integer, Integer>> passengerPickUpProbability;
    private Set<Integer> timeIntervals;


    public ParameterEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
        this.passengerPickUpEstimator = new PassengerPickUpEstimator(taxiTrips);
        estimateParameters();
    }


    private void estimateParameters(){
        passengerPickUpProbability = passengerPickUpEstimator.estimatePickUpProbability();
        timeIntervals = passengerPickUpEstimator.getTimeIntervals();
    }
}
