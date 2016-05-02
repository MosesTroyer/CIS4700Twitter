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
* The class contains all the
* information needed to poll the
* twitter api every minute for new
* tweets.
*************************************
*/

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60; // 60 seconds
    //Normally this wouldn't be so low, but testing this at 15 minutes would be less fun

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            //if the network doesn't work, abort mission
            return;
        }

        //get the saved username and the time of their last tweet
        String query = QueryPreferences.getStalkVictim(this);
        String lastResultId = QueryPreferences.getLastResult(this);
        List<Tweet> tweetList;

        if (query == null) {
            //if they're not a stalker
            return;
        } else {
            //get the list of tweets
            FetchItemsTask twitter = new FetchItemsTask(null);

            tweetList = twitter.constructTweets(twitter.getUserTimeline(query));
        }

        //they don't exist or they're not social
        if (tweetList.size() == 0) {
            return;
        }

        String resultId = tweetList.get(0).getTime();

        //check if it's a new tweet
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);

            //ccreate a new notification to alert stalker
            Resources resources = getResources();
            Intent i = UserTimelineActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_tweet))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_tweet))
                    .setContentText(resources.getString(R.string.new_tweet_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            //notify them
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
        }

        //set the new last result
        QueryPreferences.setLastResult(this, resultId);
    }

    //check to make sure the phone's internet is available
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    //set the background task to go every minute
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

}
