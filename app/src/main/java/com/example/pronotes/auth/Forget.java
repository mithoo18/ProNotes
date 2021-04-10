package com.example.pronotes.auth;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pronotes.MainActivity;
import com.example.pronotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import pl.droidsonroids.gif.GifImageView;

public class Forget extends AppCompatActivity {
    Button generate;
    EditText emailFor;
    FirebaseAuth fAuth;
    ProgressBar spinner;
    GifImageView mail;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_forget);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Forget Password");

                mail= findViewById(R.id.mail);
                spinner = findViewById(R.id.progressBar3);

                fAuth = FirebaseAuth.getInstance();

                generate = findViewById(R.id.generate);
                emailFor = findViewById(R.id.emailFor);

                final Handler handler = new Handler();
                mail.setVisibility(View.GONE);

                generate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.setVisibility(View.VISIBLE);
                        mail.setVisibility(View.GONE);
                        fAuth.sendPasswordResetEmail(emailFor.getText().toString())//main logic
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        spinner.setVisibility(View.GONE);
                                        mail.setVisibility(View.VISIBLE);//gif
                                        //handler for delay so that i can run my animation
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Forget.this, "Mail Is Send", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), Login.class));
                                                }
                                        }, 2500);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Forget.this, "Mail Is Not Send " + e, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                        spinner.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }
    //back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}
