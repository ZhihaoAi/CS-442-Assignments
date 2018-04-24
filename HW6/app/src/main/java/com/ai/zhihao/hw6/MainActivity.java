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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SVC = "ACTION_MSG_TO_SVC";
    static final String SERVICE_DATA = "SERVICE_DATA";

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> drawerItems = new ArrayList<>();

    private Menu menu;

    private NewsReceiver newsReceiver;

    private HashMap<String, ArrayList<Source>> sourcesByCategory = new HashMap<>();
    private ArrayList<Article> articles = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerList = findViewById(R.id.leftDrawer);

        drawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, drawerItems));
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
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
        pager.setBackground(getResources().getDrawable(R.drawable.background, this.getTheme()));

        new NewsSourceDownloader(this, "").execute();

    }

    private void selectItem(int position) {

        pager.setBackground(null);
        setTitle(drawerItems.get(position));

        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SVC);
        String selectedID = null;
        for (Source s : sourcesByCategory.get("all")) {
            if (s.getName().equals(drawerItems.get(position))) {
                selectedID = s.getId();
                break;
            }
        }
        Log.d(TAG, "selectItem: " + selectedID);
        intent.putExtra("SourceID", selectedID);
        if (selectedID != null)
            sendBroadcast(intent);
        drawerLayout.closeDrawer(drawerList);
    }

    private void reDoFragments() {

//        for (int i = 0; i < pageAdapter.getCount(); i++)
//            pageAdapter.notifyChangeInPosition(i);
        pageAdapter.notifyChangeInPosition(pageAdapter.getCount());

        fragments.clear();

        int i = 1;
        for (Article a : articles) {
            String count = String.format("%d of %d", i++, articles.size());
            fragments.add(NewsFragment.newInstance(a.getTitle(), a.getAuthor(), a.getDescription(), a.getUrlToImage(), a.getTime(), count, a.getUrl()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        drawerItems.clear();
        for (Source s : sourcesByCategory.get(item.getTitle())) {
            drawerItems.add(s.getName());
        }

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();

        return true;

    }

    public void setSources(ArrayList<Source> sources, ArrayList<String> categories) {

        drawerItems.clear();
        sourcesByCategory.clear();

        for (Source s : sources) {
            drawerItems.add(s.getName());
            if (!sourcesByCategory.containsKey(s.getCategory())) {
                sourcesByCategory.put(s.getCategory(), new ArrayList<Source>());
            }
            sourcesByCategory.get(s.getCategory()).add(s);
        }
        sourcesByCategory.put("all", sources);

        for (String s : categories)
            menu.add(s);

//        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, drawerItems));
        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
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
         *
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
                    if (intent.hasExtra(SERVICE_DATA))
                        articles = (ArrayList<Article>) intent.getSerializableExtra(SERVICE_DATA);
                    reDoFragments();
                    break;
            }
        }
    }

}
