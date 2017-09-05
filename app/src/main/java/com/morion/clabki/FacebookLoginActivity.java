package com.morion.clabki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLoginActivity extends AppCompatActivity {

    private TextView loginInfo;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        callbackManager = CallbackManager.Factory.create();
        initializeControls();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest facebookRequest = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                
                                String id, name, email, gender, birthday;
                                id = name = email = gender = birthday = "no disponible";
                                
                                try {
                                    if(object.has("id"))
                                        id = object.getString("id");
                                    if(object.has("name"))
                                        name = object.getString("name");
                                    if(object.has("email"))
                                        email = object.getString("email");
                                    if(object.has("gender"))
                                        gender = object.getString("gender");
                                    if(object.has("birthday"))
                                        birthday = object.getString("birthday");
                                }
                                catch (JSONException exception) {
                                    exception.printStackTrace();
                                }
                                
                                
                                loginInfo.setText("Bienvenido, " + name + "\n"
                                + "ID: " + id + " '\n"
                                + "Email: " + email + " 'n"
                                + "Género: " + gender + "\n"
                                + "Cumpleaños: " + birthday);
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                facebookRequest.setParameters(parameters);
                facebookRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                loginInfo.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                loginInfo.setText("Login Error: " + error.getMessage());
            }
        });
    }

    private void initializeControls() {
        loginInfo = (TextView) findViewById(R.id.login_info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
