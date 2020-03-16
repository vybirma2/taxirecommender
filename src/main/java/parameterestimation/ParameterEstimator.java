package parameterestimation;

import utils.Utils;

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


    private int getIntervalStart(double timeStamp){
        int intTime = (int)timeStamp;
        int rest = intTime % Utils.ESTIMATION_EPISODE_LENGTH;
        return intTime - rest;
    }

    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }
}
