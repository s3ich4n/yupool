package com.cs2017.yupool.Login.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginUtil {

	public static String splitData(String s, int n){
		String[] split = s.split(":");
		if(split.length>n) return split[n];
		return "Split Error";
	}

	public static Bitmap getBase64Image(String src, String lectureCookie){
		URL url;
		HttpURLConnection connection = null;
		Bitmap image = null;
		try {
			
			String cookies = lectureCookie;
			url = new URL(src);
			connection  = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36");
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Referer", src);
			connection.setRequestProperty("Cookie", cookies.substring(1, cookies.length()-1).replaceAll(",", ";"));

			image = BitmapFactory.decodeStream(connection.getInputStream());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
