package com.gametab.pushCommandTest;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class ProgressDialogActivity extends Activity
{
	private Handler h;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final SharedPreferences p = getSharedPreferences("settings",MODE_PRIVATE);
		final ProgressDialog d = new ProgressDialog(this);
		String msg = getIntent().getStringExtra("message");
		if (msg == null || msg.isEmpty()) {
			msg = "Not specifiled";
		}
		d.setMessage(msg);
		d.setCancelable(false);
		Window w = d.getWindow();
		WindowManager.LayoutParams pr = w.getAttributes();
		pr.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		w.setAttributes(pr);
		d.show();
		h = new Handler() {
			public void handleMessage(Message msg) {
				if (p.getBoolean("finishDialog",false)) {
					d.dismiss();
					p.edit().remove("finishDialog").apply();
					finish();
				} else {
					h.sendEmptyMessage(0);
				}
			}
		};
		h.sendEmptyMessage(0);
	}
	
}
