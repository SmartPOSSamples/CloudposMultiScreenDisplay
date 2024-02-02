package com.cloudpos.multiscreendisplay;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by gecx on 17-3-10.
 */

public class MyMediaPlayer extends MediaPlayer implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private Context context;
    private SurfaceHolder holder;
    private final String TAG = MyMediaPlayer.class.getSimpleName();
    private SurfaceView mSurface;

    MyMediaPlayer(Context context, SurfaceHolder holder, SurfaceView mSurface) {
        this.context = context;
        this.holder = holder;
        this.mSurface = mSurface;

    }

    public void initAndPlay() {
        setOnCompletionListener(this);
        setOnPreparedListener(this);
        setOnErrorListener(this);

        String dataPath = MainActivity.filePath;
        try {
            setDataSource(dataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        prepareAsync();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
//        Log.d(TAG, "surfaceview2 : " + holder.getSurface().getWidth() + "=" + surfaceView.getHeight());
    }

    public void movieStop() {
//        stop();
//        release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "onComletion called");
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError");
        return false;
    }
}
