package com.comp30022.helium.strawberry.components.location;

import android.graphics.Color;
import android.util.Log;

import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.comp30022.helium.strawberry.helpers.JSONParser;
import com.comp30022.helium.strawberry.helpers.ParserTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by noxm on 17/09/17.
 */

public class PathParserTask extends ParserTask<String, Integer, List<List<HashMap<String, String>>>> {
    // Parsing the data in non-ui thread
    private StrawberryMap strawberryMap;
    private String pathName;

    public PathParserTask(String pathName, StrawberryMap strawberryMap) {
        this.pathName = pathName;
        this.strawberryMap = strawberryMap;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            JSONParser parser = new JSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);

        } catch (Exception e) {
            Log.d("PathParserTask", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;

        PolylineOptions lineOptions = null;
        // Traversing through all the routes

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            strawberryMap.updatePolyline(pathName, lineOptions);
        } else {
            Log.d("onPostExecute", "without Polylines drawn");
        }
    }
}
