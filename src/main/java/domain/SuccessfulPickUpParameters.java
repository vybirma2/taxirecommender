package domain;

import java.util.ArrayList;

public class SuccessfulPickUpParameters {
    private ArrayList<Double> probabilities;
    private ArrayList<Double> energyConsumptionCost;
    private ArrayList<Double> tripReward;
    private ArrayList<Double> futureStateReward;


    public SuccessfulPickUpParameters(ArrayList<Double> probabilities, ArrayList<Double> energyConsumptionCost,
                                      ArrayList<Double> tripReward, ArrayList<Double> futureStateReward ) {
        this.probabilities = probabilities;
        this.energyConsumptionCost = energyConsumptionCost;
        this.tripReward = tripReward;
        this.futureStateReward = futureStateReward;
    }


    public ArrayList<Double> getProbabilities() {
        return probabilities;
    }

    public ArrayList<Double> getEnergyConsumptionCost() {
        return energyConsumptionCost;
    }

    public ArrayList<Double> getTripReward() {
        return tripReward;
    }

    public ArrayList<Double> getFutureStateReward() {
        return futureStateReward;
    }
}
