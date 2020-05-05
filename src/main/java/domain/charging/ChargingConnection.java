package domain.charging;

import java.io.Serializable;

/**
 * Class representing one of possibly more domain.charging connections available in domain.charging station. Its parameters
 * characterize later domain.charging procedure - prize of domain.charging, speed of domain.charging.
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
