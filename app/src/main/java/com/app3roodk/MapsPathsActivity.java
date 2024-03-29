package com.app3roodk;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MapsPathsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapsPathsActivity";
    private GoogleMap map;
    private Polyline polyline;
    private LatLng fromPlace, toPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    private void initMAPS() {
        Marker marker1= map.addMarker(new MarkerOptions().position(toPlace).title("Shop").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_black_24dp)));
        Marker marker2= map.addMarker(new MarkerOptions().position(fromPlace).title("You are here"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 250; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

    //region Get Directions
    private void fetchDirections(LatLng start, LatLng end) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";
        startFetching(url);
    }

    private void startFetching(String url) {
        if (!UtilityGeneral.isOnline(getBaseContext())) {
            Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getBaseContext(), "Couldn't reach to the server, please check your internet connection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    JSONObject routes = json.getJSONArray("routes").getJSONObject(0);
                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    String encodedString = overviewPolylines.getString("points");
                    ArrayList<LatLng> list = decodePoly(encodedString);
                    if (polyline != null)
                        polyline.remove();
                    polyline = map.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(14)
                            .color(Color.parseColor("#05b1fb"))
                            .geodesic(true)
                    );
                } catch (JSONException e) {
//                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            fromPlace = new LatLng(getIntent().getDoubleExtra("fromLat", 31.13), getIntent().getDoubleExtra("fromLng", 31.13));
            toPlace = new LatLng(getIntent().getDoubleExtra("toLat", 31.13), getIntent().getDoubleExtra("toLng", 31.13));
            initMAPS();
            fetchDirections(fromPlace, toPlace);
        }
    }
    //endregion

}

