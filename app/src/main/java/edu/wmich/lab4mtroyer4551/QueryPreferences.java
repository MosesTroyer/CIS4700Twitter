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
* Handles everything regarding saving
* information across sessions, such
* as the stalk victim and the
* previous searches.
*************************************
*/

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_STALK_QUERY = "stalkQuery";
    private static final String PREF_LAST_RESULT = "lastResult";
    private static final String PREF_STALK_VICTIM = "stalkVictim";

    public static String getStoredSearch(Context context){
        return getStoredQuery(context, PREF_SEARCH_QUERY);
    }

    public static void setStoredSearch(Context context, String query){
        setStoredQuery(context, query, PREF_SEARCH_QUERY);
    }

    public static String getStoredStalk(Context context){
        return getStoredQuery(context, PREF_STALK_QUERY);
    }

    public static void setStoredStalk(Context context, String query){
        setStoredQuery(context, query, PREF_STALK_QUERY);
    }

    public static String getStalkVictim(Context context){
        return getStoredQuery(context, PREF_STALK_VICTIM);
    }

    public static void setStalkVictim(Context context, String victim){
        setStoredQuery(context, victim, PREF_STALK_VICTIM);
    }

    public static String getLastResult(Context context){
        return getStoredQuery(context, PREF_LAST_RESULT);
    }

    public static void setLastResult(Context context, String query){
        setStoredQuery(context, query, PREF_LAST_RESULT);
    }

    private static String getStoredQuery(Context context, String pref) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(pref, null);
    }

    private static void setStoredQuery(Context context, String query, String pref) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(pref, query)
                .apply();
    }


}
