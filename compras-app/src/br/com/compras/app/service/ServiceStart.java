package br.com.compras.app.service;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceStart extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
    public void onCreate() {
		super.onCreate();

		ServiceSend send = new ServiceSend(this);
		ServiceReceive receive = new ServiceReceive(this);

		new Timer().schedule(send, 0, 8 * 60 * 1000); // 8 min;
		new Timer().schedule(receive, 0, 10 * 60 * 1000); // 10 min;
    }
	
	@Override 
	public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public int onStartCommand(Intent Intention, int Flags, int StartId) {
        return START_STICKY;
    }
}
