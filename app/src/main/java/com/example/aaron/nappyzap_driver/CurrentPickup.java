package com.example.aaron.nappyzap_driver;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Aaron on 12/12/2015.
 */
public class CurrentPickup {
    private String url = "https://www.nappyzap.com/androidInterface/getCurrentJob.php";
    private HashMap<String, String> params = new HashMap<String, String>();
    static String name;
    static String phoneNo;
    static LatLng pickupLoc;
    static String binLocation;
    static String address;
    static String details;
    static int sizeOfPickup;
    static int pickupID;
    private JsonObjectRequest jsObjRequest;

    public CurrentPickup(final GPSChecker fCheck, int driverID, final Context ctx, final GoogleMap mMap) {
        params.put("driverID", Integer.toString(driverID));
        params.put("lat", Double.toString(fCheck.lat));
        params.put("lng", Double.toString(fCheck.lng));
        Log.d("CurrentPickup", "Response Prepared");
        jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("CurrentPickup", "Response Received");
                            name = response.getString("title")+" "+response.getString("firstName")+" "+response.getString("lastName");
                            phoneNo = response.getString("phoneNo");
                            pickupLoc = new LatLng(response.getDouble("lat"), response.getDouble("lng"));
                            binLocation = response.getString("binLocation");
                            address = response.getString("houseNo")+", "+response.getString("street")+", "+response.getString("city")+", "+response.getString("county")+", "+response.getString("postcode");
                            pickupID = response.getInt("pickupID");
                            details = response.getString("details");
                            sizeOfPickup = response.getInt("sizeOfPickup");
                            Log.d("CurrentPickup", "Response: " + response.toString());
                            LatLngBounds currentScope = new LatLngBounds(pickupLoc, new LatLng(fCheck.lat, fCheck.lng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(currentScope, 200));
                            mMap.addMarker(new MarkerOptions()
                                    .position(pickupLoc)
                                    .title(name)
                                    .snippet(address));
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("CurrentPickup", "Response Error");
                        Log.d("CurrentPickup", "onErrorResponse: " + error.getMessage());
                    }
                });
        Log.d("CurrentPickup", "Response Sent");
        SingletonRequestQueue.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsObjRequest);
    }
}
