package edu.wmich.lab4mtroyer4551;

/*
*************************************
* Programmer: Moses Troyer
* Class ID: mtroyer4551
* Lab 4
* CIS 4700: Mobile Commerce Development
* Spring 2016
* Due date: 4/10/16
* Date completed: 4/13/16
*************************************
* This class handles the async task
* and connecting to the twitter api.
* Does some minimal formatting of
* data, but mostly recieves the users
* tweets.
*************************************
*/

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchItemsTask extends AsyncTask<String,Void,Void> {

    private UserTimelineFragment userTimelineFragment;

    private static final String TAG = "Twitter";
    private static final String BASIC_API_KEY = "XXX";
    private static final String SECRET_API_KEY = "XXX";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";

    final String GET_USERS = "GET_USERS";
    final String GET_USER_TIMELINE = "GET_USER_TIMELINE";

    String action;
    String parameter;

    List tweets;

    //create new object with usetimeline fragment object
    public FetchItemsTask(UserTimelineFragment userTimelineFragment){
        this.userTimelineFragment = userTimelineFragment;
    }

    @Override //do specific task in background. Called on .execute
    protected Void doInBackground(String... params) {
        action = params[0];
        parameter = params[1];

        //do a different action depending on the parameter passed
        switch(action){
            case GET_USERS:
                getUsers(parameter);
                break;
            case GET_USER_TIMELINE:
                tweets = constructTweets(getUserTimeline(parameter));
                break;
            default:
                break;
        }

        return null;
    }

    @Override
    //run after the background task is finished
    protected void onPostExecute(Void result){

        switch(action){
            case GET_USERS:
                break;
            case GET_USER_TIMELINE: //if the call came from the fragment, update the UI
                if(userTimelineFragment != null){
                    userTimelineFragment.updateUI(tweets);
                }
                break;
            default:
                break;
        }

    }

    //get a json array of all of the specified users tweets
    public JSONArray getUserTimeline(String screen_name){
        try {
            //build the api string
            String urlString = Uri.parse("https://api.twitter.com/1.1/statuses/user_timeline.json")
                    .buildUpon()
                    .appendQueryParameter("screen_name", screen_name)
                    .build().toString();

            //get a new connection from the server
            HttpURLConnection connection = buildConnection(urlString, getAccessToken());

            //receive the data
            String jsonString = new String(getBytes(connection));

            Log.i(TAG, "Received JSON: " + jsonString);

            //turn into an object
            JSONArray json = new JSONArray(jsonString);

            return json;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch user timeline", e);
        }
        return null;
    }

    //search for users with a search query. Requires a log in, so not used
    private void getUsers(String search){
        try {
            //build the api url
            String urlString = Uri.parse("https://api.twitter.com/1.1/users/search.json")
                    .buildUpon()
                    .appendQueryParameter("q", search)
                    .build().toString();

            //get a new connection
            HttpURLConnection connection = buildConnection(urlString, getAccessToken());

            //receive the data
            String jsonString = new String(getBytes(connection));

            Log.i(TAG, "Received JSON: " + jsonString);

            JSONArray json = new JSONArray(jsonString);


        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch users", e);
        }
    }

    //with a json array of data, make a list of tweet objects
    public List constructTweets(JSONArray json){
        List<Tweet> tweets = new ArrayList<>();

        try {
            for(int i = 0; i < json.length(); i++){
                //for every tweet, make a new tweet and add it to the list
                tweets.add(new Tweet(json.getJSONObject(i)));
            }
        } catch (Exception e){

        }

        return tweets;
    }

    //get the json data from twitter
    private byte[] getBytes(HttpURLConnection connection) throws IOException {

        try {
            connection.connect();

            InputStream in;

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //attempt to get the stream
            try {
                in = connection.getInputStream();
            } catch (FileNotFoundException e){
                in = connection.getErrorStream();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                //while there's data, write to the ouput
                out.write(buffer, 0, bytesRead);
            }

            out.close();

            //return the output as bytes
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    //Every call of the rest API requires an access token. This gets that token.
    private String getAccessToken(){
        try {
            String urlString = Uri.parse("https://api.twitter.com/oauth2/token")
                    .buildUpon()
                    .appendQueryParameter("grant_type", "client_credentials")
                    .build().toString();

            HttpURLConnection connection = buildConnection(urlString);

            //set the properties of the rest api call
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Authorization", "Basic " + create_authorization());
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);

            //get the api acess token
            String jsonString = new String(getBytes(connection));

            Log.i(TAG, "Received JSON: " + jsonString);

            JSONObject json = new JSONObject(jsonString);

            return json.getString("access_token");
        } catch (Exception e){
            Log.e(TAG, "Unable to fetch Access Token!");
        }

        return null;
    }

    //build a new connection using the specified url
    private HttpURLConnection buildConnection(String urlString){
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            return connection;
        } catch (Exception e){
            Log.e(TAG, "Unable to build a connection!");
        }

        return null;
    }

    //build a new connection with a url, but using the specified api acess token
    private HttpURLConnection buildConnection(String urlString, String access_token){
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Authorization", "Bearer " + access_token);

            return connection;
        } catch (Exception e){
            Log.e(TAG, "Unable to build a connection!");
        }

        return null;
    }

    //Create the authentication pass to access the API
    private String create_authorization(){
        //LOOK AT THIS CODE AND WEEP
        //THIS IS WHAT BROKE THIS APP FOR LITERALLY 3+ HOURS OF MY LIFE
        //THE DIFFERENCE
        //THE TINY DIFFERENCE
        //THAT MADE ME WASTE SO MUCH TIME MESSING WITH THE CONNECTION BECAUSE I THOUGHT THE PROBLEM WAS THERE
        //WAS USING DEFAULT VS NO_WRAP
        //           WHY
        //String auth = BASIC_API_KEY + ":" + SECRET_API_KEY;
        //return Base64.encodeToString(auth.getBytes(), Base64.DEFAULT);
        return Base64.encodeToString((BASIC_API_KEY + ":" + SECRET_API_KEY).getBytes(),
                Base64.NO_WRAP);
    }

}