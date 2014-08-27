package com.lovewuchin.app.archerkeyguard.settings;

import java.io.FileNotFoundException;

import com.lovewuchin.app.archerkeyguard.R;
import com.lovewuchin.app.archerkeyguard.service.KeyguardService;
import com.lovewuchin.app.archerkeyguard.util.Utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.ImageView;

public class SettingsActivity  extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener{
	
	public static final String PREFERENCE_NAME = "PREFERENCE";

	private static final int SELECT_PIC = 1;

	private static final int PHOTO_REQUEST_CUT = 100;
	
	private SharedPreferences mPrefs;
	private CheckBoxPreference mOpenCheckPreference;
	private Preference mSelectWall;
	private Intent service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		service = new Intent(this, KeyguardService.class);
		mPrefs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
		
		mSelectWall = (Preference) findPreference("key_select_back");
		mSelectWall.setOnPreferenceClickListener(this);
		
		mOpenCheckPreference = (CheckBoxPreference) findPreference("key_open_keyguard");
		mOpenCheckPreference.setChecked(mPrefs.getBoolean("keyguard_status", false));
		mOpenCheckPreference.setOnPreferenceChangeListener(this);
		
		if(mPrefs.getBoolean("keyguard_status", false)) {
			startService(service);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		String key = preference.getKey();
		if(key.equals("key_open_keyguard")) {
			mPrefs.edit().putBoolean("keyguard_status", (Boolean) newValue).commit();
			if((Boolean) newValue) {
				startService(service);
			}else if(service != null){	
				stopService(service);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		String key = preference.getKey();
		if(key.equals("key_select_back")) {
			Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");                 
			startActivityForResult(intent, SELECT_PIC);    
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK) {
		if(requestCode == SELECT_PIC) {
			Uri uri = data.getData();
			String file = Utility.getPath(getApplicationContext(), uri);
			mPrefs.edit().putString("file_path", file).commit();
			//crop(uri); 
	    }else if(requestCode == PHOTO_REQUEST_CUT && data != null && "".equals(data)) {
	    	Bitmap bitmap = (Bitmap) data.getExtras().get("data");
		    ImageView img = (ImageView) findViewById(R.id.show_pictrue);
		    img.setImageBitmap(bitmap);
	    }
		}
		super.onActivityResult(requestCode, resultCode, data);	
	}
	
	protected void crop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
 
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

}
