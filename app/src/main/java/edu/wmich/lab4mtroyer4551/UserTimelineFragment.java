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
* Everything regarding showing the
* user's timeline of tweets. Has the
* option to stalk them and view them
* on the official twitter site
*************************************
*/

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

public class UserTimelineFragment extends Fragment {

    private RecyclerView tweetRecyclerView;
    private TweetAdapter adapter;
    private Bundle extras;

    private boolean notFound = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        extras = getActivity().getIntent().getExtras();

        //menu handling won't be a thing
        //if it ain't got this swing
        setHasOptionsMenu(true);

        String username = null;

        try {
            //get the username passed into the activity
            username = extras.getString("EXTRA_USERNAME");
        } catch (Exception e){
            // Throws exception if we're coming from the notification, since it doesn't exist
            username = QueryPreferences.getStoredStalk(getContext());
        }


        //get the users timeline to show
        FetchItemsTask twitter = new FetchItemsTask(this);
        twitter.execute(twitter.GET_USER_TIMELINE, username);

    }

    //create the menu and add items to the bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user, menu);

    }

    @Override //when the user touches the menu
    public boolean onOptionsItemSelected(MenuItem item) {

        //if the page doesn't show a user and instead the error
        if(notFound){
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_item_view_twitter:
                //start up the webview activity to show off the users twitter

                String url = "http://www.twitter.com/" + extras.getString("EXTRA_USERNAME");

                Intent i = WebViewActivity
                        .newIntent(getActivity(), Uri.parse(url));

                startActivity(i);

                return true;

            case R.id.menu_item_stalk:
                //set the current user as the stalked user
                QueryPreferences.setStalkVictim(getContext(), extras.getString("EXTRA_USERNAME"));

                Toast toast = Toast.makeText(getContext(), "Stalking user!", Toast.LENGTH_SHORT);
                toast.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_timeline, container, false);

        //create a recycler view for the list of ttweets and return it
        tweetRecyclerView = (RecyclerView) view
                .findViewById(R.id.tweet_recycler_view);
        //required or else will crash
        tweetRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void updateUI(List<Tweet> tweets) {

        //if there was no user by that name, make it known
        if(tweets.size() == 0){
            try {
                notFound = true;

                //make a fake tweet
                JSONObject notFoundJson = new JSONObject("{'text': 'User not found, or there are no tweets by this user.'}");

                tweets.add(new Tweet(notFoundJson));

                MenuItem viewTwitter = (MenuItem) getView().findViewById(R.id.menu_item_view_twitter);
                viewTwitter.setEnabled(false);


            } catch (Exception e){}
        }

        if(adapter == null) {
            adapter = new TweetAdapter(tweets);
            tweetRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    //this viewholder will store references to the displayed variables
    private class TweetHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Tweet tweet;


        private TextView tweetTextView;

        public TweetHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tweetTextView = (TextView) itemView.findViewById(R.id.list_item_text);
        }


        public void bindTweet(Tweet tweet){
            this.tweet = tweet;
            tweetTextView.setText(this.tweet.getText());
        }


        @Override
        public void onClick(View v) {}
    }

    //recyclerview will talk to this when a viewholder needs to be created or connected with
    //a hero
    private class TweetAdapter extends RecyclerView.Adapter<TweetHolder>{
        private List<Tweet> tweets;

        public TweetAdapter(List<Tweet> tweets){
            this.tweets = tweets;
        }

        @Override //inflate a new view to hold the tweet
        public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_tweet, parent, false);
            return new TweetHolder(view);
        }

        @Override //set the text on the tweet when it is bound
        public void onBindViewHolder(TweetHolder holder, int position) {
            Tweet tweet = tweets.get(position);
            holder.bindTweet(tweet);
        }

        @Override //get how many tweets there are
        public int getItemCount() {
            return tweets.size();
        }
    }

}
