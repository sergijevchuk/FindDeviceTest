package com.gametab.pushCommandTest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.content.pm.*;
import java.io.*;
import android.widget.*;

public class WarrningDialog extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		String reason = getIntent().getStringExtra("message");
		if (reason == null || reason.isEmpty()) {
			reason = "Not specifiled!";
		}
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(getString(R.string.warrning));
		b.setMessage(reason);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					finish();
				}
			});
		b.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface p1)
				{
					finish();
				}
			});
		b.setCancelable(false);
		AlertDialog dd = b.create();
		Window w = dd.getWindow();
		WindowManager.LayoutParams p = w.getAttributes();
		p.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		w.setAttributes(p);
		dd.show();
	}
	
}
