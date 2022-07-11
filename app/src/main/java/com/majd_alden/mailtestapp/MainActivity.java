package com.majd_alden.mailtestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.services.gmail.model.Message;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static LocalServerReceiver localServerReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView fromTV = findViewById(R.id.fromTV);
        TextView fromValueTV = findViewById(R.id.fromValueTV);
        EditText toET = findViewById(R.id.toET);
        EditText messageET = findViewById(R.id.messageET);
        Button sendBtn = findViewById(R.id.sendBtn);

        String from = Utils.getEmail();
        fromValueTV.setText(from);

        sendBtn.setOnClickListener(view -> {
            Mail mail = new Mail();
            mail.set_host("smtp.gmail.com");
            mail.set_port("465");
            mail.set_sport("465");

            mail.set_from(fromValueTV.getText().toString());
            mail.set_to(new String[]{toET.getText().toString()});
            mail.set_subject("Test Message");
            mail.set_body(messageET.getText().toString());

            mail.set_debuggable(BuildConfig.DEBUG);

            HandlerThread handlerThreadSendMail = new HandlerThread("SendMail" + new Random().nextInt());
            handlerThreadSendMail.start();
            Handler handlerSendMail = new Handler(handlerThreadSendMail.getLooper());

            handlerSendMail.post(() -> {
                try {
                    Message message = mail.createMessageWithEmail();

                    String messageId = Utils.sendEmail(this, message);
                    Log.e("MTA_MainActivity_TAG", "messageId: " + message.getId() + ", messageId: " + messageId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}