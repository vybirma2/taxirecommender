package domain.environmentrepresentation.gridenvironment;

import domain.environmentrepresentation.EnvironmentEdge;

import java.io.Serializable;

public class GridEnvironmentEdge extends EnvironmentEdge implements Serializable {


    public GridEnvironmentEdge(int fromId, int toId, float speed, int length, int time) {
        super(fromId, toId, speed, length, time);
    }
}
