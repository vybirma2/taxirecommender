package domain.environmentrepresentation.gridenvironment;

import domain.environmentrepresentation.Environment;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import parameterestimation.TaxiTrip;
import utils.Utils;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;


public class GridEnvironment extends Environment<GridEnvironmentNode, GridEnvironmentEdge> {
    @Override
    protected void setEnvironmentGraph() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + Utils.ONE_GRID_CELL_WIDTH + "x" + Utils.ONE_GRID_CELL_HEIGHT + "grid_environment.fst");


        if(!file.exists()) {
            this.environmentGraph = new GridEnvironmentGraph(this.getOsmGraph());

            FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
            out.writeObject(this.environmentGraph);
            out.close();
        } else {
            FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
            this.environmentGraph = (GridEnvironmentGraph) in.readObject();
            in.close();
        }
        System.out.println(this.environmentGraph);
    }

}
