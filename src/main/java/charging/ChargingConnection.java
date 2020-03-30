package charging;

import java.io.Serializable;

/**
 * Class representing one of possibly more charging connections available in charging station. Its parameters
 * characterize later charging procedure - prize of charging, speed of charging.
 */
public class ChargingConnection implements Serializable {

    private int id;
    private double powerKW;
    private double prizeForKW;


    public ChargingConnection(int id, double powerKW, double prizeForKW) {
        this.id = id;
        this.powerKW = powerKW;
        this.prizeForKW = prizeForKW;
    }


    public int getId() {
        return id;
    }


    public double getPowerKW() {
        return powerKW;
    }


    public double getPrizeForKW() {
        return prizeForKW;
    }
}
