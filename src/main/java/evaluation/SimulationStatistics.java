package evaluation;

public class SimulationStatistics {

    private double rewardPerShift = 0;
    private double distanceTransferred = 0;
    private int numOfTripsDone = 0;
    private double rewardFromTrips = 0;
    private double costOfCharging = 0;
    private int totalEnergyCharged = 0;
    private int totalEnergyConsumed = 0;
    private double distanceToReachPassenger = 0;
    private double distanceWithPassenger = 0;
    private int timeSpentCharging = 0;


    public void addRewardPerShift(double rewardPerShift) {
        this.rewardPerShift += rewardPerShift;
    }

    public void addDistanceTransferred(double distanceTransferred) {
        this.distanceTransferred += distanceTransferred;
    }

    public void addNumOfTripsDone(int numOfTripsDone) {
        this.numOfTripsDone += numOfTripsDone;
    }

    public void addRewardFromTrips(double rewardFromTrips) {
        this.rewardFromTrips += rewardFromTrips;
    }

    public void addCostOfCharging(double costOfCharging) {
        this.costOfCharging += costOfCharging;
    }

    public void addTotalEnergyCharged(int totalEnergyCharged) {
        this.totalEnergyCharged += totalEnergyCharged;
    }

    public void addTotalEnergyConsumed(int totalEnergyConsumed) {
        this.totalEnergyConsumed += totalEnergyConsumed;
    }

    public void addDistanceToReachPassenger(double distanceToReachPassenger) {
        this.distanceToReachPassenger += distanceToReachPassenger;
    }

    public void addDistanceWithPassenger(double distanceWithPassenger) {
        this.distanceWithPassenger += distanceWithPassenger;
    }

    public void addTimeSpentCharging(int timeSpentCharging) {
        this.timeSpentCharging += timeSpentCharging;
    }

    @Override
    public String toString() {
        return  rewardPerShift +
                " " + distanceTransferred +
                " " + numOfTripsDone +
                " " + rewardFromTrips +
                " " + costOfCharging +
                " " + totalEnergyCharged +
                " " + totalEnergyConsumed +
                " " + distanceToReachPassenger +
                " " + distanceWithPassenger +
                " " + timeSpentCharging;
    }
}
