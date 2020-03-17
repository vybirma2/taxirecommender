package domain.environmentrepresentation.gridenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.HashSet;
import java.util.Set;

public class GridEnvironmentNode extends EnvironmentNode {

    private Set<RoadNode> osmNodes;

    private double topLatitude;
    private double bottomLatitude;
    private double leftLongitude;
    private double rightLongitude;


    public GridEnvironmentNode(int id, long sourceId, GPSLocation location, Set<Integer> neighbours, double topLatitude,
                               double bottomLatitude, double leftLongitude,double rightLongitude) {
        super(id, sourceId, location, false, false, neighbours);

        this.topLatitude = topLatitude;
        this.bottomLatitude = bottomLatitude;
        this.leftLongitude = leftLongitude;
        this.rightLongitude = rightLongitude;
    }


    public Set<RoadNode> getOsmNodes() {
        return osmNodes;
    }

    public void setOsmNodes(Set<RoadNode> osmNodes) {
        this.osmNodes = osmNodes;
    }

    public double getTopLatitude() {
        return topLatitude;
    }

    public void setTopLatitude(double topLatitude) {
        this.topLatitude = topLatitude;
    }

    public double getBottomLatitude() {
        return bottomLatitude;
    }

    public void setBottomLatitude(double bottomLatitude) {
        this.bottomLatitude = bottomLatitude;
    }

    public double getLeftLongitude() {
        return leftLongitude;
    }

    public void setLeftLongitude(double leftLongitude) {
        this.leftLongitude = leftLongitude;
    }

    public double getRightLongitude() {
        return rightLongitude;
    }

    public void setRightLongitude(double rightLongitude) {
        this.rightLongitude = rightLongitude;
    }

    public void setNeighbours(Set<Integer> neighbours) { this.neighbours = neighbours; }

}
