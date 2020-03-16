package domain.environmentrepresentation.gridenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import utils.Utils;

import java.util.List;
import java.util.Set;

import static utils.DistanceGraphUtils.getDistance;

public class GridEnvironmentGraph extends EnvironmentGraph<GridEnvironmentNode, GridEnvironmentEdge> {

    public GridEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);
    }

    @Override
    public Set<Integer> getNeighbours(int nodeId) {
        return null;
    }

    @Override
    protected void setNodes() {
        Double minLongitude = osmGraph.getAllNodes().stream().map(GPSLocation::getLongitude).min(Double::compareTo).get();
        Double minLatitude = osmGraph.getAllNodes().stream().map(GPSLocation::getLatitude).min(Double::compareTo).get();
        Double maxLongitude = osmGraph.getAllNodes().stream().map(GPSLocation::getLongitude).max(Double::compareTo).get();
        Double maxLatitude = osmGraph.getAllNodes().stream().map(GPSLocation::getLatitude).max(Double::compareTo).get();


        int height1 = (int) (getDistance(minLongitude, maxLatitude, minLongitude, minLatitude) * 1000);

        int width1 = (int) (getDistance(minLongitude, maxLatitude, maxLongitude, maxLatitude) * 1000);

        int numOfCellsWidth = width1 / Utils.ONE_GRID_CELL_WIDTH;
        int numOfCellsHeight = height1 / Utils.ONE_GRID_CELL_HEIGHT;



        System.out.println("ddd");
    }

    @Override
    protected void setEdges() {

    }


}
