package domain.environmentrepresentation.fullenvironment;

import domain.environmentrepresentation.Environment;
import parameterestimation.TaxiTrip;
import utils.DistanceGraphUtils;

import java.io.IOException;
import java.util.List;


public class FullEnvironment extends Environment<FullEnvironmentNode, FullEnvironmentEdge> {


    @Override
    protected void setEnvironmentGraph() throws IOException, ClassNotFoundException {
        this.environmentGraph = new FullEnvironmentGraph(this.getOsmGraph());
    }

}
