package domain.utils;

import java.util.ArrayList;

/**
 * Helping class gathering probabilities, rewards and future state rewards of taxi trips with passengers
 */
public class SuccessfulPickUpParameters {

    private ArrayList<Double> probabilities;
    private ArrayList<Double> tripReward;
    private ArrayList<Double> futureStateReward;


    public SuccessfulPickUpParameters(ArrayList<Double> probabilities,
                                      ArrayList<Double> tripReward, ArrayList<Double> futureStateReward ) {
        this.probabilities = probabilities;
        this.tripReward = tripReward;
        this.futureStateReward = futureStateReward;
    }


    public ArrayList<Double> getProbabilities() {
        return probabilities;
    }


    public ArrayList<Double> getTripReward() {
        return tripReward;
    }


    public ArrayList<Double> getFutureStateReward() {
        return futureStateReward;
    }
}
