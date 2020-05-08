package domain.environmentrepresentation.gridworldenvironment;

import domain.environmentrepresentation.EnvironmentEdge;

import java.io.Serializable;

public class GridWorldEnvironmentEdge extends EnvironmentEdge implements Serializable {


    public GridWorldEnvironmentEdge(int fromId, int toId, float speed, int length, int time) {
        super(fromId, toId, speed, length, time);
    }
}
