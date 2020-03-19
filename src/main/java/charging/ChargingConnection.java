package charging;

import java.io.Serializable;

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

    public void setId(int id) {
        this.id = id;
    }

    public double getPowerKW() {
        return powerKW;
    }

    public void setPowerKW(double powerKW) {
        this.powerKW = powerKW;
    }

    public double getPrizeForKW() {
        return prizeForKW;
    }

    public void setPrizeForKW(double prizeForKW) {
        this.prizeForKW = prizeForKW;
    }
}
