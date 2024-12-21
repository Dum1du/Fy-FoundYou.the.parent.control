package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataBaseHelper dataBaseHelper;
    private List<MainRecItemsData> userList;
    private ImageView addBtn;
    private LinearLayout sideMenu;
    private LinearLayout about;
    private LinearLayout help;
    private View shader;
    private FrameLayout aboutApp, helpApp;
    private Button closeAboutBtn, closeHelpBtn;

    private boolean isMenuOpened = false;

    //update related
    private static final String TAG = "AppUpdate";
    private FirebaseFirestore db;
    private String downloadUrl; // APKPure download URL
    private boolean isMandatoryUpdate;
    //private static final int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        closeHelpBtn = findViewById(R.id.closeHelp);
        TextView helpTextShow = findViewById(R.id.helpText);
        helpApp = findViewById(R.id.helpSection);
        LinearLayout qrCode = findViewById(R.id.qrLayer);
        about = findViewById(R.id.aboutLayer);
        help = findViewById(R.id.helpLayer);
        TextView aboutTextShow = findViewById(R.id.aboutText);
        shader = findViewById(R.id.shaderId);

        //actual showing about layer sections
        aboutApp = findViewById(R.id.aboutSection);
        closeAboutBtn = findViewById(R.id.closeAbout);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView menuBtn = findViewById(R.id.menuBTN);
        addBtn = findViewById(R.id.addUserBTN);
        sideMenu = findViewById(R.id.sideMenu);

       // String hardcodedHtml = "This is a <b>bold</b> word.<br />This is a new line.";
        aboutTextShow.setText(Html.fromHtml(getString(R.string.about), Html.FROM_HTML_MODE_LEGACY));

        Log.d("HTMLString", Html.fromHtml(getString(R.string.about), Html.FROM_HTML_MODE_LEGACY).toString());
        aboutTextShow.setMovementMethod(LinkMovementMethod.getInstance());

        // String hardcodedHtml = "This is a <b>bold</b> word.<br />This is a new line.";
        helpTextShow.setText(Html.fromHtml(getString(R.string.help), Html.FROM_HTML_MODE_LEGACY));

        Log.d("HTMLString", Html.fromHtml(getString(R.string.help), Html.FROM_HTML_MODE_LEGACY).toString());
        helpTextShow.setMovementMethod(LinkMovementMethod.getInstance());

        //setcolor - statusbar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.actionBarColor));

        dataBaseHelper = new DataBaseHelper(this);
        userList = new ArrayList<>();

        loadData();

        AdupterMain adupterMain = new AdupterMain(userList, this);
        recyclerView.setAdapter(adupterMain);

        addBtn.setOnClickListener(_ -> addActivity(addBtn));
        about.setOnClickListener(_ -> showAbout(about));
        help.setOnClickListener(_ -> helpMethod(help));
        closeAboutBtn.setOnClickListener(_ -> closeAboutSection(closeAboutBtn));
        closeHelpBtn.setOnClickListener(_ -> closeHelpSection(closeHelpBtn));

        PrefManager prefManager = new PrefManager(this);

        if (prefManager.isFirstTimeLaunch()){
            prefManager.setFirstTimeLaunch(false);
            Intent intent = new Intent(MainActivity.this, QrActivity.class);
            intent.putExtra("isFistTimeLaunch", true);
            startActivity(intent);
            finish();
            return;
        }

        qrCode.setOnClickListener(_ -> {
            if (isMenuOpened){
                hideMenu(sideMenu);
                shader.setVisibility(View.GONE);
                isMenuOpened = false;
            }

            Intent intent = new Intent(MainActivity.this, QrActivity.class);
            intent.putExtra("isFistTimeLaunch", false);
            startActivity(intent);
        });

        menuBtn.setOnClickListener(_ -> {
            if (!isMenuOpened){
                showMenu(sideMenu);
                shader.setVisibility(View.VISIBLE);
                isMenuOpened = true;

                return;
            }

            if (isMenuOpened){
                hideMenu(sideMenu);
                shader.setVisibility(View.GONE);
                isMenuOpened = false;

            }
        });

        shader.setOnClickListener(_ -> {
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;

        });



        //backpree event control
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

        db = FirebaseFirestore.getInstance();

        checkForUpdates();

    }

    private void closeHelpSection(Button closeHelpBtn) {
        helpApp.setVisibility(View.GONE);
    }

    private void closeAboutSection(Button closeAboutBtn) {
        aboutApp.setVisibility(View.GONE);
    }

    private void helpMethod(View view) {

        helpApp.setVisibility(View.VISIBLE);
        if (isMenuOpened == true){
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;

            return;
        }

    }

    private void showAbout(View view) {
        aboutApp.setVisibility(View.VISIBLE);
        if (isMenuOpened){
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;

        }

    }


    private void addActivity(View view) {
        if (isMenuOpened){
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;
        }
        Intent intent = new Intent(MainActivity.this, DataEntryActivity.class);
        intent.putExtra("isFistTimeLaunch", false);
        startActivity(intent);
        finish();

    }

    private void loadData() {
        Cursor cursor = dataBaseHelper.getAllData();

        if (cursor != null){

                if (cursor.moveToFirst()){
                    do {
                        String nickname = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_NAME));
                        String unique = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.UNIQUE_CODE));

                        userList.add(new MainRecItemsData(unique,nickname));
                    }while (cursor.moveToNext());
                }
            }

        assert cursor != null;
        cursor.close();

        }

    public void showMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(sideMenu,"translationX", 500f,0f);
        animator.setDuration(300);
        animator.start();
    }
    public void hideMenu(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(sideMenu,"translationX", 0f,500f);
        animator.setDuration(300);
        animator.start();
    }

    private void checkForUpdates() {
        db.collection("app_updates")
                .document("latest_update")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double latestVersion = documentSnapshot.getDouble("version_code").doubleValue();
                        downloadUrl = documentSnapshot.getString("download_url");
                        isMandatoryUpdate = documentSnapshot.getBoolean("mandatory");

                        // Compare with the current version

                        double currentVersion = BuildConfig.VERSION_CODE;
                        if (latestVersion > currentVersion) {
                            showUpdatePopup();
                        } else {
                            Log.d(TAG, "No updates available.");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch update info", e));
    }

    private void showUpdatePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version of the app is available. Please update to the latest version.");

        // Add Update button
        builder.setPositiveButton("Update", (dialog, _) -> {
            dialog.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                downloadUpdate();
            }
        });

        // Add Later button (only for non-mandatory updates)
        if (!isMandatoryUpdate) {
            builder.setNegativeButton("Later", (dialog, _) -> dialog.dismiss());
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(!isMandatoryUpdate);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void downloadUpdate() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (downloadManager == null) {
            Log.e(TAG, "DownloadManager not available");
            return;
        }

        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Downloading Update");
        request.setDescription("Please wait while the update is downloaded.");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "app_update.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadId = downloadManager.enqueue(request);

        // Monitor download completion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        Log.d(TAG, "Download completed");
                        installUpdate();
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    private void installUpdate() {
        File apkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app_update.apk");
        Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
