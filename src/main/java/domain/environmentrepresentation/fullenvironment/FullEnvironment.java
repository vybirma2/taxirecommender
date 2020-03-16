package domain.environmentrepresentation.fullenvironment;

import domain.environmentrepresentation.Environment;


public class FullEnvironment extends Environment<FullEnvironmentNode, FullEnvironmentEdge> {

    @Override
    protected void setEnvironmentGraph() {
        this.environmentGraph = new FullEnvironmentGraph(this.getOsmGraph());
    }

}
