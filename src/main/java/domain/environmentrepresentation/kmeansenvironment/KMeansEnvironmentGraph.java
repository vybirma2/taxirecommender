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
        edges = new HashMap<>();

        for (Map.Entry<Integer, KMeansEnvironmentNode> entry : nodes.entrySet()){
            KMeansEnvironmentNode node = entry.getValue();
            HashMap<Integer, KMeansEnvironmentEdge> nodeEdges = new HashMap<>();

            for (Integer neighbour : nodes.get(entry.getKey()).getNeighbours()){

                if (!entry.getKey().equals(neighbour)){
                    DistanceSpeedPairTime distanceSpeedPairTime = getDistanceSpeedTimeBetweenNodes(entry.getKey(), neighbour);
                    double distance = distanceSpeedPairTime.getDistance();
                    float speed = (float) (distanceSpeedPairTime.getSpeed());
                    nodeEdges.put(neighbour, new KMeansEnvironmentEdge(entry.getKey(), neighbour, speed,
                            (int)(distance * 1000), DistanceGraphUtils.getTripTime(distance, speed)));
                }
            }
            edges.put(node.getNodeId(), nodeEdges);
        }
    }


    private void createNodes() {
        this.nodes = new HashMap<>();
        for (PickUpPointCentroid centroid : clusters.keySet()) {
            RoadNode centroidNode = DistanceGraphUtils.chooseRoadNode(centroid.getLongitude(), centroid.getLatitude());
            this.nodes.put(centroidNode.getId(), new KMeansEnvironmentNode(centroidNode.getId(), new HashSet<>(), centroid, clusters.get(centroid)));
        }
    }


    private void setDistances() throws IOException, ClassNotFoundException {
        String name = "data/programdata/" + Utils.NUM_OF_CLUSTERS + "KMeansCentroidDistances.fst";
        File file = new File(name);

        if(!file.exists()) {
            computeAndSerializeDistances(file);
        } else {
            loadDistances(file);
        }

    }


    public double getDistanceBetweenNodes(int fromNodeId, int toNodeId){
        return distanceSpeedTime.get(fromNodeId).stream().filter(t-> t.getToNodeId() == toNodeId).findFirst().get().getDistanceSpeedPairTime().getDistance();
    }


    public DistanceSpeedPairTime getDistanceSpeedTimeBetweenNodes(int fromNodeId, int toNodeId){
        return distanceSpeedTime.get(fromNodeId).stream().filter(t-> t.getToNodeId() == toNodeId).findFirst().get().getDistanceSpeedPairTime();
    }


    private void computeAndSerializeDistances(File file) throws IOException {
        distanceSpeedTime = new HashMap<>();

        for (KMeansEnvironmentNode fromNode : this.nodes.values()){
            for (KMeansEnvironmentNode toNode : this.nodes.values()){
                if (distanceSpeedTime.containsKey(fromNode.getNodeId())){
                    DistanceSpeedPairTime distance = DistanceGraphUtils.getDistancesAndSpeedBetweenNodes(fromNode.getNodeId(), toNode.getNodeId());
                    distanceSpeedTime.get(fromNode.getNodeId()).add(new TripToNode(toNode.getNodeId(), distance));
                } else {
                    List<TripToNode> fromNodeDistances = new ArrayList<>();
                    DistanceSpeedPairTime distance = DistanceGraphUtils.getDistancesAndSpeedBetweenNodes(fromNode.getNodeId(), toNode.getNodeId());
                    fromNodeDistances.add(new TripToNode(toNode.getNodeId(), distance));
                    distanceSpeedTime.put(fromNode.getNodeId(), fromNodeDistances);
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
            Set<Integer> neighbours = distanceSpeedTime.get(node.getNodeId())
                    .stream()
                    .filter(t -> t.getToNodeId() != node.getNodeId())
                    .sorted()
                    .limit(Utils.NUM_OF_NEIGHBOURS)
                    .map(TripToNode::getToNodeId)
                    .collect(Collectors.toSet());

            node.addNeighbours(neighbours);
        }
    }
}
