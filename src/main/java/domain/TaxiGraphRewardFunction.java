package domain;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaxiGraphRewardFunction implements RewardFunction {
    @Override
    public double reward(State state, Action action, State state1) {

        return 0;
    }


    public void computeRewardForStates(List<State> states){

        List<TaxiGraphState> taxiGraphStates = new ArrayList<>();
        for (State state : states){
            taxiGraphStates.add((TaxiGraphState)state);
        }

        Collections.sort(taxiGraphStates);


        System.out.println("ddd");

    }


}
