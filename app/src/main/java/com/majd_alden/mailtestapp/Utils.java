package com.majd_alden.mailtestapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
import com.google.api.services.gmail.model.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Utils {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private static LocalServerReceiver localServerReceiver = null;
    private static Gmail service;
    private static String email;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Context context, String personId) throws IOException {
        InputStream in = context.getAssets().open(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(context.getFilesDir())) // new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("online")
                .setApprovalPrompt("force")
                .build();
        if (localServerReceiver == null) {
            localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
        } else {
            localServerReceiver.stop();
            localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
        }

        AuthorizationCodeInstalledApp lAuthorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(flow, localServerReceiver) {
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                String url = (authorizationUrl.build());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                (context).startActivity(browserIntent);
            }
        };

        return lAuthorizationCodeInstalledApp.authorize("user");
    }

    public static String signByGoogleBtn(Context context)
            throws Exception {
        Credential credentials = getCredentials(new NetHttpTransport(), context, "me");

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        // Create the gmail API client
        service = new Gmail.Builder(new NetHttpTransport()
                , jsonFactory
                , credentials)
                .setApplicationName("MailTestApp")
                .build();


         email = service.users().getProfile("me").execute().getEmailAddress();


        return email;
    }

    public static String sendEmail(Context context, Message message)
            throws Exception {
        Credential credentials = getCredentials(new NetHttpTransport(), context, "me");

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport()
                , jsonFactory
                , credentials)
                .setApplicationName("MailTestApp")
                .build();


        message = service.users().messages().send("me", message).execute();

        return message.getId();
    }

    public static String getEmail() {
        return email;
    }
}
