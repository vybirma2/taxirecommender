package domain.environmentrepresentation.gridenvironment;

import domain.environmentrepresentation.Environment;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import utils.Utils;

import java.io.*;



/**
 * Grid environment with original osm nodes fitted in grid world
 */
public class GridEnvironment extends Environment<GridEnvironmentNode, GridEnvironmentEdge> {


    @Override
    protected void setEnvironmentGraph() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + Utils.ONE_GRID_CELL_WIDTH + "x" + Utils.ONE_GRID_CELL_HEIGHT + "grid_environment.fst");

        if (!file.exists()) {
            createGridEnvironmentAndSerializeIt(file);
        } else {
            loadSerializedEnvironment(file);
        }
    }


    private void createGridEnvironmentAndSerializeIt(File file) throws IOException {
        this.environmentGraph = new GridEnvironmentGraph(this.getOsmGraph());

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(this.environmentGraph);
        out.close();
    }


    private void loadSerializedEnvironment(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        this.environmentGraph = (GridEnvironmentGraph) in.readObject();
        in.close();
    }
}
