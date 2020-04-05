package domain.environmentrepresentation.gridenvironment;

import domain.environmentrepresentation.EnvironmentEdge;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Serializable;
import java.util.HashSet;

public class GridEnvironmentEdge extends EnvironmentEdge implements Serializable {


    public GridEnvironmentEdge(int fromId, int toId, float allowedMaxSpeedInMpS, int lengthInMetres, int time) {
        super(fromId, toId, 0, new HashSet<>(), allowedMaxSpeedInMpS, lengthInMetres, 0, time);
    }
}
