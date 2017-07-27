package com.morion.clabki;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by morion on 4/07/17.
 */

public class DetectionManager {

    final private String TAG = "DETECTION_M_DEBUG_FLAG";
    private HashSet<ClabkiBeacon> detectedBeacons = new HashSet<>();
    private Context context; //This is the application context
    private NetworkSingleton netSingleton;
    private FusedLocationProviderClient fusedLocationClient;

    public DetectionManager(Context context) {
        this.context = context;
        netSingleton = NetworkSingleton.getInstance(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void push(final ClabkiBeacon beacon) {

        if(detectedBeacons.add(beacon)) {

            //Send notification
            String macAddress = beacon.getMacAddress();
            NotificationFactory notificationFactory = new NotificationFactory();
            Notification detectedBeaconNotification =
                    notificationFactory.buildBeaconDetectedNotification(context, macAddress);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(beacon.hashCode(), detectedBeaconNotification);

            final int major = beacon.getMajor();
            final int minor = beacon.getMinor();
            String isLostURL = assembleIsLostURL(major, minor);
            JsonObjectRequest isLostRequest = new JsonObjectRequest(Request.Method.GET, isLostURL, null,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response){
                    try {
                        final int reportedAsLost = response.getInt("reported_as_lost");
                        if(reportedAsLost == 1) {


                            //Send notification
                            String macAddress = beacon.getMacAddress();
                            NotificationFactory notificationFactory = new NotificationFactory();
                            Notification detectedBeaconNotification =
                                    notificationFactory.buildBeaconDetectedNotification(context, macAddress);
                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(beacon.hashCode(), detectedBeaconNotification);



                            //Get location and save it in the database
                            //This is the listener called when a valid location is delivered
                            OnSuccessListener<Location> onSuccessListener = new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null) {

                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();

                                        String setLocationURL =
                                                assembleSetLocationURL(major, minor, latitude, longitude);

                                        JsonObjectRequest setLocationRequest = new JsonObjectRequest(Request.Method.GET,
                                                setLocationURL, null, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                final boolean hasError = response.has("error");
                                                if(!hasError) {
                                                    Log.i(TAG, "The location has been saved");
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.i(TAG, "There was an error in the location request");
                                            }
                                        });

                                        netSingleton.addToRequestQueue(setLocationRequest);
                                    }
                                }
                            };

                            //API call to get the last known location of the user
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED)
                                fusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener);
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

    private String assembleSetLocationURL(int major, int minor, double latitude, double longitude) {
        String latitudeStr = Location.convert(latitude, Location.FORMAT_DEGREES);
        String longitudeStr = Location.convert(longitude, Location.FORMAT_DEGREES);
        return "http://clabkiapi-morion.rhcloud.com/api/addLocationToPet?major=" + major
                + "&minor=" + minor + "&lat=" + latitudeStr + "&lon=" + longitudeStr;
    }
}
