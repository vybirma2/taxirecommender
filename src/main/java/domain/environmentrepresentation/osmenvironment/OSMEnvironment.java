package domain.environmentrepresentation.osmenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.Environment;
import domain.parameterestimation.TaxiTrip;

import java.util.List;


public class OSMEnvironment extends Environment<OSMEnvironmentNode, OSMEnvironmentEdge> {

    public OSMEnvironment(Graph<RoadNode, RoadEdge> osmGraph, List<TaxiTrip> trainingDataSet) {
        super(osmGraph, trainingDataSet);
    }

    @Override
    protected void setEnvironmentGraph() {
        this.environmentGraph = new OSMEnvironmentGraph(this.getOsmGraph());
    }
}
