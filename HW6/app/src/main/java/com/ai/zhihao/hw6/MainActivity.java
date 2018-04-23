package com.ai.zhihao.hw6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String SERVICE_DATA = "SERVICE_DATA";

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> items = new ArrayList<>();

    private NewsReceiver newsReceiver;

    private HashMap<String, ArrayList<Source>> sourceData = new HashMap<>();

    private Menu opt_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

        //
        items.add("Category 1");
        items.add("Category 2");
        items.add("Category 3");

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerList = findViewById(R.id.leftDrawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);

        new NewsSourceDownloader(this, "").execute();
//        selectItem(0);

    }

    private void selectItem(int position) {
        Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show();
        setTitle(items.get(position));
        reDoFragments(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void reDoFragments(int idx) {

//        for (int i = 0; i < pageAdapter.getCount(); i++)
//            pageAdapter.notifyChangeInPosition(i);
        pageAdapter.notifyChangeInPosition(pageAdapter.getCount());

        fragments.clear();
        String src = items.get(idx);
        int count = (int) (Math.random() * 8 + 2);

        for (int i = 0; i < count; i++) {
            fragments.add(NewsFragment.newInstance(src + ", Item #" + (i+1) + " of " + count));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        opt_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        items.clear();
        for (Source s : sourceData.get(item.getTitle())){
            items.add(s.getName());
        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();

        return true;

    }

    public void setSources (ArrayList<Source> sources, ArrayList<String> categories) {

        items.clear();
        this.sourceData.clear();

        for (Source s : sources) {
            items.add(s.getName());
            if (!sourceData.containsKey(s.getCategory())) {
                sourceData.put(s.getCategory(), new ArrayList<Source>());
            }
            sourceData.get(s.getCategory()).add(s);
        }
        sourceData.put("all", sources);

        for (String s : categories)
            opt_menu.add(s);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, items));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

//////////////////////////////////////////////////////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
//            baseId += getCount() + n;
            baseId += n;
        }

    }

//////////////////////////////////////////////////////////////////////////////////////

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    // TODO: 4/22/18 Get the Article list from the intent s extras
                    break;
            }
        }
    }

}
