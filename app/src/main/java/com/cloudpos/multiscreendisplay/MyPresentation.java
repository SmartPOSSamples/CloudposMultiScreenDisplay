package com.cloudpos.multiscreendisplay;

import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by gecx on 16-11-22.
 */

public class MyPresentation extends Presentation implements View.OnClickListener {

    private Context mContext;
    private TextView mText;
    private SurfaceView mSurface;
    private Context context;
    private MyMediaPlayer player;
    private Button mBtnMiddle;
    private Button mBtnLeft;
    private Button mBtnQR;
    private Button mBtnQROut;
    EditText mEdit;
    ImageView mImage;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    public MyPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.context = outerContext;
        this.mContext = getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setType(WindowManager.LayoutParams.TYPE_PRIVATE_PRESENTATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        setContentView(R.layout.presentation_main);
        //This TypeThe extended screen can be used to solve the problem of click failure beyond the scope of the main screen, but the dialog cannot be suspended above the extended screen.
//        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        initView();
        initUI();
    }

    private void initView() {
        mText = (TextView) findViewById(R.id.tv_presentation);
        mSurface = (SurfaceView) findViewById(R.id.surfaceview);
        mEdit = (EditText) findViewById(R.id.edittext);
        mBtnMiddle = (Button) findViewById(R.id.btn_dialog_middle);
        mBtnLeft = (Button) findViewById(R.id.btn_dialog_left);
        mBtnQR = (Button) findViewById(R.id.btn_qr);
        mBtnQROut = (Button) findViewById(R.id.btn_qr_outofrange);
        mImage = (ImageView) findViewById(R.id.image);
    }

    private void initUI() {
        mBtnMiddle.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnQR.setOnClickListener(this);
        mBtnQROut.setOnClickListener(this);

        mSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        builder = new AlertDialog.Builder(mContext);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_middle:
                dialog = builder.setTitle("T")
                        .setPositiveButton("OK", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
                break;

            case R.id.btn_dialog_left:
                dialog = builder.setTitle("T")
                        .setPositiveButton("OK", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.LEFT);
                break;

            case R.id.btn_qr:
            case R.id.btn_qr_outofrange:
                int visibility = mImage.getVisibility();
                if (visibility == View.GONE) {
                    mImage.setVisibility(View.VISIBLE);
                } else if (visibility == View.VISIBLE) {
                    mImage.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
        super.onStop();
    }

    public void playVideo() {
        mSurface.setVisibility(View.VISIBLE);

        mSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player = new MyMediaPlayer(context, holder,mSurface);

                player.initAndPlay();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void stopVideo() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            mSurface.setVisibility(View.GONE);
        }
    }
}
