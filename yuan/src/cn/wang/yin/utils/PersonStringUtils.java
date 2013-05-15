package cn.wang.yin.utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.wang.yin.personal.R;

public class PersonStringUtils extends Activity implements Serializable {
	private static SimpleDateFormat formatter = null;

	public PersonStringUtils() {
		getDateFormat();
	}

	public static SimpleDateFormat getDateFormat() {
		if (formatter == null) {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return formatter;
	}

	public static Date pareStringToDate(String dateStr) {
		getDateFormat();
		Date strtodate = null;
		try {
			strtodate = formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strtodate;
	}

	public static String pareDateToString(Date date) {
		getDateFormat();
		String strtodate = null;
		try {
			strtodate = formatter.format(date);
			// strtodate = formatter.parse (date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strtodate;
	}

	// �����ֺ���ĸ�ַ����е���ĸȥ����ֻ���������ַ���
	public static String getDigitStrByStr(String str) {
		str = str == null ? "" : str;
		StringBuffer digitStr = new StringBuffer("");
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				digitStr.append(str.charAt(i));
			}
		}
		if (digitStr.length() > 0) {
			return digitStr.toString();
		} else {
			return "";
		}

	}

	// �����ֺ���ĸ�ַ����е���ĸȥ��������int��������
	public static int getDigitByStr(String str) {
		str = str == null ? "" : str;
		StringBuffer digitStr = new StringBuffer("");
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				digitStr.append(str.charAt(i));
			}
		}
		if (digitStr.length() > 0) {
			return Integer.parseInt(digitStr.toString());
		} else {
			return 0;
		}

	}

	/**
	 * ��ȡ�Ƿ����������ӷ���
	 * 
	 * @param context
	 * @return
	 */
	public static NetworkInfo getActiveNetwork(Context context) {
		if (context == null)
			context = PersonDbUtils.getContext();
		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnMgr == null)
			return null;
		NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo();
		
		// aActiveInfo.get
		// ��ȡ�����������Ϣ
		return aActiveInfo;
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"cn.etgps.etong", 0).versionCode;
		} catch (NameNotFoundException e) {

		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"cn.etgps.etong", 0).versionName;
		} catch (NameNotFoundException e) {

		}
		return verName;
	}

	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}


}
