package domain.environmentrepresentation.gridenvironment;

import domain.environmentrepresentation.Environment;


public class GridEnvironment extends Environment<GridEnvironmentNode, GridEnvironmentEdge> {
    @Override
    protected void setEnvironmentGraph() {
        this.environmentGraph = new GridEnvironmentGraph(this.getOsmGraph());
    }
}
