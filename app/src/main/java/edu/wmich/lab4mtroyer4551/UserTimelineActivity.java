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
* Activity to start a new
* UserTimelineFragment
*************************************
*/

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class UserTimelineActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, UserTimelineActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new UserTimelineFragment();
    }
}