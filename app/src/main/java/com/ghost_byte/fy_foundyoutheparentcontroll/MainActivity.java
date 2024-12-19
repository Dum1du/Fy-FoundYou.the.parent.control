package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DataBaseHelper dataBaseHelper;
    private AdupterMain adupterMain;
    private List<MainRecItemsData> userList;
    private ImageView menuBtn, addBtn;
    private LinearLayout sideMenu,action_bar,qrCode, about, contact;
    private View shader;
    private FrameLayout aboutApp;
    private TextView aboutTextShow;
    private Button closeAboutBtn;

    private boolean isMenuOpened = false;

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

        qrCode = findViewById(R.id.qrLayer);
        about = findViewById(R.id.aboutLayer);
        contact = findViewById(R.id.contactLayer);
        action_bar = findViewById(R.id.actionBar);
        aboutTextShow = findViewById(R.id.aboutText);
        shader = findViewById(R.id.shaderId);

        //actuall showing layer sections
        aboutApp = findViewById(R.id.aboutSection);
        closeAboutBtn = findViewById(R.id.closeAbout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuBtn = findViewById(R.id.menuBTN);
        addBtn = findViewById(R.id.addUserBTN);
        sideMenu = findViewById(R.id.sideMenu);

       // String hardcodedHtml = "This is a <b>bold</b> word.<br />This is a new line.";
        aboutTextShow.setText(Html.fromHtml(getString(R.string.about), Html.FROM_HTML_MODE_LEGACY));

        Log.d("HTMLString", Html.fromHtml(getString(R.string.about), Html.FROM_HTML_MODE_LEGACY).toString());
        aboutTextShow.setMovementMethod(LinkMovementMethod.getInstance());

        //setcolor - statusbar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.actionBarColor));

        dataBaseHelper = new DataBaseHelper(this);
        userList = new ArrayList<>();

        loadData();

        adupterMain = new AdupterMain(userList, this);
        recyclerView.setAdapter(adupterMain);

        addBtn.setOnClickListener(V -> addActivity(addBtn));
        qrCode.setOnClickListener(V -> showQr(qrCode));
        about.setOnClickListener(V -> showAbout(about));
        contact.setOnClickListener(V -> contactMethod(contact));
        closeAboutBtn.setOnClickListener(V -> closeAboutSection(closeAboutBtn));

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpened == false){
                    showMenu(sideMenu);
                    shader.setVisibility(View.VISIBLE);
                    isMenuOpened = true;

                    return;
                }

                if (isMenuOpened == true){
                    hideMenu(sideMenu);
                    shader.setVisibility(View.GONE);
                    isMenuOpened = false;

                    return;
                }
            }
        });

        shader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu(sideMenu);
                shader.setVisibility(View.GONE);
                isMenuOpened = false;

                return;
            }
        });

        //backpree event control
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

    }

    private void closeAboutSection(Button closeAboutBtn) {
        aboutApp.setVisibility(View.GONE);
    }

    private void contactMethod(View view) {
        Toast.makeText(MainActivity.this,"contact pressed", Toast.LENGTH_LONG).show();
    }

    private void showAbout(View view) {
        aboutApp.setVisibility(View.VISIBLE);
        if (isMenuOpened == true){
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;

            return;
        }

    }

    private void showQr(View view) {
        Toast.makeText(MainActivity.this,"QR pressed", Toast.LENGTH_LONG).show();
    }

    private void addActivity(View view) {
        if (isMenuOpened == true){
            hideMenu(sideMenu);
            shader.setVisibility(View.GONE);
            isMenuOpened = false;
        }

        PrefManager prefManager = new PrefManager(this);
        prefManager.setFirstTimeLaunch(true);
        startActivity(new Intent(MainActivity.this, DataEntryActivity.class));
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
}
