import domain.TaxiRecommenderDomainGenerator;
import domain.environmentrepresentation.gridenvironment.GridEnvironment;
import domain.states.TaxiGraphState;
import utils.Utils;

public class Main {


    public static void main(String[] args) {

        TaxiRecommenderDomainGenerator taxiRecommenderDomainGenerator = null;
        try {
            taxiRecommenderDomainGenerator = new TaxiRecommenderDomainGenerator(
                    "prague_full.fst",
                    "prague_charging_stations_full.json",
                    new GridEnvironment());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            ReachableStatesGenerator planner = new ReachableStatesGenerator(taxiRecommenderDomainGenerator.getTaxiGraphStateModel(), taxiRecommenderDomainGenerator.getActionTypes());
            TaxiGraphState initialState = new TaxiGraphState(taxiRecommenderDomainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getId(), 40, Utils.SHIFT_START_TIME);
            planner.performReachabilityFrom(initialState);

            taxiRecommenderDomainGenerator.getRewardFunction().computeRewardForStates(planner.getReachableStates());


            TaxiGraphState state = initialState;
            while (state.getMaxNextState() != null){
                System.out.println(state.getMaxRewardAction().getActionId());
                System.out.println(state.getMaxNextState());
                System.out.println();
                state = state.getMaxNextState();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
