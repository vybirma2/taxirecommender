package domain.environmentrepresentation.gridworldenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.Environment;
import domain.parameterestimation.TaxiTrip;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import domain.utils.Utils;

import java.io.*;
import java.util.List;


/**
 * Grid environment with original osm nodes fitted in grid world
 */
public class GridWorldEnvironment extends Environment<GridWorldEnvironmentNode, GridWorldEnvironmentEdge> {


    public GridWorldEnvironment(Graph<RoadNode, RoadEdge> osmGraph, List<TaxiTrip> trainingDataSet) {
        super(osmGraph, trainingDataSet);
    }


    @Override
    protected void setEnvironmentGraph() {
        File file = new File("data/programdata/" + Utils.DATA_SET_NAME + Utils.ONE_GRID_CELL_WIDTH + "x" + Utils.ONE_GRID_CELL_HEIGHT + "grid_environment.fst");

        if (!file.exists()) {
            createGridEnvironmentAndSerializeIt(file);
        } else {
            loadSerializedEnvironment(file);
        }
    }

    private void createGridEnvironmentAndSerializeIt(File file) {
        try {
            this.environmentGraph = new GridWorldEnvironmentGraph(this.getOsmGraph());
            FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
            out.writeObject(this.environmentGraph);
            out.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadSerializedEnvironment(File file)  {
        FSTObjectInput in;
        try {
            in = new FSTObjectInput(new FileInputStream(file));
            this.environmentGraph = (GridWorldEnvironmentGraph) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
