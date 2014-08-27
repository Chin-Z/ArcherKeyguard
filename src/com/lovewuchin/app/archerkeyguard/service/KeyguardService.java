package com.lovewuchin.app.archerkeyguard.service;

import com.lovewuchin.app.archerkeyguard.MainActivity;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class KeyguardService  extends Service{
	
	private Intent mKeyguardIntent = null;
	private KeyguardManager mKeyguardManager = null;
	private KeyguardLock mKeyguardLock = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mKeyguardIntent = new Intent(KeyguardService.this, MainActivity.class);
		mKeyguardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		registerComponent();
	}
	
	private void registerComponent() {
		// TODO Auto-generated method stub
		 IntentFilter mScreenOnOrOffFilter = new IntentFilter();  
	     mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_ON");  
	     mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_OFF");  
	     this.registerReceiver(mScreenReceiver, mScreenOnOrOffFilter);  
	}
	
	private void unregisterComponent() {
		if (mScreenReceiver != null)  
        {  
            this.unregisterReceiver(mScreenReceiver);  
        }  
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterComponent();
		startService(new Intent(KeyguardService.this, KeyguardService.class));
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("android.intent.action.SCREEN_ON") || intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
				mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);  
                mKeyguardLock = mKeyguardManager.newKeyguardLock("Archer");  
                mKeyguardLock.disableKeyguard();  
                startActivity(mKeyguardIntent); 
			}
		}
		
	};
}
