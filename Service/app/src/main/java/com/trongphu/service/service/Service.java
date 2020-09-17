package com.trongphu.service.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Service extends android.app.Service {
    @Override
    public void onCreate() {
        Toast.makeText(this,"onCreate",Toast.LENGTH_SHORT).show();
        Log.d("phunt","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"onStartCommand",Toast.LENGTH_SHORT).show();
        Log.d("phunt","onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("phunt","onDestroy");
        Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
        return null;
    }


}
