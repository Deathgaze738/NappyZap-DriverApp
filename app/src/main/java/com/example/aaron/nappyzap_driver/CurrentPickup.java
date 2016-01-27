package com.example.aaron.nappyzap_driver;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Aaron on 12/12/2015.
 */
public class CurrentPickup implements Serializable {
    private String url = "https://www.nappyzap.com/androidInterface/getCurrentJob.php";
    private Map<String, String> params;
    static String name;
    static String phoneNo;
    static LatLng pickupLoc;
    static String binLocation;
    static String address;
    static String details;
    static int sizeOfPickup;
    static int pickupID;
    static boolean complete;
    private JsonObjectRequest jsObjRequest;
    public final static Lock lock = new ReentrantLock();
    public final static Condition mapReady = lock.newCondition();

    public CurrentPickup(){
        complete = true;
        name = "No Current Pickup";
        phoneNo = "";
        pickupLoc = new LatLng(0, 0);
        address = "Please request a new pickup!";
        binLocation = "";
        details = "";
        sizeOfPickup = 0;
    }

    public CurrentPickup(final int driverID, final Context ctx) {
        lock.lock();
        JSONObject params = new JSONObject();
        try {
            params.put("driverID", Integer.toString(driverID));
            params.put("lat", Double.toString(mainActivity.gpsChecker.lat));
            params.put("lng", Double.toString(mainActivity.gpsChecker.lng));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("CurrentPickup", "Request Prepared");
        jsObjRequest = getJsonObject(ctx, params);
        Log.d("CurrentPickup", "Response Sent");
        SingletonRequestQueue.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public synchronized JsonObjectRequest getJsonObject(final Context ctx, JSONObject params){
        JsonObjectRequest ret = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

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
                            complete = false;
                            Log.d("CurrentPickup", "Response: " + response.toString());
                            Log.d("CurrentPickup", "Object Data: " + getString());
                            save(ctx);
                            Log.d("MapReady", "Signalling MapReady");
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
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };
        lock.unlock();
        return ret;
    }

    public synchronized void updateMap(GoogleMap mMap) throws InterruptedException {
        lock.lock();
        Log.d("MapReady", "Waiting for Signal");
        LatLngBounds currentScope = new LatLngBounds(pickupLoc, new LatLng(mainActivity.gpsChecker.lat, mainActivity.gpsChecker.lng));
        Log.d("UpdateMap", pickupLoc.toString());
        Log.d("UpdateMap", Double.toString(mainActivity.gpsChecker.lat));
        Log.d("UpdateMap", Double.toString(mainActivity.gpsChecker.lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(currentScope, 200));
        mMap.addMarker(new MarkerOptions()
                .position(pickupLoc)
                .title(name)
                .snippet(address));
        lock.unlock();
    }

    //Serialise Object
    public void save(Context ctx){
        try {
            Log.d("Save", "Saving Current Pickup...");
            FileOutputStream fos = ctx.openFileOutput("currentPickup.data" , Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            Log.d("Save", "Save Complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Load object
    public static void load(Context ctx){
        try {
            Log.d("Load", "Loading Current Pickup...");
            FileInputStream fis = ctx.openFileInput("currentPickup.data");
            ObjectInputStream is = new ObjectInputStream(fis);
            mainActivity.currentPickup = (CurrentPickup) is.readObject();
            is.close();
            fis.close();
            Log.d("Load", "Load Complete");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(){
        return name + " " + address + " " + complete;
    }
}
