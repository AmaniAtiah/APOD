package com.barmej.astoronmypictureoftheday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.barmej.astoronmypictureoftheday.utils.OpenPictureDataParser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TouchImageView mNasaPictureImageView;
    private WebView mWebView;
    private TextView mTitle;
    private TextView mDescription;
    private ConstraintLayout constraintLayout;
    private NasaPicture mNasaPicture;
    private DrawerLayout mDrawerLayout;
    private long downloadId;
    private NetworkUtils mNetworkUtils;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout linearLayout;
    View bottomSheet;
    Menu mMenu;
    MenuItem downloadHdMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNasaPictureImageView = findViewById(R.id.img_picture_view);
        mWebView = findViewById(R.id.wv_video_player);

        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mTitle = findViewById(R.id.title);
        mDescription = findViewById(R.id.description);

        mDrawerLayout = findViewById(R.id.drawer);

        constraintLayout = findViewById(R.id.constraint);
        linearLayout = findViewById(R.id.bottom_sheet);

        mNetworkUtils = NetworkUtils.getInstance(this);

        constraintLayout.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);







        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context,Intent intent) {
                long broadcastedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (broadcastedDownloadId == downloadId) {
                    if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(MainActivity.this,"Download complete",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Download not complete",Toast.LENGTH_SHORT).show();

                    }

                }

            }
        },filter);


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view,int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Toast.makeText(MainActivity.this,"Collapsed",Toast.LENGTH_SHORT).show();
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                        Toast.makeText(MainActivity.this,"Dragging",Toast.LENGTH_SHORT).show();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        Toast.makeText(MainActivity.this,"Expanded",Toast.LENGTH_SHORT).show();
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        Toast.makeText(MainActivity.this,"Hidden",Toast.LENGTH_SHORT).show();
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        Toast.makeText(MainActivity.this,"Settling",Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view,float v) {

            }
        });



        requestApod(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkUtils.cancelRequest(TAG);
    }

    private int getDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);

            return status;
        }
        return DownloadManager.ERROR_UNKNOWN;
    }

    @SuppressLint("ResourceType")
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;

        getMenuInflater().inflate(R.menu.main_menu,menu);
        downloadHdMenuItem = menu.findItem(R.id.action_download_hd);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pick_day) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"date picker");

        } else if (item.getItemId() == R.id.action_download_hd) {
            if (mNasaPicture.getMediaType().equals("image")) {

                    Uri uri = Uri.parse(mNasaPicture.getUrl());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDescription("Download image");
                    request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"download image");

                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    downloadId = downloadManager.enqueue(request);
            }

        } else if (item.getItemId() == R.id.action_share) {
            if (mNasaPicture.getMediaType().equals("image")) {
                Bitmap mBitmap = ((BitmapDrawable) mNasaPictureImageView.getDrawable()).getBitmap();

                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        mBitmap,mNasaPicture.getTitle(),null);
                Uri imageUri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                intent.putExtra(Intent.EXTRA_TEXT,mNasaPicture.getTitle());
                startActivity(Intent.createChooser(intent,"Select"));


            } else if (mNasaPicture.getMediaType().equals("video")) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                Uri uriVideo = Uri.parse(mNasaPicture.getUrl());

                intent.putExtra(Intent.EXTRA_TEXT,mNasaPicture.getTitle() + "\n" + uriVideo);
                intent.setType("text/plain");
                startActivity(intent);
            }

        } else if (item.getItemId() == R.id.action_about) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
            }



//            AboutFragment aboutFragment = new AboutFragment();
//            aboutFragment.show(getSupportFragmentManager(), aboutFragment.getTag());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view,int year,int month,int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
        requestApod(date);





    }

    private void requestApod(String date) {
        String apod = NetworkUtils.getPictureUrl(MainActivity.this, date).toString();

        JsonObjectRequest apodRequest = new JsonObjectRequest(
                Request.Method.GET,
                apod,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        NasaPicture nasaPicture = null;
                        try {

                            nasaPicture = OpenPictureDataParser.getPictureInfoObjectFromJson(response);


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
                }

        );
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
        String titleName = mNasaPicture.getTitle();
        mTitle.setText(titleName);

        String description = mNasaPicture.getExplanation();
        mDescription.setText(description);
    }

    private void updateApod(NasaPicture nasaPicture) {
        mNasaPicture = nasaPicture;
        showApod();
    }

    private void updateMenu(){
        if (mNasaPicture.getMediaType().equals("image")) {
            downloadHdMenuItem.setVisible(true);
        } else if(mNasaPicture.getMediaType().equals("video")) {
            downloadHdMenuItem.setVisible(false);
        }
    }

}

