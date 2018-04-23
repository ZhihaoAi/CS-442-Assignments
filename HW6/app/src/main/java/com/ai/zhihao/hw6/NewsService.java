package com.ai.zhihao.hw6;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by zhihaoai on 4/22/18.
 */

public class NewsService extends Service {

    private static final String TAG = "CountService";
    static final String ACTION_MSG_TO_SVC = "ACTION_MSG_TO_SVC";
    private boolean running = true;

    private ArrayList<Article> storyList = new ArrayList<>();

    private ServiceReceiver serviceReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceReceiver = new ServiceReceiver();

        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SVC);
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (storyList.isEmpty()){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_NEWS_STORY);
                        intent.putExtra(MainActivity.SERVICE_DATA, storyList);
                        sendBroadcast(intent);
                        storyList.clear();
                    }
                }
                Toast.makeText(NewsService.this, "Service shut down", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "run: Ending loop");
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        running = false;
        super.onDestroy();
    }

    public void setArticles (ArrayList<Article> articles) {
        storyList.clear();
        storyList.addAll(articles);
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ACTION_MSG_TO_SVC:
                    String source = "";
                    if (intent.hasExtra("SourceID"))
                        source = intent.getStringExtra("SourceID");
                    Log.d(TAG, "onReceive: " + source);
                    new NewsArticleDownloader(NewsService.this, source).execute();
                    break;
            }
        }
    }

}
