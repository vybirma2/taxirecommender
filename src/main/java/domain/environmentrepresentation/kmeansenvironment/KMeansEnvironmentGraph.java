package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TripToNode;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import utils.DistanceGraphUtils;
import utils.DistanceSpeedPairTime;
import utils.Utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static utils.Utils.NUM_OF_CLUSTERS;


public class KMeansEnvironmentGraph extends EnvironmentGraph<KMeansEnvironmentNode, KMeansEnvironmentEdge> {

    private Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters;
    private Map<Integer, List<TripToNode>> distanceSpeedTime;

    public KMeansEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) throws IOException, ClassNotFoundException {
        super(osmGraph);
    }

    @Override
    protected void setNodes() throws IOException, ClassNotFoundException {
        this.clusters = KMeansEnvironment.getClusters();
        createNodes();
        setDistances();
        setNodeNeighbours();


    }

    @Override
    protected void setEdges() {

    }

    private void createNodes(){
        this.nodes = new HashMap<>();
        for (PickUpPointCentroid centroid : clusters.keySet()) {
            RoadNode centroidNode = DistanceGraphUtils.chooseRoadNode(centroid.getLongitude(), centroid.getLatitude());
            this.nodes.put(centroidNode.getId(), new KMeansEnvironmentNode(centroidNode.id, centroidNode.sourceId,
                    new GPSLocation(centroidNode.latE6, centroidNode.lonE6, centroidNode.latProjected,
                            centroidNode.lonProjected, centroidNode.elevation), new HashSet<>()));
        }
    }


    private void setDistances() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + NUM_OF_CLUSTERS + "KMeansCentroidDistances.fst");

        if(!file.exists()) {
            computeAndSerializeDistances(file);
        } else {
            loadDistances(file);
        }

    }


    private void computeAndSerializeDistances(File file) throws IOException {
        distanceSpeedTime = new HashMap<>();

        for (KMeansEnvironmentNode fromNode : this.nodes.values()){
            for (KMeansEnvironmentNode toNode : this.nodes.values()){
                if (distanceSpeedTime.containsKey(fromNode.getId())){
                    DistanceSpeedPairTime distance = DistanceGraphUtils.getDistancesAndSpeedBetweenNodes(fromNode.getId(), toNode.getId());
                    distanceSpeedTime.get(fromNode.getId()).add(new TripToNode(toNode.getId(), distance));
                } else {
                    List<TripToNode> fromNodeDistances = new ArrayList<>();
                    DistanceSpeedPairTime distance = DistanceGraphUtils.getDistancesAndSpeedBetweenNodes(fromNode.getId(), toNode.getId());
                    fromNodeDistances.add(new TripToNode(toNode.getId(), distance));
                    distanceSpeedTime.put(fromNode.getId(), fromNodeDistances);
                }
            }
        }
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(distanceSpeedTime);
        out.close();
    }


    private void loadDistances(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        distanceSpeedTime = (Map<Integer, List<TripToNode>>)in.readObject();
        in.close();
    }


    private void setNodeNeighbours(){
        for (KMeansEnvironmentNode node : nodes.values()){
            Set<Integer> neighbours = distanceSpeedTime.get(node.getId())
                    .stream()
                    .sorted()
                    .limit(Utils.NUM_OF_NEIGHBOURS)
                    .map(TripToNode::getToNodeId)
                    .collect(Collectors.toSet());

            node.addNeighbours(neighbours);
        }
    }
}
