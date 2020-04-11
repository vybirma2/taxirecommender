package domain.environmentrepresentation.kmeansenvironment;

import domain.TaxiRecommenderDomainGenerator;
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.kmeansenvironment.kmeans.KMeansAlgorithm;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPlacesDistance;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import visualization.MapVisualizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.Utils.*;

public class KMeansEnvironment extends Environment<KMeansEnvironmentNode, KMeansEnvironmentEdge> {

    private static Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters;

    public KMeansEnvironment() {

    }

    @Override
    protected void setEnvironmentGraph() throws IOException, ClassNotFoundException {
        setClusters();
        /*if (VISUALIZE_ENVIRONMENT){
            visualizeEnvironment();
        }*/

        this.environmentGraph = new KMeansEnvironmentGraph(this.getOsmGraph());


        if (VISUALIZE_ENVIRONMENT){
            visualizeEnvironment();
        }
        //System.out.println("shbfsjf");

    }


    private void visualizeEnvironment(){

        new Thread() {
            @Override
            public void run() {
                MapVisualizer.main(null);
            }
        }.start();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*MapVisualizer.addCentroidsToMap(clusters.keySet());*/

        for (KMeansEnvironmentNode node : this.environmentGraph.getNodes()){
            List<KMeansEnvironmentNode> neighbours = new ArrayList<>();
            MapVisualizer.nodeGraphicsOverlay.getGraphics().removeAll(MapVisualizer.nodeGraphicsOverlay.getGraphics());
            MapVisualizer.addKMeansNodeToMap(node);
            MapVisualizer.addPickUpPointsToMap(node.getPickupPlaceList());
            double distance = 0;
            for (Integer neighbour : node.getNeighbours()){
                distance += ((KMeansEnvironmentGraph)environmentGraph).getDistanceBetweenNodes(node.getNodeId(), neighbour);
                neighbours.add(environmentGraph.getNode(neighbour));
            }
            distance /= 7;
            System.out.println("distance is: " + distance);
            MapVisualizer.addKMeansNodesToMap(neighbours);
            /*List<TaxiTripPickupPlace> hull = ConvexHullFinder.getConvexHull(clusters.get(entry.getKey()));
            MapVisualizer.addHullPointsToMap(hull, entry.getKey());
            MapVisualizer.addPickUpPointsToMap(entry.getValue());*/
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }


    private void setClusters() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + NUM_OF_CLUSTERS + "xKMeansLiftago_prague.fst");

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
        List<TaxiTripPickupPlace> pickupPlaces = TaxiRecommenderDomainGenerator.getTaxiTrips()
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
