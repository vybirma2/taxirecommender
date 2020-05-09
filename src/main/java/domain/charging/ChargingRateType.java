package domain.charging;

public enum ChargingRateType {

    SPEED_CHARGING(15,Integer.MAX_VALUE, "speed"),
    STANDARD_CHARGING(10, 60, "standard"),
    SLOW_CHARGING(5,20, "slow");

    private final int rate;
    private final int KWMax;
    private final String name;


    ChargingRateType(int rate, int KWmax, String name) {
        this.rate = rate;
        this.KWMax = KWmax;
        this.name = name;
    }


    public final int getValue(){
        return rate;
    }

    public int getKWMax() {
        return KWMax;
    }

    public int getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    public static String getNameOfCharging(int value){
        switch (value){
            case 15:
                return SPEED_CHARGING.name;
            case 10:
                return STANDARD_CHARGING.name;
            case 5:
                return SLOW_CHARGING.name;
        }
        return "";
    }

}
