package com.cloudpos.multiscreendisplay;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by gecx on 17-3-13.
 */

public class OperateMedia implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private int position = 0;// playPosition
    private SurfaceView surfaceView;
    private Context context;
    private MediaPlayer mediaPlayer;
    private int currentPlay = 0;
    private boolean justBack = false;

    public OperateMedia(Context context, MediaPlayer mediaPlayer,
                        SurfaceView surfaceView) {
        this.context = context;
        this.mediaPlayer = mediaPlayer;
        this.surfaceView = surfaceView;
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setOnBufferingUpdateListener(this);
        this.mediaPlayer.setOnCompletionListener(this);
        this.surfaceView.getHolder().setKeepScreenOn(true);
        this.surfaceView.getHolder().setType(
                SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.surfaceView.getHolder().addCallback(new SurfaceCallback());

    }

    public void loadSrc(int num) {
        if (num != currentPlay) {
            position = 0;
            try {
                currentPlay = num;
                mediaPlayer.reset();
                AssetFileDescriptor fd = null;
                switch (num) {
                    case 0:
                        fd = context.getAssets().openFd("a.mp4");
                        break;
                    case 1:
                        fd = context.getAssets().openFd("b.mp4");
                        break;
                    case 2:
                        fd = context.getAssets().openFd("c.mp4");
                        break;
                    case 3:
                        fd = context.getAssets().openFd("d.mp4");
                        break;
                }
                mediaPlayer.setDataSource(fd.getFileDescriptor(),
                        fd.getStartOffset(), fd.getLength());
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;// break;
        }
        if (num == currentPlay) {
            if (justBack) {
                play();
                return;
            } else {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                mediaPlayer.setDisplay(surfaceView.getHolder());
            }
        }

    }

    public void play() {
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                if (position > 0) {
                    mediaPlayer.seekTo(position);
                    if (justBack) {
                        justBack = false;
                        position = 0;
                    }
                }
                mediaPlayer.start();
            }
        });
        mediaPlayer.setDisplay(surfaceView.getHolder());
    }

    public void onCompletion(MediaPlayer mp) throws IllegalStateException {
        if (currentPlay == 0) {
//            MediaVideo.justPlay = false;
        }
    }

    //
    public void onBufferingUpdate(MediaPlayer mp, int percent)
            throws IllegalStateException {
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if (position > 0) {
                loadSrc(currentPlay);
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mediaPlayer.isPlaying()) {
                justBack = true;
                position = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }

    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

}
