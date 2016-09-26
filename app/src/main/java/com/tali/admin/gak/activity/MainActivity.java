package com.tali.admin.gak.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tali.admin.gak.db.DatabaseHelper;
import com.tali.admin.gak.adapter.ListCursorAdapter;
import com.tali.admin.gak.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String POSITION = "position";
    public static final String TITLE = "title";
    private Cursor c = null;
    private DatabaseHelper myDbHelper;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private ListCursorAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.derevo));
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(getSearchListener());

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

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        c = myDbHelper.query("history_kg", null, null, null, null, null, null);

        adapter = new ListCursorAdapter(this, c);
        adapter.setOnItemClickListener(getAdapterListener());
        recyclerView.setAdapter(adapter);

        initNavDraw();
    }

    public void resertFilter(View v){
        c = myDbHelper.query("history_kg", null, null, null, null, null, null);
        adapter.changeCursor(c);
        ((Button) findViewById(R.id.filterBtn)).setVisibility(View.GONE);
    }

    private void initNavDraw() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        Integer[] mPlanetTitles = new Integer[c.getCount()];
        for (int i = 0; i < c.getCount(); i++) {
            mPlanetTitles[i] = i+1;
        }
        System.out.println("cursor count is "+c.getCount());
        mDrawerList.setAdapter(new ArrayAdapter<Integer>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            /** Этот код вызывается, когда боковое меню переходит в полностью закрытое состояние. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Этот код вызывается, когда боковое меню полностью открывается. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ListCursorAdapter.OnItemClickListener getAdapterListener() {
        return new ListCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String title) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra(TITLE,title);
                startActivity(intent);
            }
        };
    }

    private MaterialSearchView.OnQueryTextListener getSearchListener(){
        return new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Cursor newCursor = myDbHelper.query("history_kg", null,"h_title LIKE '%"+query+"%'", null, null, null, null);
                if (newCursor.moveToFirst()) {
                    adapter.changeCursor(newCursor);
                    ((Button) findViewById(R.id.filterBtn)).setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(MainActivity.this, "По данному запросу ничего не найдено", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onDestroy() {
        c.close();
        myDbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
            intent.putExtra(POSITION,position);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
