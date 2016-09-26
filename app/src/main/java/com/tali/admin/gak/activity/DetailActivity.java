package com.tali.admin.gak.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.tali.admin.gak.db.DatabaseHelper;
import com.tali.admin.gak.R;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    private ShareActionProvider mShareActionProvider;
    private Cursor c = null;
    private DatabaseHelper myDbHelper;
    private TextView m_no;
    private TextView m_title;
    private TextView m_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.derevo));
        m_no = (TextView) findViewById(R.id.d_no);
        m_title = (TextView) findViewById(R.id.d_title);
        m_description = (TextView) findViewById(R.id.d_description);

        String title = getIntent().getStringExtra(MainActivity.TITLE);
        int index = getIntent().getIntExtra(MainActivity.POSITION,-1);

        myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        if(index != -1){
            c = myDbHelper.query("HISTORY_KG", null, null, null, null, null, null);
            c.moveToPosition(index);
            m_no.setText("Билет номер: "+ c.getString(0));
            m_title.setText(c.getString(1));
            m_description.setText(c.getString(2));
        } else {
            c = myDbHelper.query("HISTORY_KG", null, "h_title ='" + title + "'", null, null, null, null);
            if (c.moveToFirst()) {
                m_no.setText("Билет номер: " + c.getString(0));
                m_title.setText(c.getString(1));
                m_description.setText(c.getString(2));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // Set share Intent.
        // Note: You can set the share Intent afterwords if you don't want to set it right now.
        mShareActionProvider.setShareIntent(createShareIntent());
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String text = m_no.getText().toString() + "\n"
                + m_title.getText().toString() + "\n"
                + m_description.getText().toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        return shareIntent;
    }

    @Override
    protected void onDestroy() {
        c.close();
        myDbHelper.close();
        super.onDestroy();
    }
}
