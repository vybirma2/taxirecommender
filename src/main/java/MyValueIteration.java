
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.action.ActionUtils;
import burlap.mdp.core.state.State;
import domain.TaxiGraphStateModel;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class MyValueIteration {

    private TaxiGraphStateModel taxiGraphStateModel;
    private HashSet<State> reachableStates = new HashSet<>();
    private List<ActionType> actionTypes;

    public MyValueIteration(TaxiGraphStateModel taxiGraphStateModel, List<ActionType> actionTypes) {
        this.taxiGraphStateModel = taxiGraphStateModel;
        this.actionTypes = actionTypes;
    }

    public boolean performReachabilityFrom(State si) {
        System.out.println("Starting reachability analysis");
        LinkedList<State> openList = new LinkedList<>();
        openList.offer(si);

        while(true) {
            State sh;

            if (openList.isEmpty()) {
                System.out.println("Finished reachability analysis; # states: " + this.reachableStates.size());
                return true;
            }

            sh = openList.poll();

            this.reachableStates.add(sh);
            List<Action> actions = ActionUtils.allApplicableActionsForTypes(actionTypes, sh);

            for (Action action : actions) {
                List<StateTransitionProb> tps = this.taxiGraphStateModel.stateTransitions(sh, action);

                for (StateTransitionProb tp : tps) {
                    openList.offer(tp.s);
                }
            }
        }
    }


    public HashSet<State> getReachableStates(){
        return reachableStates;
    }
}
