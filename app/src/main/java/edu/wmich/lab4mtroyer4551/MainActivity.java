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
* Put in a new Search Fragment.
*************************************
*/

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }

}
