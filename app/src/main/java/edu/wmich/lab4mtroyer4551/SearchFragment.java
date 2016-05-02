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
* The 'home' screen of the app.
* Allows the user to search for a
* twitter user, or get their tweets
* if the username is known.
*************************************
*/

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchFragment extends Fragment {

    EditText editTextUser;
    EditText editTextSearchUser;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //give it that actionbar
        setHasOptionsMenu(true);

        //stalk stalking
        PollService.setServiceAlarm(getActivity(), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        //link buttons and the text views
        Button buttonStalk = (Button) v.findViewById(R.id.buttonStalk);
        editTextUser = (EditText) v.findViewById(R.id.editTextUser);

        Button buttonSearch = (Button) v.findViewById(R.id.buttonSearch);
        editTextSearchUser = (EditText) v.findViewById(R.id.editTextUserSearch);

        //when the stalk button is pressed, save the last search, then launch the activity
        //to show off all of their tweets
        buttonStalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUser.getText().toString();

                QueryPreferences.setStoredStalk(getContext(), username);

                Intent intent = new Intent(getActivity(), UserTimelineActivity.class);
                intent.putExtra("EXTRA_USERNAME", username);
                startActivity(intent);
            }
        });

        //opens a webview with a new search for a user. Adds in 'twitter' for results
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editTextSearchUser.getText().toString();

                //save the search
                QueryPreferences.setStoredSearch(getContext(), username);

                //https://www.google.com/#q=twitter+bill+trinen
                String url = "https://www.google.com/#q=twitter";

                //split up their username by spaces for the google query
                String[] delimitedUsername = username.split(" ");

                for (String s : delimitedUsername) {
                    url += "+" + s;
                }

                //open up a new webview
                Intent i = WebViewActivity
                        .newIntent(getActivity(), Uri.parse(url));

                startActivity(i);
            }
        });

        updateFields();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear_terms:
                //clear out the previously saved search terms so they're not saved

                QueryPreferences.setStoredSearch(getContext(), null);
                editTextUser.getText().clear();

                QueryPreferences.setStoredStalk(getContext(), null);
                editTextSearchUser.getText().clear();

                return true;

            case R.id.menu_item_clear_stalk:
                //Clear out the victim, probably to lose the trail
                QueryPreferences.setStoredSearch(getContext(), null);

                Toast toast = Toast.makeText(getContext(), "Cleared stalk victim!", Toast.LENGTH_SHORT);
                toast.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //update the saved text on the text fields, based on what they were previously
    private void updateFields(){
        editTextSearchUser.setText(QueryPreferences.getStoredSearch(getContext()));
        editTextUser.setText(QueryPreferences.getStoredStalk(getContext()));
    }

}

