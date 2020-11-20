package com.backpackvr.ppquest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends FragmentActivity {

    public static String TAG = "ppquest";
    APKInstaller installer;
    TextView debugText;
    Activity activity;


    FrameLayout container;
    FragmentManager myFragmentManager;
    FileManager myFragment1;
    GameGridFragment myFragment2;
    GameFragment myFragment3;
    BrowserFragment myFragmentBrowser;

    final static String TAG_1 = "FRAGMENT_1";
    final static String TAG_2 = "FRAGMENT_2";
    final static String TAG_3 = "FRAGMENT_3";
    final static String TAG_4 = "FRAGMENT_4";

    final static String KEY_MSG_1 = "FRAGMENT1_MSG";
    final static String KEY_MSG_2 = "FRAGMENT2_MSG";
    final static String KEY_MSG_3 = "FRAGMENT3_MSG";
    final static String KEY_MSG_4 = "FRAGMENT4_MSG";

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();

        ImageLoader.getInstance().init(config);
        imageLoader= ImageLoader.getInstance();
    }

    View progressBar;

    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);

        progressBar.setBackgroundColor(0xFF00FF00);
        setProgress(0f);

        installer = new APKInstaller();

        activity = this;

        installer.requestPermissions(this);

        debugText = (TextView) findViewById(R.id.textDebug);
        debugText.setText("++Quest");

        initImageLoader(this);

        container = (FrameLayout)findViewById(R.id.maincontainer);

        ImageButton button1 = (ImageButton)findViewById(R.id.buttonFiles);
        ImageButton buttonGrid = (ImageButton)findViewById(R.id.buttonGrid);

        ImageButton buttonWeb = (ImageButton)findViewById(R.id.buttonWeb);
        ImageButton buttonExit = (ImageButton)findViewById(R.id.buttonExit);

        TextView tooltipExitButton = (TextView)findViewById(R.id.tooltipExitButton);
        View buttonGroupExit= findViewById(R.id.buttonGroupExit);
        buttonGroupExit.bringToFront();
        tooltipExitButton.bringToFront();
        tooltipExitButton.setVisibility(View.GONE);

        TextView tooltipFiles = (TextView)findViewById(R.id.tooltipFiles);
        View buttonGroupFiles= findViewById(R.id.buttonGroupFiles);
        buttonGroupFiles.bringToFront();
        tooltipFiles.bringToFront();
        tooltipFiles.setVisibility(View.GONE);


        TextView tooltipWeb = (TextView)findViewById(R.id.tooltipWeb);
        View buttonGroupWeb= findViewById(R.id.buttonGroupWeb);
        buttonGroupWeb.bringToFront();
        tooltipWeb.bringToFront();
        tooltipWeb.setVisibility(View.GONE);


        TextView tooltipGrid = (TextView)findViewById(R.id.tooltipGrid);
        View buttonGroupGrid= findViewById(R.id.buttonGroupGrid);
        buttonGroupGrid.bringToFront();
        tooltipGrid.bringToFront();
        tooltipGrid.setVisibility(View.GONE);


        buttonExit.setOnClickListener(new View.OnClickListener() {

                                       @Override
                                       public void onClick(View arg0) {
                                           finishAndRemoveTask();

                                       }
        });


        button1.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent ev) {
                tooltipFiles.setVisibility(View.VISIBLE);
                if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    tooltipFiles.setVisibility(View.GONE);
                }

                return false;
            }
        });
        buttonGrid.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent ev) {
                tooltipGrid.setVisibility(View.VISIBLE);
                if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    tooltipGrid.setVisibility(View.GONE);
                }

                return false;
            }
        });

        buttonWeb.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent ev) {
                tooltipWeb.setVisibility(View.VISIBLE);
                if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    tooltipWeb.setVisibility(View.GONE);
                }

                return false;
            }
        });
        buttonExit.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent ev) {
                tooltipExitButton.setVisibility(View.VISIBLE);
                if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    tooltipExitButton.setVisibility(View.GONE);
                }

                return false;
            }
        });

        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                FileManager fragment = (FileManager)myFragmentManager.findFragmentByTag(TAG_1);

                if (fragment == null) {

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MSG_1, "Replace MyFragment1");
                    myFragment1.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.maincontainer, myFragment1, TAG_1);
                    fragmentTransaction.commit();

                }else{

                    /*fragment.setMsg("MyFragment1 already loaded");*/
                    Log.i(TAG, "MyFragment1 already loaded");
                }
            }});

        buttonGrid.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                GameGridFragment fragment = (GameGridFragment)myFragmentManager.findFragmentByTag(TAG_2);

                if (fragment == null) {

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MSG_2, "Replace MyFragment2");
                    myFragment2.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.maincontainer, myFragment2, TAG_2);
                    fragmentTransaction.commit();

                }else{
                    /*fragment.setMsg("MyFragment2 already loaded");*/

                    Log.i(TAG, "MyFragment2 already loaded");
                }
            }});


        buttonWeb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                BrowserFragment fragment = (BrowserFragment)myFragmentManager.findFragmentByTag(TAG_4);

                if (fragment == null) {

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MSG_4, "Replace MyFragment2");
                    myFragmentBrowser.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.maincontainer, myFragmentBrowser, TAG_4);
                    fragmentTransaction.commit();

                }else{
                    /*fragment.setMsg("MyFragment2 already loaded");*/

                    Log.i(TAG, "MyFragment2 already loaded");
                }
            }});

        myFragmentManager = getSupportFragmentManager();
        myFragment1 = new FileManager();
        myFragment2 = new GameGridFragment();
        myFragment3 = new GameFragment();
        myFragmentBrowser = new BrowserFragment();

        if(savedInstanceState == null){
            //if's the first time created

            FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.maincontainer, myFragment2, TAG_2);
            fragmentTransaction.commit();
        }



    }

    float currentProgress  = 0;
    public void setProgress(float endScale) {
        Animation anim = new ScaleAnimation(
                currentProgress, endScale, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(300);
        currentProgress = endScale;
        progressBar.startAnimation(anim);
    }


    public void displayImage(ImageView iv, String url) {
        imageLoader.displayImage(url, iv);
    }

    public void OpenGameFragment(String gameId) {
        GameFragment fragment = (GameFragment)myFragmentManager.findFragmentByTag(TAG_3);

        if (fragment == null) {

            Bundle bundle = new Bundle();
            bundle.putString(KEY_MSG_3, gameId);
            myFragment3.setArguments(bundle);

            FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.maincontainer, myFragment3, TAG_3);
            fragmentTransaction.commit();

        }else{
            /*fragment.setMsg("MyFragment2 already loaded");*/

            Log.i(TAG, "MyFragment3 already loaded");
        }
    }

    public void CloseGameFragment() {
        FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.maincontainer, myFragment2, TAG_2);
        fragmentTransaction.commit();

    }
    public void openGame(String gameId) {
        Log.i(TAG, "game_id: " + gameId);
        OpenGameFragment(gameId);
    }

    public void downloadThis(String gameId, String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setMimeType(mimeType);
        //------------------------COOKIE!!------------------------
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        //------------------------COOKIE!!------------------------
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
        DownloadManager dm = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = dm.enqueue(request);
        /*Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();*/

        //final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = dm.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    float megs_downloaded = (float)bytes_downloaded / (1024*1024);
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    float megs_total = (float)bytes_total / (1024*1024);
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        Log.i(TAG, "finished downloading");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                debugText.setText("Download complete : " + fileName);
                                setProgress(0f);

                            }
                        });



                        installAPK(gameId, fileName);
                        cursor.close();
                        break;
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            float progress = (float)bytes_downloaded / (float)bytes_total;
                            setProgress(progress);
                            debugText.setText("Downloading " + fileName + " : " + String.format("%.2f",megs_downloaded) + "/" + String.format("%.2f",megs_total) + " MB");
                        }
                    });
                    cursor.close();
                }

            }
        }).start();

    }

    public void installAPK(String gameId, String fileName) {
        String fullPath = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName;
        if (gameId.length() > 0) {
            final PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
            String packageName = info.packageName;
            if (Database.gameList.get(gameId).images.size() > 0) {
                String imageURL = Database.gameList.get(gameId).images.get(0);
                ImageSize targetSize = new ImageSize(315, 250);
                imageLoader.loadImage(imageURL, targetSize, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/thumbnails";
                        File dir = new File(file_path);
                        if(!dir.exists())
                            dir.mkdirs();
                        File file = new File(dir, packageName + ".png");
                        FileOutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        loadedImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        try {
                            fOut.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        APKInstaller.installAPK( fullPath, this);

    }
}