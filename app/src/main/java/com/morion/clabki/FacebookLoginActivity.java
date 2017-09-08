package com.morion.clabki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {

    private TextView loginInfo;
    private Button loginButton;
    private CallbackManager callbackManager;
    private LoginManager loginManager = LoginManager.getInstance();
    private ProfileTracker profileTracker;

    private final String TAG = "FACEBOOK_LOGIN_DEBUG";

    //****************************************************************************************
    //******************************* Activity Life Methods **********************************
    //****************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        initializeControls();
        callbackManager = CallbackManager.Factory.create();
        setControlsAccordingToLoginState();
    }

    //****************************************************************************************
    //******************************* Other Methods ******************************************
    //****************************************************************************************

    //Checks if user is login in Facebook
    private boolean isUserLogin() {
        return !(AccessToken.getCurrentAccessToken() == null);
    }


    private void setProfileTracker() {

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                setControlsAccordingToLoginState();
                stopTracking();
                final AccessToken accessToken = AccessToken.getCurrentAccessToken();

                //Request to obtain data from user
                GraphRequest facebookRequest = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    Log.i(TAG, "Facebook id: " + object.toString(4));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.i(TAG, "Facebook token: " + accessToken.getToken());
                                Log.i(TAG, "Firebase token: " + FirebaseInstanceId.getInstance().getToken());
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                facebookRequest.setParameters(parameters);
                facebookRequest.executeAsync();
            }
        };
    }


    //****************************************************************************************
    //*************************Methods related with UI controls*******************************
    //****************************************************************************************

    //Get instances for the different UI controls
    private void initializeControls() {
        loginInfo = (TextView) findViewById(R.id.login_info);
        loginButton = (Button) findViewById(R.id.login_button);
    }

    //Sets the UI elements depending on login state
    private void setControlsAccordingToLoginState() {
        //Checks if user is logged to display appropriate button text and welcome message
        if(isUserLogin()) {
            Profile profile = Profile.getCurrentProfile();
            loginInfo.setText("Bienvenido, " + profile.getFirstName());
            loginButton.setText("SALIR");
        }
        else {
            loginInfo.setText("");
            loginButton.setText("CONTINUAR CON FACEBOOK");
        }
    }

    //Handles the login button
    public void handleLoginButton(View view) {

        if(isUserLogin()) {
            loginManager.logOut();
            setControlsAccordingToLoginState();
        }
        else {
            setProfileTracker();
            profileTracker.startTracking();
            loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
        }

    }


    //****************************************************************************************
    //*************************Callbacks used in this activity********************************
    //****************************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
