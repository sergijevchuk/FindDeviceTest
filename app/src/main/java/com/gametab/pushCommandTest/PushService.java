package com.gametab.pushCommandTest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.util.*;
import java.util.*;
import java.net.*;
import java.io.*;
import android.widget.*;
import org.json.*;
import java.util.concurrent.*;

public class PushService extends Service
{
	//private String lastJsonOuptput;
	private String url = "http://192.168.1.6:8080/find/device/getLastCommand.php";
	private String KEY = "com.gametab.pushCommandTest.PushService";
	private String token,theLastCommand,theLastArgs;
	private Handler h;
	private SharedPreferences settings;
	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void onCreate()
	{
		settings = getSharedPreferences("settings",MODE_PRIVATE);
		token = settings.getString("token","empty");
		theLastCommand = settings.getString("lastCommand","");
		theLastArgs = settings.getString("theLastArgs","");
		Log.i(KEY,"The service started! The device token is: " + token);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		h = new Handler() {
			public void handleMessage(Message msg) {
				Check c = new Check();
				c.execute(new String[] {url,"token=" + token});
			}
		};
		h.sendEmptyMessage(0);
		return START_STICKY;
	}
	private class Check extends AsyncTask<String,Void,String>
	{

		@Override
		protected String doInBackground(String... p1)
		{
			try {
				return Utils.connect("POST",p1[0],p1[1]);
			} catch (IOException e) {
				return "Error: " + e.toString();
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (result != null && !result.isEmpty()) {
				if (process(result)) {
					settings.edit().putString("lastCommand",theLastCommand).apply();
					settings.edit().putString("theLastArgs",theLastArgs).apply();
					h.sendEmptyMessage(0);
				} else {
					//failed to process ignore
					h.sendEmptyMessage(0);
				}
			} else {
				Toast.makeText(getApplicationContext(),"The result is empty ignoring!",0).show();
				h.sendEmptyMessage(0);
			}
		}
		private boolean process(String result) {
			try {
				JSONObject ob = new JSONObject(result);
				if (ob.getInt("code") == 200) {
					String command = ob.getString("command");
					if (command.equals(theLastCommand) && ob.getString("params").equals(theLastArgs)) {
						return true;
					} else {
						theLastCommand = command;
						theLastArgs = ob.getString("params");
					}
					String[] params = ob.getString("params").split(";");
					switch(command) {
						case "lock":
							Log.i(KEY,"Activiting lost mode reason:" + params[0]);
							Intent i = new Intent(getApplicationContext(),StartLockServiceActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("lockreason",params[0]);
							startActivity(i);
							break;
						case "print_message":
							Toast.makeText(getApplicationContext(),params[0],0).show();
							break;
						case "format":
							Log.i(KEY,"Starting format process....");
							Utils.wipe(getApplicationContext());
							break;
						case "reboot":
							Utils.reboot(getApplicationContext());
							break;
						case "dialog":
							startActivity(new Intent(getApplicationContext(),WarrningDialog.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("message",params[0]));
							break;
					}
				} else {
					return false;
				}
			} catch (JSONException e) {
				Log.e(KEY,"The result is not a json, it a server error! Error: " + result);
				return false;
			}
			return true;
		}
	}

	@Override
	public void onDestroy()
	{
		Log.i(KEY,"The service destroyed! Saving the last command!");
		settings.edit().putString("lastCommand",theLastCommand).apply();
		settings.edit().putString("theLastArgs",theLastArgs).apply();
		Log.i(KEY,"Saved stopping normally!");
		super.onDestroy();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		stopSelf();
		super.onTaskRemoved(rootIntent);
	}
	
}
