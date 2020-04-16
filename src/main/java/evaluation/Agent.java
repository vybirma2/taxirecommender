package evaluation;


import charging.ChargingConnection;

import java.util.List;

public interface Agent {


    int getNodeToGoTo(SimulationState simulationState);


    int getStayingTime(SimulationState simulationState);


    int getChargingTime(SimulationState simulationState);


    Integer getChargingStation(SimulationState simulationState);


    boolean tripOffer(SimulationState simulationState, int tripToNode);


    ChargingConnection chooseConnection(SimulationState simulationState, List<ChargingConnection> connections);
}
