package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button qrCloseBtn = findViewById(R.id.closeQr);

        boolean isFIrstTIme = getIntent().getBooleanExtra("isFistTimeLaunch", false);

        if (isFIrstTIme){
            showAlertDialog();
        }



        qrCloseBtn.setOnClickListener(_ -> {
            if (isFIrstTIme){
                Intent intent = new Intent(QrActivity.this, DataEntryActivity.class);
                intent.putExtra("isFistTimeLaunch", true);
                startActivity(intent);

            }else {
                startActivity(new Intent(QrActivity.this, MainActivity.class));
                finish();
            }
        });



    }


    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert")
                .setMessage("- This app will not properly work on huawei devices.\n - To work this app properly, you need to allow all the permissions requested by the client app " +
                        "\n - Avoid using this app for illegal activities. \n - Check about section in main menu for more details")
                .setPositiveButton("Agree", (dialog, _) -> dialog.dismiss())
                .setNegativeButton("Exit", (dialog, _) -> {
                    finishAffinity();
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}