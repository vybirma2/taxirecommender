package domain.environmentrepresentation.gridenvironment;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import domain.environmentrepresentation.EnvironmentEdge;

import java.util.HashSet;
import java.util.Set;

public class GridEnvironmentEdge extends EnvironmentEdge {
    public GridEnvironmentEdge(int fromId, int toId, float allowedMaxSpeedInMpS, int lengthInMetres) {
        super(fromId, toId, 0, new HashSet<>(), allowedMaxSpeedInMpS, lengthInMetres, 0);
    }
}
