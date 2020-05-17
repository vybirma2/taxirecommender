package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.kmeansenvironment.kmeans.KMeansAlgorithm;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPlacesDistance;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import domain.parameterestimation.TaxiTrip;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static domain.utils.Utils.*;


/**
 * K-Means environment class responsible for computation of K-Means clusters used to define KMeansEnvironmentGraph
 */
public class KMeansEnvironment extends Environment<KMeansEnvironmentNode, KMeansEnvironmentEdge> {

    public static Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters;

    public KMeansEnvironment(Graph<RoadNode, RoadEdge> osmGraph , List<TaxiTrip> trainingDataSet) {
        super(osmGraph, trainingDataSet);
    }


    @Override
    protected void setEnvironmentGraph() {
        try {
            setClusters();
            this.environmentGraph = new KMeansEnvironmentGraph(this.getOsmGraph());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setClusters() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + NUM_OF_CLUSTERS + "xKMeans" + DATA_SET_NAME);

        if(!file.exists()) {
            computeAndSerializeKMeansClusters(file);
        } else {
            loadKMeansClusters(file);
        }
    }

    private void loadKMeansClusters(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        clusters = (Map<PickUpPointCentroid, List<TaxiTripPickupPlace>>)in.readObject();
        in.close();
    }

    private void computeAndSerializeKMeansClusters(File file) throws IOException {
        List<TaxiTripPickupPlace> pickupPlaces = trainingDataSet
                .stream()
                .map(t -> new TaxiTripPickupPlace(t.getPickUpLongitude(), t.getPickUpLatitude()))
                .collect(Collectors.toList());

        clusters = KMeansAlgorithm.runKMeans(pickupPlaces,
                NUM_OF_CLUSTERS, new PickUpPlacesDistance(), MAX_KMEANS_ITERATIONS);

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(clusters);
        out.close();
    }

    public static Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> getClusters() {
        return clusters;
    }
}
