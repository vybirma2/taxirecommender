package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.states.TaxiGraphState;


public class PickUpPassengerAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction  {

    private int toNodeId;
    private long actionTime;
    private int energyConsumption;


    public PickUpPassengerAction(int aId, int toNodeId, long actionTime, int energyConsumption) {
        super(aId);
        this.toNodeId = toNodeId;
        this.actionTime = actionTime;
        this.energyConsumption = energyConsumption;
    }


    @Override
    public String actionName() {
        return ActionTypes.PICK_UP_PASSENGER.getName();
    }


    @Override
    public Action copy() {
        return new PickUpPassengerAction(this.aId, toNodeId, actionTime, energyConsumption);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return (int) actionTime;
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return energyConsumption;
    }


    public int getToNodeId() {
        return toNodeId;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PickUpPassengerAction that = (PickUpPassengerAction)o;
            return (this.aId == that.aId) && this.actionTime == that.actionTime && this.toNodeId == that.toNodeId
                    && this.energyConsumption == that.energyConsumption;
        } else {
            return false;
        }
    }
}
