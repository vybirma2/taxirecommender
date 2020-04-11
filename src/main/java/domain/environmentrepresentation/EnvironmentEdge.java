package domain.environmentrepresentation;


public abstract class EnvironmentEdge {

    private int time;
    private int fromId;
    private int toId;
    private int length;
    private float speed;


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


    public int getFromId() {
        return fromId;
    }


    public int getToId() {
        return toId;
    }


    public int getLength() {
        return length;
    }


    public float getSpeed() {
        return speed;
    }
}
