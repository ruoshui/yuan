package cn.wang.yin.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.CollectGpsUtil;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonDbUtils;
import cn.wang.yin.utils.SIMCardInfo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public String mtag = "MainActivity";
	Button button1;
	public static TextView textView1;
	int i = 0;
	public static final int FAIL = 0;
	public static final int SUCCESS = 1;
	final Timer timer = new Timer();
	TimerTask task;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	TelephonyManager telephonyManager;

	// private static List<String> listTmp = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PersonDbUtils.init(
				getApplicationContext(),
				getSharedPreferences(PersonConstant.USER_AGENT_INFO,
						Context.MODE_PRIVATE));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// button1 = (Button) findViewById(R.id.button1);
		textView1 = (TextView) findViewById(R.id.textView1);
		// SIMCardInfo.init(getApplicationContext());
		handler.post(runnnable);
		// button1.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getApplicationContext(),
		// PersonService.class);
		// stopService(intent);
		// textView1.setText(textView1.getText() + "\n" + "ֹͣ����");
		// }
		// });
		// float fv =
		// Float.valueOf(android.os.Build.VERSION.RELEASE.substring(0,
		// 3).trim());
		// if (fv > 2.3) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork() //
		// ��������滻ΪdetectAll()
		// // �Ͱ����˴��̶�д������I/O
		// .penaltyLog() // ��ӡlogcat����ȻҲ���Զ�λ��dropbox��ͨ���ļ�������Ӧ��log
		// .build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects() // ̽��SQLite���ݿ����
		// .penaltyLog() // ��ӡlogcat
		// .penaltyDeath().build());
		// }
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		// option.setScanSpan(5000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(true);// ��ֹ���û��涨λ
		option.setPoiNumber(5); // ��෵��POI����
		option.setPoiDistance(1000); // poi��ѯ����
		option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
		mLocationClient.setLocOption(option);

		System.out.println(android.os.Build.VERSION.RELEASE);

		SIMCardInfo sci = new SIMCardInfo(getApplicationContext());
		textView1.setText(textView1.getText() + "\n" + "�ֻ����룺"
				+ sci.getNativePhoneNumber() + "\n" + sci.getProvidersName());
		telephonyManager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();
		sb.append(telephonyManager.getDeviceId() + "\n");
		sb.append(telephonyManager.getCallState() + "\n");
		sb.append(telephonyManager.getDeviceSoftwareVersion() + "\n");
		sb.append(telephonyManager.getLine1Number() + "\n");
		sb.append(telephonyManager.getPhoneType() + "\n");
		sb.append(telephonyManager.getSubscriberId() + "\n");
		textView1.setText(textView1.getText() + "\n" + sb.toString());
	}

	Runnable runnnable = new Runnable() {
		@Override
		public void run() {
			// Intent intent = new Intent(getApplicationContext(),
			// PersonService.class);
			// startService(intent);
			handler.sendEmptyMessage(0);

		}
	};

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// /��ʱִ������
			// ////////////////////////////////////////////////////////////////
			// NetworkInfo netWork = EtongStringUtils
			// .getActiveNetwork(getApplicationContext());
			switch (msg.what) {
			case 1:
				if (mLocationClient != null && mLocationClient.isStarted())
					mLocationClient.requestLocation();
				textView1.setText(textView1.getText() + "\n" + "��λ" + "\n"
						+ "------------------------------");
				Log.i(mtag, "��λ");
				CollectGpsUtil.uploadGps();
				// Log.i(mtag, "�ϴ�" + "\t");
				// textView1.setText(textView1.getText() + "\n" + "�ϴ�" + "\n"
				// + "------------------------------");
				break;
			case 2:
				String str = (String) textView1.getText();
				if (str.split("\n").length > 30) {
					textView1.setText("");
				}
				textView1.setText(textView1.getText() + "\n"
						+ msg.obj.toString() + "\n"
						+ "------------------------------");
				Log.i(mtag, textView1.getText().toString());
				break;
			case 3:
				BDLocation location = (BDLocation) msg.obj;
				CollectGpsUtil.saveGps(location);
				textView1.setText(textView1.getText() + "\n" + "�洢" + "\n"
						+ "------------------------------");
				Log.i(mtag, textView1.getText().toString());
				break;
			case 4:
				break;
			}

		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, PersonConstant.WAIT_TIMS, PersonConstant.WAIT_TIMS);
		super.onResume();
	}

	@Override
	protected void onStart() {
		mLocationClient.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			Message message = new Message();
			message.what = 2;
			message.obj = sb;
			handler.sendMessage(message);
			Message msg = new Message();
			msg.what = 3;
			msg.obj = location;
			handler.sendMessage(msg);

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}

		}
	}
}
