package domain.actions;

/**
 * All available actions
 */
public enum ActionTypes {

    TO_NEXT_LOCATION(0, "to_next_location"),
    STAYING_IN_LOCATION(1, "staying_in_location"),
    GOING_TO_CHARGING_STATION(2, "going_to_charging_station"),
    CHARGING_IN_CHARGING_STATION(3, "charging_in_charging_station"),
    PICK_UP_PASSENGER(4, "pick_up_passenger");

    private final int value;
    private final String name;


    ActionTypes(int value, String name) {
        this.value = value;
        this.name = name;
    }


    public final int getValue(){
        return value;
    }


    public String getName() {
        return name;
    }

    public static String getNameOfAction(int value){
        switch (value){
            case 0:
                return TO_NEXT_LOCATION.name;
            case 1:
                return STAYING_IN_LOCATION.name;
            case 2:
                return GOING_TO_CHARGING_STATION.name;
            case 3:
                return CHARGING_IN_CHARGING_STATION.name;
            case 4:
                return PICK_UP_PASSENGER.name;
        }
        return "";
    }
}
