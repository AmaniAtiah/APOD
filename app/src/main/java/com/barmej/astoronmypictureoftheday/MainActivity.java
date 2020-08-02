package com.barmej.astoronmypictureoftheday;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.barmej.astoronmypictureoftheday.entity.NasaPicture;
import com.barmej.astoronmypictureoftheday.network.NetworkUtils;
import com.barmej.astoronmypictureoftheday.utils.APODData;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TouchImageView mNasaPictureImageView;
    private WebView mWebView;
    private TextView mTitle;
    private TextView mDescription;
    private ConstraintLayout constraintLayout;
    private NasaPicture mNasaPicture;
    private long downloadId;
    private NetworkUtils mNetworkUtils;
    private LinearLayout linearLayout;
    private MenuItem downloadHdMenuItem;
    private SimpleDateFormat simpleDateFormat;
    private String titleName;
    private String description;
    private String currentDate;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNasaPictureImageView = findViewById(R.id.img_picture_view);
        mWebView = findViewById(R.id.wv_video_player);
        mTitle = findViewById(R.id.title);
        mDescription = findViewById(R.id.description);
        constraintLayout = findViewById(R.id.constraint);
        linearLayout = findViewById(R.id.bottom_sheet);
        constraintLayout.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);

        mNetworkUtils = NetworkUtils.getInstance(this);

        registerReceiver(onCompleteDownload,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        currentDate = simpleDateFormat.format(new Date());
        requestApod(currentDate);
    }

    private BroadcastReceiver onCompleteDownload = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context,Intent intent) {
            long broadcastedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if (broadcastedDownloadId == downloadId) {
                Toast.makeText(MainActivity.this,"Download complete",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,"Download not complete",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkUtils.cancelRequest(TAG);
    }

    @SuppressLint("ResourceType")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        downloadHdMenuItem = menu.findItem(R.id.action_download_hd);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onCompleteDownload);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pick_day) {
            showDatePicker();

        } else if (item.getItemId() == R.id.action_download_hd) {
            beginDownload();

        } else if (item.getItemId() == R.id.action_share) {
            shareImageOrVideo();

        } else if (item.getItemId() == R.id.action_about) {
            showAbout();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(),"date picker");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void beginDownload() {
        if (mNasaPicture.getMediaType().equals("image")) {
            uri = Uri.parse(mNasaPicture.getHdurl());
            File file = new File(getExternalFilesDir(null),"Download file");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Data Download");
            request.setDescription("Download image");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(file));
            request.setRequiresCharging(false);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
        }
    }

    private void shareImageOrVideo() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (mNasaPicture.getMediaType().equals("image")) {
            Bitmap mBitmap = ((BitmapDrawable) mNasaPictureImageView.getDrawable()).getBitmap();
            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    mBitmap,mNasaPicture.getTitle(),null);
            uri = Uri.parse(path);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            intent.putExtra(Intent.EXTRA_TEXT,mNasaPicture.getTitle());
            startActivity(Intent.createChooser(intent,"Select"));

        } else if (mNasaPicture.getMediaType().equals("video")) {
            intent.setAction(Intent.ACTION_SEND);
            uri = Uri.parse(mNasaPicture.getUrl());
            intent.putExtra(Intent.EXTRA_TEXT,mNasaPicture.getTitle() + "\n" + uri);
            intent.setType("text/plain");
            startActivity(intent);
        }
    }

    private void showAbout() {
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.show(getSupportFragmentManager(),"about");
    }

    @Override
    public void onDateSet(DatePicker view,int year,int month,int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
        requestApod(date);
    }

    private void requestApod(String date) {
        String apod = NetworkUtils.getPictureUrl(MainActivity.this,date).toString();
        JsonObjectRequest apodRequest = new JsonObjectRequest(
                Request.Method.GET,
                apod,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        NasaPicture nasaPicture = null;
                        try {
                            nasaPicture = APODData.getPictureInfoObjectFromJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (nasaPicture != null) {
                            updateApod(nasaPicture);
                            updateMenu();
                            constraintLayout.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        apodRequest.setTag(TAG);
        mNetworkUtils.addRequestQueue(apodRequest);
    }

    private void showApod() {
        if (mNasaPicture.getMediaType().equals("image")) {
            mNasaPictureImageView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
            Picasso.get()
                    .load(mNasaPicture.getUrl())
                    .into(mNasaPictureImageView);

        } else if (mNasaPicture.getMediaType().equals("video")) {
            mNasaPictureImageView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mNasaPicture.getUrl());
        }
        titleName = mNasaPicture.getTitle();
        mTitle.setText(titleName);
        description = mNasaPicture.getExplanation();
        mDescription.setText(description);
    }

    private void updateApod(NasaPicture nasaPicture) {
        mNasaPicture = nasaPicture;
        showApod();
    }

    private void updateMenu() {
        if (mNasaPicture.getMediaType().equals("image")) {
            downloadHdMenuItem.setVisible(true);
        } else if (mNasaPicture.getMediaType().equals("video")) {
            downloadHdMenuItem.setVisible(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustFullScreen(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            adjustFullScreen(getResources().getConfiguration());
        }
    }

    private void adjustFullScreen(Configuration config) {
        final View decorView = getWindow().getDecorView();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

}

