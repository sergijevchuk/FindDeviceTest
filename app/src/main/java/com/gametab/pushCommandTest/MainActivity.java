package com.gametab.pushCommandTest;

import android.app.*;
import android.content.*;
import android.os.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		SharedPreferences p = getSharedPreferences("settings",MODE_PRIVATE);
		if (!p.contains("token")) {
			p.edit().putString("token","BravisA512").apply();
		}
		Intent i = new Intent(this,PushService.class);
		startService(i);
		finish();
    }
}
