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
* Tweet object to save pieces of the
* user's tweets.
*************************************
*/

import android.util.Log;

import org.json.JSONObject;

public class Tweet {

    String text;
    String time;
    String name;

    public Tweet(JSONObject json){

        try {
            //get the variables from the json
            text = json.getString("text");
            time = json.getString("created_at");
            name = json.getJSONObject("user").getString("name");

            //If it's a retweet, make sure that it's more easily known
            if(text.substring(0, 2).equals("RT")){
                text = name + " retweeted" + text.substring(2);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getText(){
        return text;
    }

    public String getTime(){ return time; }

}
