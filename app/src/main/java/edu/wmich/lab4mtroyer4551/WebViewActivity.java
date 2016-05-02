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
* Creates a new webview based on the given url
*************************************
*/

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class WebViewActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri uri) {
        Intent i = new Intent(context, WebViewActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return WebViewFragment.newInstance(getIntent().getData());
    }

}
