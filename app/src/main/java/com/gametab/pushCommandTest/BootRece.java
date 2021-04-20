package com.gametab.pushCommandTest;
import android.content.*;

public class BootRece extends BroadcastReceiver
{

	@Override
	public void onReceive(Context p1, Intent p2)
	{
		if (p2.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			p1.startService(new Intent(p1,PushService.class));
		}
	}

}
