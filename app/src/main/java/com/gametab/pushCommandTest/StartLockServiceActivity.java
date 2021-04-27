package com.gametab.pushCommandTest;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;

public class StartLockServiceActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		final WindowManager mgr = (WindowManager) getSystemService(WINDOW_SERVICE);
		final View v = getLayoutInflater().inflate(R.layout.lockview,null,false);
		TextView t = v.findViewById(R.id.lockviewTextView2);
		Button b = v.findViewById(R.id.lockviewButton1);
		String reason = getIntent().getStringExtra("lockreason");
		if (reason == null || reason.isEmpty()) {
			t.setText("Reason Not specifiled!");
		} else {
			t.setText("The reason: " + reason);
		}
		b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					mgr.removeViewImmediate(v);
					finish();
				}
			});
		int type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		}
		WindowManager.LayoutParams p = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,type,WindowManager.LayoutParams.FLAG_FULLSCREEN,PixelFormat.TRANSPARENT);
		mgr.addView(v,p);
	}
	
}
