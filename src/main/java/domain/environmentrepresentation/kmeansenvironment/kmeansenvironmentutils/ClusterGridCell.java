package domain.environmentrepresentation.kmeansenvironment.kmeansenvironmentutils;

import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironmentNode;

import java.util.ArrayList;

public class ClusterGridCell {
    private ArrayList<KMeansEnvironmentNode> nodes = new ArrayList<>();

    public void addNode(KMeansEnvironmentNode node){
        nodes.add(node);
    }


    public ArrayList<KMeansEnvironmentNode> getNodes() {
        return nodes;
    }
}
