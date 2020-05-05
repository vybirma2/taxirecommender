package domain.environmentrepresentation.fullenvironment;

import domain.environmentrepresentation.Environment;

import java.io.IOException;


public class FullEnvironment extends Environment<FullEnvironmentNode, FullEnvironmentEdge> {


    @Override
    protected void setEnvironmentGraph() throws IOException, ClassNotFoundException {
        this.environmentGraph = new FullEnvironmentGraph(this.getOsmGraph());
    }

}
