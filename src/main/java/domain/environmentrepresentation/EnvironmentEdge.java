package domain.environmentrepresentation;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import cz.agents.multimodalstructures.edges.RoadEdge;

import java.util.Set;

public abstract class EnvironmentEdge extends RoadEdge {
    public EnvironmentEdge(int fromId, int toId, long wayID, Set<ModeOfTransport> permittedModes, float allowedMaxSpeedInMpS, int lengthInMetres, int category) {
        super(fromId, toId, wayID, permittedModes, allowedMaxSpeedInMpS, lengthInMetres, category);
    }
}
