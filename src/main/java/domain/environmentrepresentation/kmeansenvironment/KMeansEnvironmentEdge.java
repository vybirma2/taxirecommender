package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import domain.environmentrepresentation.EnvironmentEdge;

import java.util.Set;

public class KMeansEnvironmentEdge extends EnvironmentEdge {


    public KMeansEnvironmentEdge(int fromId, int toId, long wayID, Set<ModeOfTransport> permittedModes, float allowedMaxSpeedInMpS, int lengthInMetres, int category, int time) {
        super(fromId, toId, wayID, permittedModes, allowedMaxSpeedInMpS, lengthInMetres, category, time);
    }
}
