package parameterestimation;

import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ParameterEstimator {

    private ArrayList<TaxiTrip> taxiTrips;
    private HashMap<Integer, HashMap<Integer, Integer>> passengerPickUpProbability;

    public ParameterEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
        estimateParameters();
    }


    private void estimateParameters(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips();
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, Integer> intervalPickUps = new HashMap<>();
            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalPickUps.containsKey(taxiTrip.getPickUpRoadNode().getId())){
                    intervalPickUps.replace(taxiTrip.getPickUpRoadNode().getId(), intervalPickUps.get(taxiTrip.getPickUpRoadNode().getId()) + 1 );
                } else {
                    intervalPickUps.put(taxiTrip.getPickUpRoadNode().getId(), 1);
                }
            }
            pickupsInOSMNodes.put(entry.getKey(), intervalPickUps);
        }

        passengerPickUpProbability = new HashMap<>();

    }


    private HashMap<Integer, ArrayList<TaxiTrip>> getTimeSortedTrips(){
        int estimationTime = Utils.SHIFT_START_TIME;

        Collections.sort(taxiTrips);

        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = new HashMap<>();

        ArrayList<TaxiTrip> trips = new ArrayList<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (beforeTripTime(taxiTrip, Utils.SHIFT_START_TIME)){
                continue;
            } else if (afterTripTime(taxiTrip, estimationTime) && beforeTripTime(taxiTrip, estimationTime + Utils.ESTIMATION_EPISODE_LENGTH)){
                trips.add(taxiTrip);
            } else {
                timeSortedTaxiTrips.put(estimationTime, trips);
                trips = new ArrayList<>();
                estimationTime += Utils.ESTIMATION_EPISODE_LENGTH;
                if (estimationTime >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME){
                    return timeSortedTaxiTrips;
                }
                trips.add(taxiTrip);
            }

            if (afterTripTime(taxiTrip, Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH + 1)) {
                break;
            }
        }

        return timeSortedTaxiTrips;
    }


    private boolean beforeTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() <= time;
    }


    private boolean afterTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() >= time;
    }

}
