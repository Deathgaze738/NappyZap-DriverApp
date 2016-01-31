package com.example.aaron.nappyzap_driver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 30/01/2016.
 */
public class completeDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.completion_dialog_layout, null));

        builder.setMessage("Leave a comment on your pickup?")
                .setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Send the completion request to update the database
                        Log.d("Complete", "Leave a comment on your pickup?");
                        String url = "https://www.nappyzap.com/androidInterface/completePickup.php";
                        EditText t = (EditText) ((AlertDialog) dialog).findViewById(R.id.commentBox);
                        String comments = t.getText().toString();
                        JSONObject params = new JSONObject();
                        try {
                            params.put("pickupID", Integer.toString(mainActivity.currentPickup.pickupID));
                            params.put("comments", comments);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest ret = new JsonObjectRequest
                                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        Log.d("CurrentPickup", "Response Error");
                                        Log.d("CurrentPickup", "onErrorResponse: " + error.getMessage());
                                        mainActivity.currentPickup.complete = true;

                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("User-agent", "My useragent");
                                return headers;
                            }
                        };
                        SingletonRequestQueue.getInstance(getActivity()).addToRequestQueue(ret);
                        Log.d("Complete", "Pickup Completed");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Nothing happens here
                        completeDialog.this.getDialog().cancel();
                    }
                });
        mainActivity.currentPickup = new CurrentPickup();

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
