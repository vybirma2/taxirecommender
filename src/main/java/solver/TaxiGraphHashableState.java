package solver;

import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;
import burlap.statehashing.simple.IISimpleHashableState;
import domain.states.TaxiGraphState;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static utils.Utils.*;

/**
 * Container for TaxiGraphState used in search for reachable states
 */
public class TaxiGraphHashableState extends IISimpleHashableState {


    public TaxiGraphHashableState(State s) {
        super(s);
    }


    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 31);

        this.appendHashCodeForValue(hashCodeBuilder, VAR_NODE, ((TaxiGraphState)s).getNodeId());
        this.appendHashCodeForValue(hashCodeBuilder, VAR_TIMESTAMP, ((TaxiGraphState)s).getTimeStamp());
        this.appendHashCodeForValue(hashCodeBuilder, VAR_STATE_OF_CHARGE, ((TaxiGraphState)s).getStateOfCharge());

        return hashCodeBuilder.toHashCode();
    }


    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof HashableState && this.statesEqual(this.s, ((HashableState) obj).s());
        }
    }


    protected boolean statesEqual(State s1, State s2) {
        TaxiGraphState state1 = (TaxiGraphState) s1;
        TaxiGraphState state2 = (TaxiGraphState) s2;

        return  state1.getStateOfCharge() == state2.getStateOfCharge()
                && state1.getNodeId() == state2.getNodeId()
                && state1.getTimeStamp() == state2.getTimeStamp();
    }
}
