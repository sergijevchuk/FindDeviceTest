package com.gametab.pushCommandTest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.util.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.http.*;
import android.widget.*;
import org.json.*;
import java.util.concurrent.*;

public class PushService extends Service
{
	//private String lastJsonOuptput;
	private String url = "http://192.168.1.6:8080/find/device/getLastCommand.php";
	private String KEY = "com.gametab.pushCommandTest.PushService";
	private String token,theLastCommand;
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
		Log.i(KEY,"The service started! The device token is: " + token);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		h = new Handler() {
			public void handleMessage(Message msg) {
				Check c = new Check();
				c.execute(new String[] {url,token});
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
				URL urlC = new URL(p1[0]);
				HttpURLConnection con = (HttpURLConnection) urlC.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				con.addRequestProperty("token",p1[1]);
				int responseCode = con.getResponseCode();
				StringBuffer sb = new StringBuffer();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line;
					while((line = read.readLine()) != null) {
						sb.append(line);
					}
				} else {
					throw new IOException("Error " + responseCode);
				}
				return sb.toString();
			} catch (IOException e) {
				return "Internal error: " + e.toString();
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (process(result)) {
				h.sendEmptyMessage(0);
			} else {
				Log.i(KEY,"The service has been disabled by process function stopping!");
				stopSelf();
			}
		}
		private boolean process(String result) {
			try {
				JSONObject ob = new JSONObject(result);
				if (ob.getInt("code") == 200) {
					String command = ob.getString("command");
					if (command.equals(theLastCommand)) {
						return true;
					} else {
						theLastCommand = command;
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
					}
				} else {
					String error = ob.getString("error");
					Toast.makeText(getApplicationContext(),"The server error: " + error + ". The function has been disabled!",0).show();
					return false;
				}
			} catch (JSONException e) {
				Log.e(KEY,"The result is not a json, it a server error!");
				Toast.makeText(getApplicationContext(),"Failed to connect to server, or server error!",0).show();
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
		Log.i(KEY,"Saved stopping normally!");
		super.onDestroy();
	}
	
}
