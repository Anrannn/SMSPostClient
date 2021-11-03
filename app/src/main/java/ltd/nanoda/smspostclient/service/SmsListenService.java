package ltd.nanoda.smspostclient.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ltd.nanoda.smspostclient.LogWriteUtil;
import ltd.nanoda.smspostclient.Message;
import ltd.nanoda.smspostclient.MyApplication;
import ltd.nanoda.smspostclient.R;
import ltd.nanoda.smspostclient.ServiceObserver;
import ltd.nanoda.smspostclient.observer.SmsDatabaseChaneObserver;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsListenService extends Service {
    private ltd.nanoda.smspostclient.observer.SmsDatabaseChaneObserver SmsDatabaseChaneObserver;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static final Uri SMS_MESSAGE_URI = Uri.parse("content://sms");

    private void registerSmsDatabaseChangeObserver(Context context) {
        try {
            SmsDatabaseChaneObserver = new SmsDatabaseChaneObserver(new Handler(), context);
            context.getContentResolver().registerContentObserver(SMS_MESSAGE_URI, true, SmsDatabaseChaneObserver);
        } catch (Throwable ignored) {
        }
    }

    private void unregisterSmsDatabaseChangeObserver(Context context) {
        try {
            context.getContentResolver().unregisterContentObserver(SmsDatabaseChaneObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogWriteUtil.write(this.getClass().getSimpleName() + "is onCreate", "AppLife");

    }


    @SuppressLint("SimpleDateFormat")
    private void beat() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://nanoda.ltd/BeatServlet")
//                        .url("http:192.168.50.13:8080/SMSServer_war_exploded/BeatServlet?flag=client")
                        .build();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                Log.e("task","task");


            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0,60000*3);


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceObserver.setIsActive(true);

        LogWriteUtil.write(this.getClass().getSimpleName() + " is onStartCommand", "AppLife");

        beat();

        //注册通知渠道
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("service", "BackgroundService", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        //创建通知
        Notification builder = new NotificationCompat.Builder(this, "service")
                .setSmallIcon(R.drawable.icon1)
                .setContentTitle("BackgroundService")
                .setContentText("Service start")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        //启动Foreground 防止ANR
        startForeground(1, builder);
        Log.e("service", "start");

        //注册观察者
        registerSmsDatabaseChangeObserver(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ServiceObserver.setIsActive(false);

        //注销观察者
        unregisterSmsDatabaseChangeObserver(this);

        LogWriteUtil.write(this.getClass().getSimpleName() + " is onDestroy", "AppLife");

    }
}