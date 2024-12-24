package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataEntryActivity extends AppCompatActivity {
    private DataBaseHelper dataBaseHelper;
    private EditText name, uniqueCode;
    private Button saveBTN;
    private PrefManager prefManager;
    private FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.loadingIcon);
        prefManager = new PrefManager(this);
        dataBaseHelper = new DataBaseHelper(this);
        name = findViewById(R.id.userNAmeEditText);
        uniqueCode = findViewById(R.id.uniqueCOdeEditText);
        saveBTN = findViewById(R.id.addUserBTN);

        boolean isFIrstTIme = getIntent().getBooleanExtra("isFistTimeLaunch", false);



        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (isFIrstTIme){
                    finishAffinity();
                }else {
                    startActivity(new Intent(DataEntryActivity.this, MainActivity.class));
                }

            }
        });

        // Save users
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String uniqueFb = uniqueCode.getText().toString().trim();
                if (uniqueFb.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DataEntryActivity.this, "UniqueCode can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("locationInfo")
                        .document(uniqueFb)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()) {
                                    saveUser();
                                    startActivity(new Intent(DataEntryActivity.this, MainActivity.class));
                                    progressBar.setVisibility(View.GONE);
                                    finish();

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(DataEntryActivity.this, "Please check your UNIQUE CODE again!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.w("Firestore", "Error checking UniqueCode", task.getException());
                                Toast.makeText(DataEntryActivity.this, "Error checking UniqueCode", Toast.LENGTH_LONG).show();
                            }
                        });
            }

            private void saveUser() {
                String userName = name.getText().toString().trim();
                String unique = uniqueCode.getText().toString().trim();

                if (userName.isEmpty() || unique.isEmpty()) {
                    Toast.makeText(DataEntryActivity.this, "Nickname and UniqueCode can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                long result = dataBaseHelper.insertUser(userName, unique);

                if (result != -1) {
                    Log.d("sql", "data saved successfully");
                    name.setText("");
                    uniqueCode.setText("");
                } else {
                    Log.d("sql", "Error saving user");
                }
            }
        });
    }
}
