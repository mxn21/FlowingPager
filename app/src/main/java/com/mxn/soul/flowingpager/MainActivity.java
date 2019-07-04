package com.mxn.soul.flowingpager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.mxn.soul.flowingpager_core.ElasticPager;
import com.mxn.soul.flowingpager_core.FlowingPager;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FlowingPager mPager;
    private RecyclerView rvFeed;
    private PlayPauseView playPauseView  ;
    private AssetFileDescriptor fd ;
    private MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (FlowingPager) findViewById(R.id.pagerlayout);
        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);
        playPauseView = (PlayPauseView) findViewById(R.id.flowingbutton);

        setupToolbar();
        setupFeed();
        setupMenu();
        initMusic() ;
        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseView.isPlaying()) {
                    playPauseView.pause();
                    mediaPlayer.pause();
                } else {
                    playPauseView.play();
                    mediaPlayer.start();
                }
            }
        });

        mPager.setOnPagerStateChangeListener(new ElasticPager.OnPagerStateChangeListener() {
            @Override
            public void onPagerStateChange(int oldState, int newState) {
                if (newState == ElasticPager.STATE_CLOSED) {
                    Log.i("MainActivity", "pager STATE_CLOSED");
                }
            }
            @Override
            public void onPagerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });
    }

    private void initMusic() {
        fd = null;
        try {
            fd = getAssets().openFd("test.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fd.getFileDescriptor(),
                    fd.getStartOffset(),fd.getLength());
            mediaPlayer.prepare() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        FeedAdapter feedAdapter = new FeedAdapter(this);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.updateItems();
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MusicDetailFragment mMenuFragment = (MusicDetailFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MusicDetailFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }
}
