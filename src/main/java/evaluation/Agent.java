package evaluation;


import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.TaxiRecommenderDomain;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;

import java.util.List;

public abstract class Agent {

    protected Graph<RoadNode, RoadEdge> osmGraph;

    public Agent(Graph<RoadNode, RoadEdge> osmGraph) {
        this.osmGraph = osmGraph;
    }

    public abstract MeasurableAction getAction(SimulationState currentState);
    public abstract boolean tripOffer(SimulationState currentState, SimulationTaxiTrip trip);
    public abstract void resetAgent();
}
