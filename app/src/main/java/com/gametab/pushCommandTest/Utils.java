package com.gametab.pushCommandTest;
import android.content.*;
import android.os.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Utils
{
	private static int DEFAULT_WIPE_TIME = 20;
	private static int DEFAULT_WIPE_TIME_IN_MS = 20000;
	public static void wipe(final Context c) {
		final SharedPreferences p = c.getSharedPreferences("settings",Context.MODE_PRIVATE);
		Intent i = new Intent(c,WarrningDialog.class);
		i.putExtra("message",String.format(c.getString(R.string.formatText),DEFAULT_WIPE_TIME));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(i);
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				Intent i = new Intent(c,ProgressDialogActivity.class);
				i.putExtra("message","Wipping...");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				c.startActivity(i);
				t.schedule(new TimerTask() {
					public void run() {
						p.edit().putBoolean("finishDialog",true).apply();
						reboot(c);
					}
				},5000);
			}
		},DEFAULT_WIPE_TIME_IN_MS);
	}
	public static void reboot(Context c) {
		PowerManager m = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock l = m.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyTag");
		l.acquire();
		l.release();
		try {
			m.reboot(null);
		} catch (Exception e) {
			Intent i = new Intent(c,WarrningDialog.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("message","Failed to reboot!");
			c.startActivity(i);
		}
	}
	public static String connect(String method,String url,String arg) throws IOException {
		URL urlC = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlC.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod(method);
		OutputStream out = con.getOutputStream();
		out.write(arg.getBytes());
		out.flush();
		out.close();
		int responseCode = con.getResponseCode();
		StringBuffer sb = new StringBuffer();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while((line = read.readLine()) != null) {
				sb.append(line);
			}
			con.disconnect();
		} else {
			throw new IOException("Error " + responseCode);
		}
		return sb.toString();
	}
}
