package charging;

import java.io.Serializable;

/**
 * Class representing one of possibly more charging connections available in charging station. Its parameters
 * characterize later charging procedure - prize of charging, speed of charging.
 */
public class ChargingConnection implements Serializable {

    private final int id;
    private final double powerKW;


    public ChargingConnection(int id, double powerKW) {
        this.id = id;
        this.powerKW = powerKW;
    }


    public int getId() {
        return id;
    }


    public double getPowerKW() {
        return powerKW;
    }

}
