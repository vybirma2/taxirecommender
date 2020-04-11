package visualization;

import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPoint;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class MapVisualizer extends Application {

    private MapView mapView;
    public static GraphicsOverlay nodeGraphicsOverlay;

    private static SimpleMarkerSymbol centroidSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 8.0f);


    private static SimpleMarkerSymbol pickUpPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF00FF00, 2.0f);


    private static SimpleMarkerSymbol hullPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF800080, 8.0f);

    private static SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3);



    public static void main(String[] args) {
        centroidSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 8.0f));

        Application.launch();
    }

    @Override
    public void start(Stage stage) {

        try {
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            stage.setTitle("Map Visualizer");
            stage.setMaximized(true);
            stage.setScene(scene);

            setMapView();

            stackPane.getChildren().addAll(mapView);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addPickUpPointsToMap(List<TaxiTripPickupPlace> allNodes){
        //nodeGraphicsOverlay.getGraphics().removeAll(nodeGraphicsOverlay.getGraphics());
        for (TaxiTripPickupPlace node: allNodes) {
            addPickUpPointToMap(node);
        }
    }


    public static void addPickUpPointToMap(TaxiTripPickupPlace node){
        nodeGraphicsOverlay.getGraphics().add(createGraphicPickupPoint(node));
    }


    private static Graphic createGraphicPickupPoint(PickUpPoint node) {
        Point point = new Point(node.getLongitude(), node.getLatitude(), SpatialReferences.getWgs84());
        Graphic pointGraphic = new Graphic(point, pickUpPointSymbol);

        return pointGraphic;
    }


    public static void addHullPointsToMap(List<TaxiTripPickupPlace> allNodes, PickUpPointCentroid centroid){
        //nodeGraphicsOverlay.getGraphics().removeAll(nodeGraphicsOverlay.getGraphics());
        if (allNodes != null){
            for (TaxiTripPickupPlace node: allNodes) {
                addHullPointToMap(node);
                addHullCentroidConnectionPointToMap(node, centroid);
            }
        }
    }


    public static void addHullPointToMap(TaxiTripPickupPlace node){
        nodeGraphicsOverlay.getGraphics().add(createGraphicHullPoint(node));
    }


    public static void addHullCentroidConnectionPointToMap(TaxiTripPickupPlace node, PickUpPointCentroid centroid){

        PointCollection points = new PointCollection( SpatialReferences.getWgs84());
        points.add(node.getLongitude(), node.getLatitude());
        points.add(centroid.getLongitude(), centroid.getLatitude());
        Polyline line = new Polyline(points);

        nodeGraphicsOverlay.getGraphics().add(new Graphic(line, lineSymbol));
    }


    private static Graphic createGraphicHullPoint(PickUpPoint node) {
        Point point = new Point(node.getLongitude(), node.getLatitude(), SpatialReferences.getWgs84());
        Graphic pointGraphic = new Graphic(point, hullPointSymbol);

        return pointGraphic;
    }



    public static void addCentroidsToMap(Set<PickUpPointCentroid> allNodes){
        nodeGraphicsOverlay.getGraphics().removeAll(nodeGraphicsOverlay.getGraphics());
        for (PickUpPointCentroid node: allNodes) {
            addCentroidToMap(node);
        }
    }


    public static void addKMeansNodesToMap(List<KMeansEnvironmentNode> allNodes){
        //nodeGraphicsOverlay.getGraphics().removeAll(nodeGraphicsOverlay.getGraphics());
        for (KMeansEnvironmentNode node: allNodes) {
            addKMeansNodeToMap(node);
        }
    }


    public static void addCentroidToMap(PickUpPointCentroid node){
        nodeGraphicsOverlay.getGraphics().add(createGraphicCentroid(node));
    }

    public static void addKMeansNodeToMap(KMeansEnvironmentNode node){
        nodeGraphicsOverlay.getGraphics().add(createGraphicKMeansNode(node));
    }


    private static Graphic createGraphicKMeansNode(KMeansEnvironmentNode node) {
        Point point = new Point(node.getLongitude(), node.getLatitude(), SpatialReferences.getWgs84());
        Graphic pointGraphic = new Graphic(point, centroidSymbol);

        return pointGraphic;
    }


    private static Graphic createGraphicCentroid(PickUpPoint node) {
        Point point = new Point(node.getLongitude(), node.getLatitude(), SpatialReferences.getWgs84());
        Graphic pointGraphic = new Graphic(point, centroidSymbol);

        return pointGraphic;
    }



    private void setMapView() {

        nodeGraphicsOverlay = new GraphicsOverlay();

        // creating map of some type and starting ViewPoint defined by lon//lat and level of detail
        ArcGISMap map = new ArcGISMap(Basemap.Type.NAVIGATION_VECTOR, 48.464044, 12.634277, 9);
        mapView = new MapView();
        mapView.setMap(map);

        mapView.getGraphicsOverlays().add(nodeGraphicsOverlay);
    }


    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }

}
