package domain.environmentrepresentation.fullenvironment;

import cz.agents.multimodalstructures.additional.ModeOfTransport;
import domain.environmentrepresentation.EnvironmentEdge;

import java.util.Set;

public class FullEnvironmentEdge extends EnvironmentEdge {


    public FullEnvironmentEdge(int fromId, int toId, float speed, int length, int time) {
        super(fromId, toId, speed, length, time);
    }
}
