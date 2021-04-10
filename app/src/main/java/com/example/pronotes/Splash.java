package com.example.pronotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Splash extends AppCompatActivity {
    FirebaseAuth fAuth;
    TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        fAuth = FirebaseAuth.getInstance();
        textView4 = findViewById(R.id.textView4);
        getMotive();//motivation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(haveNetwork()) {
                    Log.d("abc1", "here1");
                    //nahi hai null tho no need anyn account
                    if (fAuth.getCurrentUser() != null) {
                        Log.d("abc1", "here2");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else
                        {
                        fAuth.signOut();//bec meta data
                        Log.d("abc1", "here3");
                        fAuth.signInAnonymously()
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("abc1", "here4");
                                        if (task.isSuccessful())
                                        {
                                            Log.d("abc1", "here5");
                                            Toast.makeText(Splash.this, "Logged in With Temporary Account.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        } else
                                            {
                                            Log.d("abc1", "here6");
                                            Toast.makeText(Splash.this, "Error ! ", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }
                                });
                    }
                }
                //if no internet
                else {
                    Log.d("abc1", "here7");
                    startActivity(new Intent(getApplicationContext(), NoInternet.class));
                }
                }

        },2000);
    }
    private void getMotive() {
        List<Integer> MotiveList = new ArrayList<>();
        MotiveList.add(R.string.motive_1);
        MotiveList.add(R.string.motive_2);
        MotiveList.add(R.string.motive_3);
        MotiveList.add(R.string.motive_4);
        MotiveList.add(R.string.motive_5);
        MotiveList.add(R.string.motive_6);
        MotiveList.add(R.string.motive_7);
        MotiveList.add(R.string.motive_8);
        //obj create kiya hai
        Random randomMotive = new Random();
        Integer random = MotiveList.get(randomMotive.nextInt(MotiveList.size()-1));
        textView4.setText(random);
    }

    private boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info:networkInfos)
        {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    have_WIFI = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    have_MobileData = true;
        }
        return have_MobileData || have_WIFI;

    }
}