package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import de.alsclo.voronoi.Voronoi;
import de.alsclo.voronoi.graph.Edge;
import de.alsclo.voronoi.graph.Point;
import domain.charging.ChargingStationReader;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TripToNode;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import domain.utils.DistanceGraphUtils;
import domain.utils.DistanceSpeedPairTime;
import domain.utils.Utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * K-Means environment graph implementation responsible for node and edge setting
 * concerning the formal description from the thesis
 */
public class KMeansEnvironmentGraph extends EnvironmentGraph<KMeansEnvironmentNode, KMeansEnvironmentEdge> {

    private Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters;
    private Map<Integer, List<TripToNode>> distanceSpeedTime;

    public KMeansEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) throws IOException, ClassNotFoundException {
        super(osmGraph);
    }

    @Override
    protected void setNodes() {
        this.clusters = KMeansEnvironment.getClusters();
        createNodes();
        DistanceGraphUtils.setNodes(getNodes());
        DistanceGraphUtils.setGraph(this);
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
        ArrayList<RoadNode> deletedNodes = new ArrayList<>();
        HashSet<RoadNode> remainingNodes = new HashSet<>(osmGraph.getAllNodes());
        for (PickUpPointCentroid centroid : clusters.keySet()) {
            RoadNode centroidNode = DistanceGraphUtils.chooseRoadNode(remainingNodes, centroid.getLongitude(), centroid.getLatitude());
            while (nodes.containsKey(centroidNode.getId()) || ChargingStationReader.getChargingStation(centroidNode.getId()) != null){
                deletedNodes.add(centroidNode);
                remainingNodes.remove(centroidNode);
                centroidNode = DistanceGraphUtils.chooseRoadNode(remainingNodes, centroid.getLongitude(), centroid.getLatitude());
            }
            this.nodes.put(centroidNode.getId(), new KMeansEnvironmentNode(centroidNode, new HashSet<>(), centroid, clusters.get(centroid)));
            remainingNodes.addAll(deletedNodes);
            deletedNodes.clear();
        }
    }

    private void setDistances() {
        String name = "data/programdata/" + Utils.NUM_OF_CLUSTERS +"KMeansCentroidDistances" + Utils.DATA_SET_NAME;
        File file = new File(name);

        if(!file.exists()) {
            computeAndSerializeDistances(file);
        } else {
            loadDistances(file);
        }
    }

    public DistanceSpeedPairTime getDistanceSpeedTimeBetweenNodes(int fromNodeId, int toNodeId){
        return distanceSpeedTime.get(fromNodeId).stream().filter(t-> t.getToNodeId() == toNodeId).findFirst().get().getDistanceSpeedPairTime();
    }

    private void computeAndSerializeDistances(File file) {
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

        FSTObjectOutput out;
        try {
            out = new FSTObjectOutput(new FileOutputStream(file));
            out.writeObject(distanceSpeedTime);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadDistances(File file) {
        FSTObjectInput in;
        try {
            in = new FSTObjectInput(new FileInputStream(file));
            distanceSpeedTime = (Map<Integer, List<TripToNode>>)in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setNodeNeighbours() {
        Collection<Point> points = new ArrayList<>();
        for (KMeansEnvironmentNode node : getNodes()){
            points.add(new Point(node.getLongitude(), node.getLatitude()));
        }

        Voronoi voronoi = new Voronoi(points);
        de.alsclo.voronoi.graph.Graph graph = voronoi.getGraph();

        List<Edge> collect = graph.edgeStream().collect(Collectors.toList());

        for (Edge edge : collect){
            EnvironmentNode fromNode = DistanceGraphUtils.chooseEnvironmentNode(edge.getSite1().x, edge.getSite1().y);
            EnvironmentNode toNode = DistanceGraphUtils.chooseEnvironmentNode(edge.getSite2().x, edge.getSite2().y);
            fromNode.addNeighbour(toNode.getNodeId());
            toNode.addNeighbour(fromNode.getNodeId());
        }
    }
}
