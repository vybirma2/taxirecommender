package domain.environmentrepresentation.gridenvironment;

import charging.ChargingStation;
import cz.agents.basestructures.GPSLocation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.DistanceSpeedPair;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.environmentrepresentation.EnvironmentNode;
import utils.Utils;

import java.util.*;

import static utils.DistanceGraphUtils.*;

public class GridEnvironmentGraph extends EnvironmentGraph<GridEnvironmentNode, GridEnvironmentEdge> {


    private double gridHeight;
    private double gridWidth;

    private double topLatitude;
    private double bottomLatitude;
    private double leftLongitude;
    private double rightLongitude;

    private int numOfColumns;
    private int numOfRows;

    private ArrayList<Double> longitudeGridBorders;
    private ArrayList<Double> latitudeGridBorders;

    private GridEnvironmentNode[][] gridWorld;

    private double cellHeight;
    private double cellWidth;


    public GridEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);
    }

    @Override
    public Set<Integer> getNeighbours(int nodeId) {
        return null;
    }

    @Override
    protected void setNodes() {
        nodes = new HashMap<>();
        computeGridParameters();
        createGridWorld();
    }

    @Override
    protected void setEdges() {
        edges = new HashMap<>();

        for (Map.Entry<Integer, GridEnvironmentNode> entry : nodes.entrySet()){
            GridEnvironmentNode node = entry.getValue();
            Set<Integer> neighbours = node.getNeighbours();
            HashMap<Integer, GridEnvironmentEdge> nodeEdges = new HashMap<>();
            for (Integer neighbour : neighbours){
                GridDistanceSpeedPair distanceSpeedPair = getDistancesAndSpeedBetweenNodesInGrid(node.getId(), neighbour);
                nodeEdges.put(neighbour, new GridEnvironmentEdge(node.getId(), neighbour, (float) (distanceSpeedPair.getSpeed()/3.6),
                        (int)(distanceSpeedPair.getDistance() * 1000) ));
            }
            edges.put(node.getId(), nodeEdges);
        }
    }



    private GridDistanceSpeedPair getDistancesAndSpeedBetweenNodesInGrid(int fromNodeId, int toNodeId){
        double resultDistance;
        double resultSpeed;

        LinkedList<Integer> nodePath = aStar(fromNodeId, toNodeId);


        if (nodePath != null){

            double distance = 0;
            double speed = 0;
            Integer current = nodePath.getFirst();

            for (Integer node : nodePath){
                distance += getDistanceBetweenOsmNodes(current, node);
                speed += getSpeedBetweenOsmNodes(current, node);
                current = node;
            }

            resultDistance = distance;
            resultSpeed = speed/(nodePath.size() - 1);

        } else {
            throw new IllegalArgumentException("No connection between node: " + fromNodeId + " and node: " + toNodeId);
        }


        return new GridDistanceSpeedPair(resultDistance, resultSpeed);
    }


    private void createGridWorld(){
        this.gridWorld = new GridEnvironmentNode[numOfRows][numOfColumns];

        Set<RoadNode>[][] gridWorldRoadNodes = prepareRoadNodeSets();

        for (int row = 0; row < numOfRows; row++){
            for (int column = 0; column < numOfColumns; column++){
                setGridCell(gridWorldRoadNodes[row][column], row, column);
            }
        }

        setNeighbours();
    }


    private void setNeighbours(){
        for (int row = 0; row < numOfRows; row++){
            for (int column = 0; column < numOfColumns; column++){
                if (gridWorld[row][column] != null){
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

            }
        }
    }


    private void addTopLeftCornerNeighbours(int row, int column){
        if (numOfColumns > 1 && numOfRows > 1){
            addRightNeighbour(row, column);
            addBottomNeighbour(row, column);
 //           addBottomRightNeighbour(row, column);
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
 //           addBottomLeftNeighbour(row, column);
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
  //          addTopRightNeighbour(row, column);
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
  //          addTopLeftNeighbour(row, column);
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
 //       addTopRightNeighbour(row, column);;
//        addBottomRightNeighbour(row, column);
    }


    private void addLeftInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addBottomNeighbour(row, column);
        addTopNeighbour(row, column);
  //      addTopLeftNeighbour(row, column);;
//        addBottomLeftNeighbour(row, column);
    }

    private void addTopInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
        addTopNeighbour(row, column);
      //  addTopLeftNeighbour(row, column);;
  //      addTopRightNeighbour(row, column);
    }


    private void addBottomInsideNeighbours(int row, int column){
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
        addBottomNeighbour(row, column);
    //    addBottomLeftNeighbour(row, column);;
  //      addBottomRightNeighbour(row, column);
    }

    private void addAllNeighbours(int row, int column){
        addTopNeighbour(row, column);
        addBottomNeighbour(row, column);
        addLeftNeighbour(row, column);
        addRightNeighbour(row, column);
      //  addTopLeftNeighbour(row, column);
    //    addTopRightNeighbour(row, column);
  //      addBottomLeftNeighbour(row, column);
//        addBottomRightNeighbour(row, column);
    }

    private void addTopNeighbour(int row, int column){
        int i = row - 1;
        while (i >= 0){
            if (gridWorld[i][column] != null){
                gridWorld[row][column].getNeighbours().add(gridWorld[i][column].getId());
                return;
            }
            i--;
        }
    }


    private void addBottomNeighbour(int row, int column){
        int i = row + 1;
        while (i < numOfRows){
            if (gridWorld[i][column] != null){
                gridWorld[row][column].getNeighbours().add(gridWorld[i][column].getId());
                return;
            }
            i++;
        }
    }


    private void addLeftNeighbour(int row, int column){
        int i = column - 1;
        while (i >= 0){
            if (gridWorld[row][i] != null){
                gridWorld[row][column].getNeighbours().add(gridWorld[row][i].getId());
                return;
            }
            i--;
        }
    }


    private void addRightNeighbour(int row, int column){
        int i = column + 1;
        while (i < numOfColumns){
            if (gridWorld[row][i] != null){
                gridWorld[row][column].getNeighbours().add(gridWorld[row][i].getId());
                return;
            }
            i++;
        }
    }

    private void addTopLeftNeighbour(int row, int column){
        gridWorld[row][column].getNeighbours().add(gridWorld[row - 1][column - 1].getId());
    }

    private void addTopRightNeighbour(int row, int column){
        gridWorld[row][column].getNeighbours().add(gridWorld[row - 1][column + 1].getId());
    }

    private void addBottomLeftNeighbour(int row, int column){
        gridWorld[row][column].getNeighbours().add(gridWorld[row + 1][column - 1].getId());
    }

    private void addBottomRightNeighbour(int row, int column){
        gridWorld[row][column].getNeighbours().add(gridWorld[row + 1][column + 1].getId());
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

    private void setGridCell(Set<RoadNode> nodes, int row, int column){
        double left = longitudeGridBorders.get(column);
        double right = left + cellWidth;
        double bottom = latitudeGridBorders.get(row);
        double top = bottom + cellHeight;

        double longitude = (right - left)/2 + left;
        double latitude = (top - bottom)/2 + bottom;

        RoadNode centerNode = chooseRoadNode(nodes, longitude, latitude);

        if (centerNode != null){
            gridWorld[row][column] = new GridEnvironmentNode( centerNode.id, centerNode.sourceId,
                    new GPSLocation(centerNode.latE6, centerNode.lonE6, centerNode.latProjected,
                            centerNode.lonProjected, centerNode.elevation), new HashSet<>(), top, bottom, left, right);
            gridWorld[row][column].setOsmNodes(nodes);
            this.nodes.put(gridWorld[row][column].getId(), gridWorld[row][column]);
        }
    }


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


    private Integer getRowCoordinate(RoadNode node){
        for (int i = numOfRows - 1; i >= 0; i--){
            if (node.getLatitude() > latitudeGridBorders.get(i) ){
                return i;
            }
        }
        return null;
    }


    private Integer getColumnCoordinate(RoadNode node){
        for (int i = numOfColumns - 1; i >= 0; i--){
            if (node.getLongitude() > longitudeGridBorders.get(i) ){
                return i;
            }
        }
        return null;
    }


    private void computeGridParameters(){
        setBoundingBox();
        setCellProperties();
    }


    private void setBoundingBox(){
        leftLongitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLongitude).min(Double::compareTo).get();
        bottomLatitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLatitude).min(Double::compareTo).get();
        rightLongitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLongitude).max(Double::compareTo).get();
        topLatitude = osmGraph.getAllNodes().parallelStream().map(GPSLocation::getLatitude).max(Double::compareTo).get();
    }


    private void setCellProperties(){
        gridHeight = getDistance(leftLongitude, topLatitude, leftLongitude, bottomLatitude) * 1000;
        gridWidth = getDistance(leftLongitude, topLatitude, rightLongitude, topLatitude) * 1000;

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

}
