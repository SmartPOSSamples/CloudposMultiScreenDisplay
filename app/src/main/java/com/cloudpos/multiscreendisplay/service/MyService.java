package com.cloudpos.multiscreendisplay.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.cloudpos.multiscreendisplay.MyPresentation;

/**
 * Created by gecx on 17-3-31.
 */

public class MyService extends Service {

    private final String TAG= "screenService";
    public static final String INTENT_EXTRA_FLAG = "intent_extra_flag";
    public static final int TYPE_OPEN_PRESENTATION = 0;
    public static final int TYPE_CLOSE_PRESENTATION = 1;
    public static final int TYPE_PLAY_VIDEO= 2;
    public static final int TYPE_STOP_VIDEO= 3;

    private MediaRouter mMediaRouter;
    private MyPresentation mPresentation;

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommend = " + mPresentation);

        int intExtra = intent.getIntExtra(INTENT_EXTRA_FLAG, -1);
        Log.d(TAG, "intExtra:" + intExtra);
        if (intExtra == TYPE_OPEN_PRESENTATION) {
            if (mPresentation == null) {
                updatePresentation();
            }
        } else if (intExtra == TYPE_CLOSE_PRESENTATION) {
            if (mPresentation != null) {
                mPresentation.dismiss();
                onDestroy();
            }
        } else if (intExtra == TYPE_PLAY_VIDEO) {
            if (mPresentation != null) {
                mPresentation.playVideo();
            }
        } else if (intExtra == TYPE_STOP_VIDEO) {
            if (mPresentation != null) {
                mPresentation.stopVideo();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updatePresentation() {
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            Log.i(TAG, "Dismissing presentation because the current route no longer "
                    + "has a presentation display.");
            mPresentation.dismiss();
            mPresentation = null;
        }

        // display presentation
        if (mPresentation == null && presentationDisplay != null) {
            Log.i(TAG, "Showing presentation on display: " + presentationDisplay);
            mPresentation = new MyPresentation(this, presentationDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in "
                        + "the meantime.", ex);
                mPresentation = null;
            }
        }

        // Update the contents playing in this activity.
//        updateContents();
    }

    /**
     * Listens for when presentations are dismissed.
     */
    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (dialog == mPresentation) {
                Log.i(TAG, "Presentation was dismissed.");
                mPresentation.stopVideo();
                mPresentation = null;
//                        updateContents();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
