package com.majd_alden.mailtestapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        findViewById(R.id.signByGoogleBtn).setOnClickListener(view -> {
            HandlerThread handlerThreadSignByGoogle = new HandlerThread("SignByGoogle" + new Random().nextInt());
            handlerThreadSignByGoogle.start();
            Handler handlerSignByGoogle = new Handler(handlerThreadSignByGoogle.getLooper());

            handlerSignByGoogle.post(() -> {
                try {
                    String email = Utils.signByGoogleBtn(this);
                    Log.e("MTA_SignActivity_TAG", "email: " + email);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


}