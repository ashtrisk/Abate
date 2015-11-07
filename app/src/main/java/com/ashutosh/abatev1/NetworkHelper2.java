package com.ashutosh.abatev1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vostro-Daily on 11/5/2015.
 */
// This class helps to connect to the remote server and send user sign in data to the server
public class NetworkHelper2 extends AsyncTask<Void, Void, HashMap<String, String>>{

    private String LOG_TAG = NetworkHelper.class.toString();
    private String requestURL;
    private HashMap<String, String> postDataParams;
    private Context ctx;
    public static final String USER_ID = "id";
    public static final String USER_EMAIL = "email";

    public NetworkHelper2(HashMap<String, String> params, Context ctx) {
        requestURL = "http://abate-csmadhav.rhcloud.com/useradd";
        postDataParams = params;
        this.ctx = ctx;
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
        // check for internet connection status, create a connection, send user data to server, receive json and parse it to get userID.
        // make http request, read data from input stream, clean up and log any errors.
        if(!((SignInActivity)ctx).isNetworkAvailable()){            // may produce an error some day
            return null;        // do nothing
        }

        String userJsonStr = performPostCall(requestURL, postDataParams);

        HashMap<String, String> userData = new HashMap<>();
        try {

            userData = getUserDataFromJson(userJsonStr);        // parses json and returns user data

        }catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }
        return userData;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> userData) {
        super.onPostExecute(userData);
        String id = userData.get("id");
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");
        Toast.makeText(ctx, "DATA USER : "+id+"\n"+username+"\n"+email+"\n"+password, Toast.LENGTH_LONG).show();
        // Storing user id to default shared preferences
        SharedPreferences pref = ((SignInActivity)ctx).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ID, id);
        editor.putString(USER_EMAIL, email);
        editor.commit();
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();     // The content of the writer are committed to the target and then flushed. (first connect, commit, flush)
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private HashMap<String, String> getUserDataFromJson(String userJsonStr) throws JSONException {

        final String ID = "id";
        final String USERNAME = "username";
        final String EMAIL = "email";
        final String PASSWORD = "password";

        JSONObject jsoObj = new JSONObject(userJsonStr);
        String id = jsoObj.getString(ID);
        String username = jsoObj.getString(USERNAME);
        String email = jsoObj.getString(EMAIL);
        String password = jsoObj.getString(PASSWORD);

        HashMap<String, String> userData = new HashMap<>();
        userData.put(ID, id);
        userData.put(USERNAME, username);
        userData.put(EMAIL, email);
        userData.put(PASSWORD, password);

        return userData;
    }
}
