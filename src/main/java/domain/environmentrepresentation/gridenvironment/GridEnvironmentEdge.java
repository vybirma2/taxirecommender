package domain.environmentrepresentation.gridenvironment;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import domain.environmentrepresentation.EnvironmentEdge;

import java.util.Set;

public class GridEnvironmentEdge extends EnvironmentEdge {
    public GridEnvironmentEdge(int fromId, int toId, long wayID, Set<ModeOfTransport> permittedModes, float allowedMaxSpeedInMpS, int lengthInMetres, int category) {
        super(fromId, toId, wayID, permittedModes, allowedMaxSpeedInMpS, lengthInMetres, category);
    }
}
