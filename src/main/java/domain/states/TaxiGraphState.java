package domain.states;


import java.util.*;

/**
 * Class representing state for planning containing information of current node, state of charge, timestamp but also
 * previous actions and states or maximal possible reward achieved by maxReward action which is computed after generating
 * all possible states
 */
public class TaxiGraphState implements Comparable<TaxiGraphState> {

    private static int stateId = 0;

    private int id;
    private int nodeId;
    private int stateOfCharge;
    private int timeStamp;

    private List<Integer> nextLocationPreviousStates;
    private List<Integer> stayingPreviousStates;
    private List<Integer> tripPreviousStates;
    private List<Integer> chargingPreviousStates;
    private List<Integer> goingChargingPreviousStates;


    private int maxRewardState;

    private double maxReward = Double.MIN_VALUE;
    private final HashMap<Integer, Double> afterTaxiTripStateRewards = new HashMap<>();


    public TaxiGraphState(int nodeId, int stateOfCharge, int timeStamp) {
        this.id = stateId++;
        this.nodeId = nodeId;
        this.stateOfCharge = stateOfCharge;
        this.timeStamp = timeStamp;
    }


    public TaxiGraphState copy() {
        return new TaxiGraphState(this.nodeId, this.stateOfCharge, this.timeStamp);
    }


    public int getStateOfCharge() {
        return stateOfCharge;
    }


    public int getTimeStamp() {
        return timeStamp;
    }


    public int getNodeId() {
        return nodeId;
    }


    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }


    public void setStateOfCharge(int stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }


    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }


    public int getId() {
        return id;
    }

    /**
     * @return maximal possible reward which were set during reward computation in reward function
     */
    public double getReward() {
        if (maxRewardState == Double.MIN_VALUE){
            return 0;
        } else {
            return maxReward;
        }
    }


    /**
     * If better than before set value, setting new maximum
     * @param reward potentially received reward
     */
    public void setActionReward(int actionId, double reward) {
        if (maxRewardState == Double.MIN_VALUE || this.maxReward < reward){
            this.maxRewardState = actionId;
            this.maxReward = reward;
        }
    }


    public void addNextLocationPreviousState(int stateId){
        if (nextLocationPreviousStates == null){
            nextLocationPreviousStates = new ArrayList<>();
        }
        nextLocationPreviousStates.add(stateId);
    }


    public void addStayingPreviousState(int stateId){
        if (stayingPreviousStates == null){
            stayingPreviousStates = new ArrayList<>();
        }
        stayingPreviousStates.add(stateId);
    }


    public void addGoingToChargingPreviousState(int stateId){
        if (goingChargingPreviousStates == null){
            goingChargingPreviousStates = new ArrayList<>();
        }
        goingChargingPreviousStates.add(stateId);
    }


    public void addChargingPreviousState(int stateId){
        if (chargingPreviousStates == null){
            chargingPreviousStates = new ArrayList<>();
        }
        chargingPreviousStates.add(stateId);
    }


    public void addTripPreviousState(int stateId){
        if (tripPreviousStates == null){
            tripPreviousStates = new ArrayList<>();
        }
        tripPreviousStates.add(stateId);
    }


    public void addAfterTaxiTripStateReward(int toNodeId, double reward){

        this.afterTaxiTripStateRewards.put(toNodeId, reward);
    }


    public Double getAfterTaxiTripStateReward(int toNodeId){
        return this.afterTaxiTripStateRewards.get(toNodeId);
    }


    public int getMaxRewardState() {
        return maxRewardState;
    }


    public boolean isStartingState(){
        return nextLocationPreviousStates.isEmpty();
    }


    @Override
    public String toString() {
        return "TaxiGraphState{" +
                "stateOfCharge=" + stateOfCharge +
                ", timeStamp=" + timeStamp +
                ", nodeId=" + nodeId +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        TaxiGraphState that = (TaxiGraphState) o;
        return nodeId == that.nodeId &&
                stateOfCharge == that.stateOfCharge &&
                timeStamp == that.timeStamp;
    }


    @Override
    public int hashCode() {
        return Objects.hash(nodeId, stateOfCharge, timeStamp);
    }


    @Override
    public int compareTo(TaxiGraphState o) {
        return Integer.compare(o.timeStamp, this.timeStamp);
    }
}
