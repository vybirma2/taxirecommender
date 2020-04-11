package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import domain.environmentrepresentation.EnvironmentEdge;

import java.util.HashSet;
import java.util.Set;

public class KMeansEnvironmentEdge extends EnvironmentEdge {


    public KMeansEnvironmentEdge(int fromId, int toId, float allowedMaxSpeedInMpS, int lengthInMetres, int time) {
        super(fromId, toId, 0, new HashSet<>(), allowedMaxSpeedInMpS, lengthInMetres, 0, time);
    }
}
