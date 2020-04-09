package domain.environmentrepresentation.gridenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Set;

/**
 * Grid wold node/cell with its bounding box coordinates and set of fitted nodes
 */
public class GridEnvironmentNode extends EnvironmentNode implements Serializable {

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
}
