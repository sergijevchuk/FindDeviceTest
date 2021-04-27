package com.gametab.pushCommandTest;
import android.content.*;
import android.widget.*;

public class BootRece extends BroadcastReceiver
{

	@Override
	public void onReceive(Context p1, Intent p2)
	{
		Toast.makeText(p1,"Runned!",0).show();
			Intent i = new Intent(p1,MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			p1.startActivity(i);
		}

}
