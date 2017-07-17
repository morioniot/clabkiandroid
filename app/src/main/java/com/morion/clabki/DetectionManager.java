package com.morion.clabki;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by morion on 4/07/17.
 */

public class DetectionManager {

    final private String TAG = "DETECTION_M_DEBUG_FLAG";
    private HashSet<ClabkiBeacon> detectedBeacons = new HashSet<>();
    private Context context;
    private NetworkSingleton netSingleton;

    public DetectionManager(Context context) {
        this.context = context;
        netSingleton = NetworkSingleton.getInstance(context);
    }

    public void push(final ClabkiBeacon beacon) {

        if(detectedBeacons.add(beacon)) {

            String isLostURL = assembleIsLostURL(beacon.getMajor(), beacon.getMinor());
            JsonObjectRequest isLostRequest = new JsonObjectRequest(Request.Method.GET, isLostURL, null,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response){
                    try {
                        final int reportedAsLost = response.getInt("reported_as_lost");
                        if(reportedAsLost == 1) {
                            String macAddress = beacon.getMacAddress();
                            NotificationFactory notificationFactory = new NotificationFactory();
                            Notification detectedBeaconNotification =
                                    notificationFactory.buildBeaconDetectedNotification(context, macAddress);
                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(beacon.hashCode(), detectedBeaconNotification);
                        }
                        else{
                            Log.i(TAG, "The pet has been detected but it is not lost");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "The value did not cast to boolean properly");
                    }

                    Log.i(TAG, "Response: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.i(TAG, "There was an error in the http request");
                }
            });

            netSingleton.addToRequestQueue(isLostRequest);
        }
    }

    private String assembleIsLostURL(int major, int minor) {
        return "http://clabkiapi-morion.rhcloud.com/api/getStatus?major=" + major + "&minor=" + minor;
    }
}
