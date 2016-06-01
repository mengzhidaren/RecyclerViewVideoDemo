package com.yyl.recyclerviewvideo.fragment;


import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;


import com.yyl.recyclerviewvideo.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment with a yyl +1 button.
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    String tag = "yyl";
    @Bind(R.id.textureView)
    TextureView textureView;


    LibVLC libVLC;
    MediaPlayer mediaPlayer;
    String path = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libVLC = new LibVLC();
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.setMedia(new Media(libVLC, Uri.parse(path)));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setMode(boolean small) {
        if (!isResumed()) return;

    }
    boolean isFullState;
    public void setFullState(boolean isFull){
        isFullState=isFull;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(tag, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer.getVLCVout().setVideoSurface(new Surface(surface), null);
                mediaPlayer.getVLCVout().attachViews();
                mediaPlayer.play();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mediaPlayer.getVLCVout().detachViews();
                mediaPlayer.stop();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
//        textureView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i(tag, "onTouch   isFullState="+isFullState);
//                return true;
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
