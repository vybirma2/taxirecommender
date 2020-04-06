package domain;

public class TaxiGraphTransition {

    private int fromNodeId;
    private int toNodeId;


    public TaxiGraphTransition(int fromNodeId, int toNodeId) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
    }

    public int getFromNodeId() {
        return fromNodeId;
    }


    public int getToNodeId() {
        return toNodeId;
    }
}
