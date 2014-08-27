package com.lovewuchin.app.archerkeyguard;

import java.util.Calendar;

import com.lovewuchin.app.archerkeyguard.settings.SettingsActivity;
import com.lovewuchin.app.archerkeyguard.util.LockLayer;
import com.lovewuchin.app.archerkeyguard.util.Utility;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements SwipeBackActivityBase {

	private SwipeBackLayout mSwipeBackLayout;
	private SwipeBackActivityHelper mSwipeBackHelper;
	private LockLayer lockLayer;
	
	private SharedPreferences mPrefs;
	private ImageView mShowPic;
	private TextView mTimeView;
	private TextView mAreaView;
	private TextView mDateView;
	private TextView mNoticeView;
	private Calendar mCalendar;
	public BroadcastReceiver mIntentReceiver; 
	
	private String mDateFormat;
	private String mFormat; 
	private final static String M12 = "h:mm";  
    private final static String M24 = "kk:mm";  
	
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //initialization swipebacklayout
        mSwipeBackHelper = new SwipeBackActivityHelper(this);
        mSwipeBackHelper.onActivityCreate();

        //initialization locklayer
        View lock = View.inflate(this, R.layout.main, null);  
        lockLayer = new LockLayer(this);  
        lockLayer.setLockView(lock);  
        lockLayer.lock(); 
        
        setContentView(R.layout.activity_main);
        lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lockLayer.unlock();
			}
		});
        
        mPrefs = getSharedPreferences(SettingsActivity.PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
        
        Utility.enableTint(this);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        mSwipeBackLayout.setEdgeSize(1280);
        
        mShowPic = (ImageView) findViewById(R.id.show_pictrue);
        mShowPic.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(mPrefs.getString("file_path", "/storage/sdcard0/aria.jpg"))));
        
        mTimeView = (TextView) findViewById(R.id.text_time);
        mAreaView = (TextView) findViewById(R.id.text_area);
        mDateView = (TextView) findViewById(R.id.text_date);
        mNoticeView = (TextView) findViewById(R.id.text_notice);
        
        mDateFormat = getString(R.string.month_day_year);
        mCalendar = Calendar.getInstance();
        
        setDateFormat();        
        registerComponent();
    }
    
    private void setDateFormat() {
		// TODO Auto-generated method stub
    	mFormat = android.text.format.DateFormat.is24HourFormat(this)  
                ? M24 : M12;  
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onPostCreate(savedInstanceState);
    	mSwipeBackHelper.onPostCreate();
    }
    
    //disable back&volume+- key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	return disableKeycode(keyCode, event);
    }


	private boolean disableKeycode(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int key = event.getKeyCode();
		switch(key) {
		   case KeyEvent.KEYCODE_BACK : return true;
		   case KeyEvent.KEYCODE_VOLUME_DOWN : return true;
		   case KeyEvent.KEYCODE_VOLUME_UP : return true;
		   case KeyEvent.KEYCODE_HOME : return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		// TODO Auto-generated method stub
		return mSwipeBackHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		// TODO Auto-generated method stub
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		// TODO Auto-generated method stub
		Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
	}
	
	public void registerComponent() {
		 if (mIntentReceiver == null) {  
	            mIntentReceiver = new TimeChangedReceiver();  
	            IntentFilter filter = new IntentFilter();  
	            filter.addAction(Intent.ACTION_TIME_TICK);  
	            filter.addAction(Intent.ACTION_TIME_CHANGED);  
	            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);  
	            registerReceiver(mIntentReceiver, filter);  
	        }  
		 updateTime();
	}
	
	public void unregisterComponent() {
		   if (mIntentReceiver != null) {  
	            unregisterReceiver(mIntentReceiver);  
	        }  
		   mIntentReceiver = null;  
	}

	public void updateTime() {
		// TODO Auto-generated method stub
		 mCalendar.setTimeInMillis(System.currentTimeMillis());  
		  
	     CharSequence newTime = DateFormat.format(mFormat, mCalendar);  
	     mTimeView.setText(newTime);  
	}
	
	private class TimeChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 final boolean timezoneChanged =  
	                    intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED);  
			 new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateTime();
				}
			});
		}
		
	};
}
