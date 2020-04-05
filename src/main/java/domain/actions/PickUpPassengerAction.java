package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.states.TaxiGraphState;

import java.util.Objects;

/**
 * Action of picking up passenger
 */
public class PickUpPassengerAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction  {

   // private int timeStamp;
    private int fromNodeId;
    private int toNodeId;
    private long actionTime;
    private int energyConsumption;


    public PickUpPassengerAction(int aId, int fromNodeId, int toNodeId, long actionTime, int energyConsumption/*, int timeStamp*/) {
        super(aId);
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.actionTime = actionTime;
        this.energyConsumption = energyConsumption;
      //  this.timeStamp = timeStamp;
    }


    @Override
    public int getActionId() {
        return this.aId;
    }


    @Override
    public String actionName() {
        return ActionTypes.PICK_UP_PASSENGER.getName();
    }


    @Override
    public Action copy() {
        return new PickUpPassengerAction(this.aId, fromNodeId, toNodeId, actionTime, energyConsumption/*, timeStamp*/);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return (int) actionTime;
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return energyConsumption;
    }


    @Override
    public int getToNodeId() {
        return toNodeId;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fromNodeId, toNodeId, actionTime, energyConsumption/*, timeStamp*/);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PickUpPassengerAction that = (PickUpPassengerAction)o;
            return (this.aId == that.aId)
                    && this.actionTime == that.actionTime
                    && this.toNodeId == that.toNodeId
                    && this.energyConsumption == that.energyConsumption
                    && this.fromNodeId == that.fromNodeId
                   /* && this.timeStamp == that.timeStamp*/;
        } else {
            return false;
        }
    }
}
