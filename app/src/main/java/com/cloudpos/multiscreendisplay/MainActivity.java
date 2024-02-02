package com.cloudpos.multiscreendisplay;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GridView mGridView;

    private MediaRouter mMediaRouter;
    private MyPresentation mPresentation;

    public static Context mContext;

    public static String filePath;

    private String[] movieItems;
    TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        mContext = this;
        initView();
        initUI();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mTv = (TextView) findViewById(R.id.tv);
    }

    private void initUI() {
        String[] strs = {"Start MultiScreen", "Stop MultiScreen", "Play Video\n(/sdcard/Movies/)", "Stop Play"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, strs);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new MyItemListener());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                getFile();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFile();
                }
                break;
        }
    }

    public void getFile() {
        File file = new File("/sdcard/Movies/");
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                movieItems = new String[listFiles.length];
                for (int i = 0; i < listFiles.length; i++) {
                    Log.d(TAG, "file:" + listFiles[i].getName());
                    movieItems[i] = listFiles[i].getName();
                }
            }
        }
    }


    class MyItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    if (mPresentation == null) {
                        updatePresentation();
                    }
                    break;
                case 1:
                    if (mPresentation != null) {
                        mPresentation.dismiss();
                    }
                    break;
//                case 2:
//                    Intent intent = new Intent(mContext, MyService.class);
//                    intent.putExtra(MyService.INTENT_EXTRA_FLAG, MyService.TYPE_OPEN_PRESENTATION);
//                    startService(intent);
//                    break;
//                case 3:
//                    Intent intent1 = new Intent(mContext, MyService.class);
//                    intent1.putExtra(MyService.INTENT_EXTRA_FLAG, MyService.TYPE_CLOSE_PRESENTATION);
//                    startService(intent1);
//                    break;
                case 2:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Select video:");
                    builder.setSingleChoiceItems(movieItems, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filePath = "/sdcard/Movies/" + movieItems[which];
                            dialog.dismiss();
                            Log.d(TAG, "filePath:" + filePath);
                            if (mPresentation != null) {
                                mPresentation.playVideo();
                            }
//                            else {
//                                Intent intent2 = new Intent(mContext, MyService.class);
//                                intent2.putExtra(MyService.INTENT_EXTRA_FLAG, MyService.TYPE_PLAY_VIDEO);
//                                startService(intent2);
//                            }
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                case 3:
                    if (mPresentation != null) {
                        Log.d(TAG, "stopVideo");
                        mPresentation.stopVideo();
                        Log.d(TAG, "stopVideo  end ");
                    }
//                    else {
//                        Intent intent3 = new Intent(mContext, MyService.class);
//                        intent3.putExtra(MyService.INTENT_EXTRA_FLAG, MyService.TYPE_STOP_VIDEO);
//                        startService(intent3);
//                    }
                    break;


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            Log.i(TAG, "Presentation was dismissed. : " + (dialog == mPresentation));
            if (dialog == mPresentation) {
                mPresentation.stopVideo();
                mPresentation = null;
//                updateContents();
            }
        }
    };

    private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() {
        @Override
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteSelected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: type=" + type + ", info=" + info);
            updatePresentation();
        }

        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
            updatePresentation();
        }
    };
}
