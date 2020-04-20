package parameterestimation;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;

public class LongitudeLatitudeReader {
    public LongitudeLatitudeReader() {
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("9a25fc76f3e54b7e8034ce69f2e0d22e");
        JOpenCageForwardRequest request = new JOpenCageForwardRequest("375 Albert Rd, Woodstock, Cape Town, 7915, South Africa");
        request.setRestrictToCountryCode("za"); // restrict results to a specific country
        request.setBounds(18.367, -34.109, 18.770, -33.704); // restrict results to a geographic bounding box (southWestLng, southWestLat, northEastLng, northEastLat)

        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition(); // get the coordinate pair of the first result

    }
}
