package solver;

import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;
import burlap.statehashing.simple.SimpleHashableStateFactory;

public class TaxiGraphHashableFactory extends SimpleHashableStateFactory {


    public TaxiGraphHashableFactory() {
    }


    public HashableState hashState(State s) {
        return new TaxiGraphHashableState(s);
    }
}
