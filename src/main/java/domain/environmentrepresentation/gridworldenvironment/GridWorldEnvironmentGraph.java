package domain.environmentrepresentation.gridworldenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.utils.DistanceGraphUtils;
import domain.utils.DistanceSpeedPairTime;
import domain.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static domain.utils.DistanceGraphUtils.*;

/**
 * Grid world environment consisting of grid which cell consists of grid node to which original road nodes are fitted
 * each cell is defined by the most centric node
 */
public class GridWorldEnvironmentGraph extends EnvironmentGraph<GridWorldEnvironmentNode, GridWorldEnvironmentEdge> implements Serializable {

    private double cellHeight;
    private double cellWidth;

    private double topLatitude;
    private double bottomLatitude;
    private double leftLongitude;
    private double rightLongitude;

    private int numOfColumns;
    private int numOfRows;

    private ArrayList<Double> longitudeGridBorders;
    private ArrayList<Double> latitudeGridBorders;
    private GridWorldEnvironmentNode[][] gridWorld;


    public GridWorldEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) throws IOException, ClassNotFoundException {
        super(osmGraph);
    }


    /**
     * Computing grid parameters according to the given osmGraph - heights, widths, grid borders...
     * fitting of nodes to grid cells, defining cell neighbours
     */
    @Override
    protected void setNodes() {
        nodes = new HashMap<>();
        computeGridParameters();
        createGridWorld();
    }

    private void computeGridParameters(){
        setBoundingBox();
        setCellProperties();
    }

    /**
     * Setting edges between neighbouring nodes in the grid world
     */
    @Override
    protected void setEdges() {
        edges = new HashMap<>();

        for (Map.Entry<Integer, GridWorldEnvironmentNode> entry : nodes.entrySet()){
            GridWorldEnvironmentNode node = entry.getValue();
            HashMap<Integer, GridWorldEnvironmentEdge> nodeEdges = new HashMap<>();

            for (Integer neighbour : nodes.get(entry.getKey()).getNeighbours()){

                if (!entry.getKey().equals(neighbour)){
                    DistanceSpeedPairTime distanceSpeedPairTime = getDistancesAndSpeedBetweenNodes(entry.getKey(), neighbour);
                    double distance = (distanceSpeedPairTime.getDistance());
                    float speed = (float) (distanceSpeedPairTime.getSpeed());
                    nodeEdges.put(neighbour, new GridWorldEnvironmentEdge(entry.getKey(), neighbour, speed,
                            (int)(distance * 1000), DistanceGraphUtils.getTripTime(distance, speed)));
                }
            }
            edges.put(node.getNodeId(), nodeEdges);
        }
    }

    private void createGridWorld(){
        this.gridWorld = new GridWorldEnvironmentNode[numOfRows][numOfColumns];
        Set<RoadNode>[][] gridWorldRoadNodes = prepareRoadNodeSets();

        for (int row = 0; row < numOfRows; row++){
            for (int column = 0; column < numOfColumns; column++){
                setGridCell(gridWorldRoadNodes[row][column], row, column);
            }
        }

        setNeighbours();
    }

    /**
     * Adding neighbours in all 8 directions top, bottom, left, right and diagonals
     */
    private void setNeighbours(){
        for (int row = 0; row < numOfRows; row++){
            for (int column = 0; column < numOfColumns; column++){
                if (gridWorld[row][column] != null){
                    addNeighbours(row, column);
                }
            }
        }
    }

    private void addNeighbours(int row, int column){
        if (insideGrid(row, column)){
            addAllNeighbours(row, column);
        } else if (topInside(row, column)){
            addBottomInsideNeighbours(row, column);
        } else if (leftInside(row, column)){
            addRightInsideNeighbours(row, column);
        } else if (rightInside(row, column)){
            addLeftInsideNeighbours(row, column);
        } else if (bottomInside(row, column)){
            addTopInsideNeighbours(row, column);
        } else if (topLeft(row, column)){
            addTopLeftCornerNeighbours(row, column);
        } else if (topRight(row, column)){
            addTopRightCornerNeighbours(row, column);
        } else if (bottomLeft(row, column)){
            addBottomLeftCornerNeighbours(row, column);
        } else if (bottomRight(row, column)){
            addBottomRightCornerNeighbours(row, column);
        }
    }

    private void addTopLeftCornerNeighbours(int row, int column){
        if (numOfColumns > 1 && numOfRows > 1){
            addRightNeighbour(row, column);
            addBottomNeighbour(row, column);
            addBottomRightNeighbour(row, column);
        } else if (numOfColumns > 1){
            addRightNeighbour(row, column);
        } else if (numOfRows > 1){
            addBottomNeighbour(row, column);
        }
    }

    private void addTopRightCornerNeighbours(int row, int column){
        if (numOfColumns > 1 && numOfRows > 1){
            addLeftNeighbour(row, column);
            addBottomNeighbour(row, column);
            addBottomLeftNeighbour(row, column);
        } else if (numOfColumns > 1){
            addLeftNeighbour(row, column);
        } else if (numOfRows > 1){
            addBottomNeighbour(row, column);
        }
    }

    private void addBottomLeftCornerNeighbours(int row, int column){
        if (numOfColumns > 1 && numOfRows > 1){
            addRightNeighbour(row, column);
            addTopNeighbour(row, column);
            addTopRightNeighbour(row, column);
        } else if (numOfColumns > 1){
            addRightNeighbour(row, column);
        } else if (numOfRows > 1){
            addTopNeighbour(row, column);
        }
    }

    private void addBottomRightCornerNeighbours(int row, int column){
        if (numOfColumns > 1 && numOfRows > 1){
            addLeftNeighbour(row, column);
            addTopNeighbour(row, column);
            addTopLeftNeighbour(row, column);
        } else if (numOfColumns > 1){
            addLeftNeighbour(row, column);
        } else if (numOfRows > 1){
            addTopNeighbour(row, column);
        }
    }

    private void addRightInsideNeighbours(int row, int column){
        addRightNeighbour(row, column);
        addBottomNeighbour(row, column);
        addTopNeighbour(row, column);
        addTopRightNeighbour(row, column);;
        addBottomRightNeighbour(row, column);
    }

    private void addLeftInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addBottomNeighbour(row, column);
        addTopNeighbour(row, column);
        addTopLeftNeighbour(row, column);;
        addBottomLeftNeighbour(row, column);
    }

    private void addTopInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
        addTopNeighbour(row, column);
        addTopLeftNeighbour(row, column);;
        addTopRightNeighbour(row, column);
    }

    private void addBottomInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
        addBottomNeighbour(row, column);
        addBottomLeftNeighbour(row, column);;
        addBottomRightNeighbour(row, column);
    }

    private void addAllNeighbours(int row, int column){
        addTopNeighbour(row, column);
        addBottomNeighbour(row, column);
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
        addTopLeftNeighbour(row, column);
        addTopRightNeighbour(row, column);
        addBottomLeftNeighbour(row, column);
        addBottomRightNeighbour(row, column);
    }

    private void addTopNeighbour(int row, int column){
        int i = row - 1;
        while (i >= 0){
            if (gridWorld[i][column] != null){
                gridWorld[row][column].addNeighbour(gridWorld[i][column].getNodeId());
                return;
            }
            i--;
        }
    }

    private void addBottomNeighbour(int row, int column){
        int i = row + 1;
        while (i < numOfRows){
            if (gridWorld[i][column] != null){
                gridWorld[row][column].addNeighbour(gridWorld[i][column].getNodeId());
                return;
            }
            i++;
        }
    }

    private void addLeftNeighbour(int row, int column) {
        int i = column - 1;
        while (i >= 0) {
            if (gridWorld[row][i] != null) {
                gridWorld[row][column].addNeighbour(gridWorld[row][i].getNodeId());
                return;
            }
            i--;
        }
    }

    private void addRightNeighbour(int row, int column){
        int i = column + 1;
        while (i < numOfColumns){
            if (gridWorld[row][i] != null){
                gridWorld[row][column].addNeighbour(gridWorld[row][i].getNodeId());
                return;
            }
            i++;
        }
    }

    private void addTopLeftNeighbour(int row, int column){
        int i = column - 1;
        int j = row - 1;
        while (i >= 0 && j >= 0){
            if (gridWorld[j][i] != null){
                gridWorld[row][column].addNeighbour(gridWorld[j][i].getNodeId());
                return;
            }
            i--;
            j--;
        }
    }

    private void addTopRightNeighbour(int row, int column){
        int i = column + 1;
        int j = row - 1;
        while (i < numOfColumns && j >= 0){
            if (gridWorld[j][i] != null){
                gridWorld[row][column].addNeighbour(gridWorld[j][i].getNodeId());
                return;
            }
            i++;
            j--;
        }
    }

    private void addBottomLeftNeighbour(int row, int column){
        int i = column - 1;
        int j = row + 1;
        while (i >= 0 && j < numOfRows){
            if (gridWorld[j][i] != null){
                gridWorld[row][column].addNeighbour(gridWorld[j][i].getNodeId());
                return;
            }
            i--;
            j++;
        }
    }

    private void addBottomRightNeighbour(int row, int column){
        int i = column + 1;
        int j = row + 1;
        while (i < numOfColumns && j < numOfRows){
            if (gridWorld[j][i] != null){
                gridWorld[row][column].addNeighbour(gridWorld[j][i].getNodeId());
                return;
            }
            i++;
            j++;
        }
    }

    private boolean topLeft(int row, int column){
        return row == 0 && column == 0;
    }

    private boolean topRight(int row, int column){
        return row == 0 && column == numOfColumns - 1;
    }

    private boolean bottomLeft(int row, int column){
        return row == numOfRows - 1 && column == 0;
    }

    private boolean bottomRight(int row, int column){
        return row == numOfRows -1 && column == numOfColumns - 1;
    }

    private boolean topInside(int row, int column){
        return row == 0 && column > 0 && column < numOfColumns - 1 && numOfRows > 1;
    }

    private boolean leftInside(int row, int column){
        return column == 0 && row > 0 && row < numOfRows - 1 && numOfColumns > 1;
    }

    private boolean rightInside(int row, int column){
        return column == numOfColumns - 1 && row > 0 && row < numOfRows - 1 && numOfColumns > 1;
    }

    private boolean bottomInside(int row, int column){
        return row == numOfRows - 1 && column > 0 && column < numOfColumns - 1 && numOfRows > 1;
    }

    private boolean insideGrid(int row, int column){
        return row > 0 && row < numOfRows - 1 && column > 0 && column < numOfColumns - 1;
    }

    /**
     * Choosing center node from given inside nodes
     * @param nodes nodes fitted to grid cell
     * @param row
     * @param column
     */
    private void setGridCell(Set<RoadNode> nodes, int row, int column){
        double left = longitudeGridBorders.get(column);
        double right = left + cellWidth;
        double bottom = latitudeGridBorders.get(row);
        double top = bottom + cellHeight;

        double longitude = (right - left)/2 + left;
        double latitude = (top - bottom)/2 + bottom;

        RoadNode centerNode = chooseRoadNode(nodes, longitude, latitude);

        if (centerNode != null){
            gridWorld[row][column] = new GridWorldEnvironmentNode(centerNode, new HashSet<>());
            this.nodes.put(gridWorld[row][column].getNodeId(), gridWorld[row][column]);
        }
    }

    /**
     * @return 2d array of fitted nodes into individual cells of the grid world
     */
    private Set<RoadNode>[][] prepareRoadNodeSets(){
        Set<RoadNode>[][] gridWorldRoadNodes = new Set[numOfRows][numOfColumns];

        for (int i = 0; i < numOfRows; i++){
            for (int j = 0; j < numOfColumns; j++){
                gridWorldRoadNodes[i][j] = new HashSet<>();
            }
        }

        for (RoadNode node : osmGraph.getAllNodes()) {
            Integer row = getRowCoordinate(node);
            Integer column = getColumnCoordinate(node);

            if (row != null && column != null) {
                gridWorldRoadNodes[row][column].add(node);
            }
        }

        return gridWorldRoadNodes;
    }

    /**
     * @param node
     * @return row coordinate of node corresponding to grid latitude borders
     */
    public Integer getRowCoordinate(RoadNode node){
        for (int i = numOfRows - 1; i >= 0; i--){
            if (node.getLatitude() >= latitudeGridBorders.get(i) ){
                return i;
            }
        }
        return null;
    }

    /**
     * @param node
     * @return column coordinate of node corresponding to grid longitude borders
     */
    public Integer getColumnCoordinate(RoadNode node){
        for (int i = numOfColumns - 1; i >= 0; i--){
            if (node.getLongitude() >= longitudeGridBorders.get(i) ){
                return i;
            }
        }
        return null;
    }

    /**
     * Choosing corner nodes and setting bounding box for grid world
     */
    private void setBoundingBox(){
        leftLongitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLongitude).min(Double::compareTo).get();
        bottomLatitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLatitude).min(Double::compareTo).get();
        rightLongitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLongitude).max(Double::compareTo).get();
        topLatitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLatitude).max(Double::compareTo).get();
    }

    /**
     * computing and setting properties for cells in the grid world according to the set parameters
     */
    private void setCellProperties(){
        double gridHeight = getEuclideanDistance(leftLongitude, topLatitude, leftLongitude, bottomLatitude) * 1000;
        double gridWidth = getEuclideanDistance(leftLongitude, topLatitude, rightLongitude, topLatitude) * 1000;

        numOfColumns = (int)(gridWidth / Utils.ONE_GRID_CELL_WIDTH);
        numOfRows = (int)(gridHeight / Utils.ONE_GRID_CELL_HEIGHT);

        cellHeight = (topLatitude - bottomLatitude)/numOfRows;
        cellWidth = (rightLongitude - leftLongitude)/numOfColumns;

        longitudeGridBorders = new ArrayList<>();
        latitudeGridBorders = new ArrayList<>();

        for (int i = 0; i <= numOfColumns; i++){
            longitudeGridBorders.add(leftLongitude + i * cellWidth);
        }

        for (int i = 0; i <= numOfRows; i++){
            latitudeGridBorders.add(bottomLatitude + i * cellHeight);
        }
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.numOfRows; i++){
            for (int j = 0; j < this.numOfColumns; j++){
                if (this.gridWorld[i][j] != null){
                    result.append(this.gridWorld[i][j].getNodeId()).append("   ");
                } else {
                    result.append("        ");
                }

            }
            result.append("\n");
        }
        return result.toString();
    }
}
