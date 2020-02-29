package domain.actions;

public enum ActionTypes {
    TO_NEXT_LOCATION(0, "to_next_location"),
    STAYING_IN_LOCATION(1, "staying_in_location"),
    GOING_TO_CHARGING_STATION(2, "going_to_charging_station"),
    CHARGING_IN_CHARGING_STATION(3, "charging_in_charging_station");

    private final int value;
    private final String name;

    ActionTypes(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue(){
        return value;
    }

    public String getName() {
        return name;
    }
}
