package domain.environmentrepresentation;

/**
 * Abstract class of an environment edge representation as a wrapper for concrete environment edge implementations.
 */
public abstract class EnvironmentEdge {

    private final int time;
    private final int fromId;
    private final int toId;
    private final int length;
    private final float speed;

    public EnvironmentEdge(int fromId, int toId, float speed, int length, int time) {
        this.fromId = fromId;
        this.toId = toId;
        this.speed = speed;
        this.length = length;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int getLength() {
        return length;
    }

    public float getSpeed() {
        return speed;
    }
}
