
import domain.TaxiGraphStateModel;
import domain.actions.MeasurableAction;
import domain.actions.TaxiActionType;
import domain.states.TaxiGraphState;

import java.util.*;


public class ReachableStatesGenerator {

    private TaxiGraphStateModel taxiGraphStateModel;
    private HashSet<TaxiGraphState> reachableStates = new HashSet<>();
    private List<TaxiActionType> actionTypes;

    public ReachableStatesGenerator(TaxiGraphStateModel taxiGraphStateModel, List<TaxiActionType> actionTypes) {
        this.taxiGraphStateModel = taxiGraphStateModel;
        this.actionTypes = actionTypes;
    }

    public void performReachabilityFrom(TaxiGraphState startingState) {

        System.out.println("Starting reachability analysis");

        LinkedList<TaxiGraphState> openList = new LinkedList<>();
        TaxiGraphState currentState;
        openList.offer(startingState);
        List<MeasurableAction> applicableActions;

        while(true) {

            if (openList.isEmpty()) {
                System.out.println("Finished reachability analysis; # states: " + this.reachableStates.size());
                return;
            }

            currentState = openList.poll();
            this.reachableStates.add(currentState);

            applicableActions = allApplicableActionsForTypes(currentState);

            for (MeasurableAction action : applicableActions) {
                TaxiGraphState taxiGraphState = this.taxiGraphStateModel.stateTransitions(currentState, action);
                if (taxiGraphState != null){
                    openList.offer(taxiGraphState);
                }
            }
        }
    }


    private List<MeasurableAction> allApplicableActionsForTypes(TaxiGraphState state) {
        List<MeasurableAction> result = new ArrayList<>();

        for (TaxiActionType a : actionTypes) {
            result.addAll(a.allApplicableActions(state));
        }

        return result;
    }


    public HashSet<TaxiGraphState> getReachableStates(){
        return reachableStates;
    }
}
