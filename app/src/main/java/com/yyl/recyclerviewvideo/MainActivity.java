package com.yyl.recyclerviewvideo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yyl.recyclerviewvideo.adapter.TestAdapter;
import com.yyl.recyclerviewvideo.fragment.VideoFragment;
import com.yyl.recyclerviewvideo.view.RecyclerViewVideo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * * Created by yyl on 2016/5/27/027.
 */
public class MainActivity extends AppCompatActivity {
    String tag = getClass().getName();
    @Bind(R.id.list)
    RecyclerView testRecyclerview;

    @Bind(R.id.header)
    RecyclerViewVideo header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ButterKnife.bind(this);
        TestAdapter adapter = new TestAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        testRecyclerview.setLayoutManager(linearLayoutManager);
        testRecyclerview.setAdapter(adapter);
        adapter.init();
        videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video);
        header.setOnChangeVisivibleState(new RecyclerViewVideo.OnChangeVisivibleState() {
            @Override
            public void change(boolean hidden) {
//                if (videoFragment != null)
//                    videoFragment.setMode(hidden);
            }
        });
        header.attachTo(testRecyclerview);

        TextView textView = new TextView(this);
        textView.setText("this is heard4");
        textView.setGravity(Gravity.CENTER);
        adapter.addHeaderView(textView);
    }


    boolean expand;
    VideoFragment videoFragment;

    @OnClick(R.id.button)
    public void onClick() {
        header.setFullVideo(expand = !expand);
        //   videoFragment.setFullState(expand);
    }
}
