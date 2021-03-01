package com.nanang.notifyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.nanang.notifyapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "ch1";
    private static final String NOTIF_URL = "http://google.co.id";
    private static final String ACTION_UPDATE_NOTIFICATION = "com.nanang.notifyapp.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION = "com.nanang.notifyapp.ACTION_CANCEL_NOTIFICATION";
    private final NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION);
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        registerReceiver(notificationReceiver, intentFilter);
        
        binding.notifyButton.setOnClickListener(this);
        binding.updateButton.setOnClickListener(this);
        binding.cancelButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notifyButton :
                sendNotification();
                break;
            case R.id.updateButton :
                updateNotification();
                break;
            case R.id.cancelButton :
                cancelNotification();
                break;
        }
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateNotification() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Notification Title!/n")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .addAction(R.mipmap.ic_launcher, "Cancel", cancelPendingIntent)
                        .setContentIntent(cancelPendingIntent)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(image)
                                .setBigContentTitle("Notification Updated!"));
        Notification notification = notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private void sendNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notifPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent moreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NOTIF_URL));
        PendingIntent morePendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, moreIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Notification Title")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .addAction(R.mipmap.ic_launcher, "Learn more", morePendingIntent)
                        .addAction(R.mipmap.ic_launcher, "Update", updatePendingIntent)
                        .setContentIntent(notifPendingIntent);
        Notification notification = notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            CharSequence name = "Channel1";
            String desc = "Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
                case ACTION_CANCEL_NOTIFICATION:
                    cancelNotification();
                    break;
            }
        }
    }
}