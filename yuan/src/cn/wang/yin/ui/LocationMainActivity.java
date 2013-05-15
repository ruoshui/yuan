package cn.wang.yin.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.wang.yin.personal.R;
import cn.wang.yin.personal.service.HandlerService;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonDbUtils;
import cn.wang.yin.utils.PersonStringUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.location.LocationClient;

@SuppressLint("NewApi")
public class LocationMainActivity extends Activity {
	public static String mtag = "MainActivity";
	Button button1;
	public static TextView textView1;
	int i = 0;
	public static final int FAIL = 0;
	public static final int SUCCESS = 1;
	public static final int SAVE = 3;
	final Timer timer = new Timer();
	TimerTask task;
	Timer uploadTimer = new Timer();
	TimerTask uploadTask;
	SeekBar seekBar1;
	SeekBar seekBar2;
	TextView seekBar2_textView;
	TextView seekBar1_textView;
	public static LocationClient mLocationClient = null;
	TelephonyManager telephonyManager;
	public static long locationTime = PersonConstant.WAIT_TIMS;
	public static long uploadTime = PersonConstant.UPLOAD_TIMS;
	private static final String TAG = "PushDemoActivity";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	public static final String EXTRA_MESSAGE = "message";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PersonDbUtils.init(
				getApplicationContext(),
				getSharedPreferences(PersonConstant.USER_AGENT_INFO,
						Context.MODE_PRIVATE));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView1 = (TextView) findViewById(R.id.textView1);
		seekBar1_textView = (TextView) findViewById(R.id.seekBar1_textView);
		seekBar2_textView = (TextView) findViewById(R.id.seekBar2_textView);
		findInfo();
		// push("��ʼ��������");
		startService(new Intent(getApplicationContext(), HandlerService.class));
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progress = progress > 0 ? progress : 1;
				if (fromUser) {
					locationTime = 1000 * progress;
				}
				// Intent intent = new Intent(
				// "cn.wang.yin.personal.service.PushMessageReceiver");
				// intent.putExtra("tab", 1);
				// sendBroadcast(intent);
				seekBar1_textView.setText("��λ���Ϊ��" + locationTime / 1000 + "��");
			}
		});
		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progress = progress > 0 ? progress : 1;
				if (fromUser) {
					uploadTime = 1000 * progress;
				}
				seekBar2_textView.setText("�ϴ����Ϊ��" + uploadTime / 1000 + "��");

			}
		});
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY, PersonConstant.API_KEY);
		// PushConstants.restartPushService(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	/**
	 * ����Intent
	 * 
	 * @param intent
	 *            intent
	 */
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Handle intent: \r\n" + intent);

		if (ACTION_RESPONSE.equals(action)) {

			String method = intent.getStringExtra(RESPONSE_METHOD);

			if (PushConstants.METHOD_BIND.equals(method)) {
				Log.d(TAG, "Handle bind response");
				String toastStr = "";
				int errorCode = intent.getIntExtra(RESPONSE_ERRCODE, 0);
				if (errorCode == 0) {
					String content = intent.getStringExtra(RESPONSE_CONTENT);
					String appid = "";
					String channelid = "";
					String userid = "";

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent
								.getJSONObject("response_params");
						appid = params.getString("appid");
						channelid = params.getString("channel_id");
						userid = params.getString("user_id");
					} catch (JSONException e) {
						Log.e(TAG, "Parse bind json infos error: " + e);
					}

					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(this);
					Editor editor = sp.edit();
					editor.putString("appid", appid);
					editor.putString("channel_id", channelid);
					editor.putString("user_id", userid);
					editor.commit();

					// showChannelIds();

					toastStr = "Bind Success";
				} else {
					toastStr = "Bind Fail, Error Code: " + errorCode;
					if (errorCode == 30607) {
						Log.d("Bind Fail", "update channel token-----!");
					}
				}

				Toast.makeText(this, toastStr, Toast.LENGTH_LONG).show();
			}
		} else if (ACTION_MESSAGE.equals(action)) {
			String message = intent.getStringExtra(EXTRA_MESSAGE);
			String summary = "Receive message from server:\n\t";
			JSONObject contentJson = null;
			String contentStr = message;
			try {
				contentJson = new JSONObject(message);
				contentStr = contentJson.toString(4);
			} catch (JSONException e) {
				Log.d(TAG, "Parse message json exception.");
			}
			summary += contentStr;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(summary);
			builder.setCancelable(true);
			Dialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else {
			Log.i(TAG, "Activity normally start!");
		}
	}

	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// /��ʱִ������
			// ////////////////////////////////////////////////////////////////
			switch (msg.what) {
			case 1:

				break;
			case 2:

				break;
			case 3:

				break;
			case 4: {
				if (msg.obj != null) {
					push(msg.obj.toString());
				}
			}
				break;
			case 5: {
				if (msg.obj != null) {
					push(msg.obj.toString());
				}
				// Log.i(mtag, textView1.getText().toString());
			}
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

	static int k = 0;

	public static void push(String str) {
		k++;
		if (k > 60) {
			textView1.setText("");
			k = 0;

		}
		textView1
				.setText(PersonStringUtils.pareDateToString(new Date())
						+ "\n"
						+ str
						+ "\n"
						+ "------------------------------------------------------------\n"
						+ textView1.getText() + "\n");
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	public void findInfo() {
		Message msg = new Message();
		msg.what = 4;
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		if (tm.getCallState() > -1) {
			PersonDbUtils.putValue(PersonConstant.USER_AGENT_INFO_CALLSTATE,
					tm.getCallState(), null);
		}
		if (tm.getCellLocation() != null) {
			PersonDbUtils.putValue(PersonConstant.USER_AGENT_INFO_CELLLOCATION,
					tm.getCellLocation().toString(), null);
		}
		if (tm.getDeviceId() != null) {
			PersonDbUtils.putValue(PersonConstant.USER_AGENT_INFO_CALLIMEI,
					tm.getDeviceId(), null);
		}
		if (tm.getLine1Number() != null) {
			PersonDbUtils.putValue(PersonConstant.USER_AGENT_INFO_CALLMSISDN,
					tm.getLine1Number(), null);
		}
		if (tm.getNetworkCountryIso() != null) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLNETWORKCOUNTRYISO,
					tm.getNetworkCountryIso(), null);
		}
		if (tm.getNetworkOperator() != null) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLNETWORKOPERATOR,
					tm.getNetworkOperator(), null);
		}
		if (tm.getNetworkOperatorName() != null) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLNETWORKOPERATORNAME,
					tm.getNetworkOperatorName(), null);
		}
		if (tm.getNetworkType() > -1) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLNETWORKTYPE,
					tm.getNetworkType(), null);
		}
		if (tm.getPhoneType() > -1) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLPHONETYPE,
					tm.getPhoneType(), null);
		}
		if (tm.getSimOperator() != null) {
			PersonDbUtils.putValue(
					PersonConstant.USER_AGENT_INFO_CALLSIMOPERATOR,
					tm.getSimOperator(), null);
		}
		if (tm.getSimState() > -1) {
			PersonDbUtils.putValue(PersonConstant.USER_AGENT_INFO_CALLSIMSTATE,
					tm.getSimState(), null);
		}

		/*
		 * �绰״̬�� 1.tm.CALL_STATE_IDLE=0 �޻ 2.tm.CALL_STATE_RINGING=1 ����
		 * 3.tm.CALL_STATE_OFFHOOK=2 ժ��
		 */
		msg.obj = msg.obj + "\n" + "�绰״̬" + "\n" + tm.getCallState();// int

		/*
		 * �绰��λ��
		 */
		msg.obj = msg.obj + "\n" + "�绰��λ��" + "\n" + tm.getCellLocation();// CellLocation

		/*
		 * Ψһ���豸ID�� GSM�ֻ��� IMEI �� CDMA�ֻ��� MEID. Return null if device ID is not
		 * available.
		 */
		msg.obj = msg.obj + "\n" + "IMEI" + "\n" + tm.getDeviceId();// String

		/*
		 * �豸������汾�ţ� ���磺the IMEI/SV(software version) for GSM phones. Return
		 * null if the software version is not available.
		 */
		msg.obj = msg.obj + "\n" + "�豸������汾��" + "\n"
				+ tm.getDeviceSoftwareVersion();// String

		/*
		 * �ֻ��ţ� GSM�ֻ��� MSISDN. Return null if it is unavailable.
		 */
		msg.obj = msg.obj + "\n" + "GSM�ֻ��� MSISDN" + "\n" + tm.getLine1Number();// String

		/*
		 * �����ĵ绰����Ϣ: ���ͣ�List<NeighboringCellInfo>
		 * ��ҪȨ�ޣ�android.Manifest.permission#ACCESS_COARSE_UPDATES
		 */
		msg.obj = msg.obj + "\n" + "�����ĵ绰����" + "\n"
				+ tm.getNeighboringCellInfo();// List<NeighboringCellInfo>

		/*
		 * ��ȡISO��׼�Ĺ����룬�����ʳ�;���š� ע�⣺�����û���������ע�����Ч�� ��CDMA�����н��Ҳ���ɿ���
		 */
		msg.obj = msg.obj + "\n" + " ��ȡISO��׼�Ĺ�����" + "\n"
				+ tm.getNetworkCountryIso();// String

		/*
		 * MCC+MNC(mobile country code + mobile network code) ע�⣺�����û���������ע��ʱ��Ч��
		 * ��CDMA�����н��Ҳ���ɿ���
		 */
		msg.obj = msg.obj + "\n" + " MCC+MNC" + "\n" + tm.getNetworkOperator();// String

		/*
		 * ������ĸ�����current registered operator(��ǰ��ע����û�)������ ע�⣺�����û���������ע��ʱ��Ч��
		 * ��CDMA�����н��Ҳ���ɿ���
		 */
		msg.obj = msg.obj + "\n" + "����" + "\n" + tm.getNetworkOperatorName();// String

		/*
		 * ��ǰʹ�õ��������ͣ� ���磺 NETWORK_TYPE_UNKNOWN ��������δ֪ 0 NETWORK_TYPE_GPRS GPRS����
		 * 1 NETWORK_TYPE_EDGE EDGE���� 2 NETWORK_TYPE_UMTS UMTS���� 3
		 * NETWORK_TYPE_HSDPA HSDPA���� 8 NETWORK_TYPE_HSUPA HSUPA���� 9
		 * NETWORK_TYPE_HSPA HSPA���� 10 NETWORK_TYPE_CDMA CDMA����,IS95A �� IS95B. 4
		 * NETWORK_TYPE_EVDO_0 EVDO����, revision 0. 5 NETWORK_TYPE_EVDO_A EVDO����,
		 * revision A. 6 NETWORK_TYPE_1xRTT 1xRTT���� 7
		 */
		msg.obj = msg.obj + "\n" + "��ǰʹ�õ���������" + "\n" + tm.getNetworkType();// int

		/*
		 * �ֻ����ͣ� ���磺 PHONE_TYPE_NONE ���ź� PHONE_TYPE_GSM GSM�ź� PHONE_TYPE_CDMA
		 * CDMA�ź�
		 */
		msg.obj = msg.obj + "\n" + "�ֻ�����" + "\n" + tm.getPhoneType();// int

		/*
		 * Returns the ISO country code equivalent for the SIM provider's
		 * country code. ��ȡISO�����룬�൱���ṩSIM���Ĺ����롣
		 */
		msg.obj = msg.obj + "\n" + " ��ȡISO������" + "\n" + tm.getSimCountryIso();// String

		/*
		 * Returns the MCC+MNC (mobile country code + mobile network code) of
		 * the provider of the SIM. 5 or 6 decimal digits.
		 * ��ȡSIM���ṩ���ƶ���������ƶ�������.5��6λ��ʮ��������. SIM����״̬������
		 * SIM_STATE_READY(ʹ��getSimState()�ж�).
		 */
		msg.obj = msg.obj + "\n" + "��ȡSIM���ṩ���ƶ���������ƶ�������" + "\n"
				+ tm.getSimOperator();// String

		/*
		 * ���������ƣ� ���磺�й��ƶ�����ͨ SIM����״̬������ SIM_STATE_READY(ʹ��getSimState()�ж�).
		 */
		msg.obj = msg.obj + "\n" + "����������" + "\n" + tm.getSimOperatorName();// String

		/*
		 * SIM�������кţ� ��ҪȨ�ޣ�READ_PHONE_STATE
		 */
		msg.obj = msg.obj + "\n" + "SIM�������к�" + "\n" + tm.getSimSerialNumber();// String

		/*
		 * SIM��״̬��Ϣ�� SIM_STATE_UNKNOWN δ֪״̬ 0 SIM_STATE_ABSENT û�忨 1
		 * SIM_STATE_PIN_REQUIRED ����״̬����Ҫ�û���PIN����� 2 SIM_STATE_PUK_REQUIRED
		 * ����״̬����Ҫ�û���PUK����� 3 SIM_STATE_NETWORK_LOCKED ����״̬����Ҫ�����PIN����� 4
		 * SIM_STATE_READY ����״̬ 5
		 */
		msg.obj = msg.obj + "\n" + "SIM��״̬��Ϣ" + "\n" + tm.getSimState();// int

		/*
		 * Ψһ���û�ID�� ���磺IMSI(�����ƶ��û�ʶ����) for a GSM phone. ��ҪȨ�ޣ�READ_PHONE_STATE
		 */
		msg.obj = msg.obj + "\n" + "IMSI" + "\n" + tm.getSubscriberId();// String

		/*
		 * ȡ�ú������ʼ���صı�ǩ����Ϊʶ��� ��ҪȨ�ޣ�READ_PHONE_STATE
		 */
		msg.obj = msg.obj + "\n" + "ȡ�ú������ʼ����" + "\n"
				+ tm.getVoiceMailAlphaTag();// String

		/*
		 * ��ȡ�����ʼ����룺 ��ҪȨ�ޣ�READ_PHONE_STATE
		 */
		msg.obj = msg.obj + "\n" + "�ʼ�����" + "\n" + tm.getVoiceMailNumber();// String

		/*
		 * ICC���Ƿ����
		 */
		msg.obj = msg.obj + "\n" + " ICC���Ƿ����" + "\n" + tm.hasIccCard();// boolean

		/*
		 * �Ƿ�����: (��GSM��;��)
		 */
		msg.obj = msg.obj + "\n" + " �Ƿ�����" + "\n" + tm.isNetworkRoaming();//
		
	
		
		handler.sendMessage(msg);
	}

	@Override
	protected void onStart() {
		// mLocationClient.start();
		super.onStart();
		// Log.d(TAG, ">=====onStart=====<");
		// Intent recIntent = this.getIntent();
		// String openType = ""
		// + recIntent.getIntExtra(PushConstants.EXTRA_OPENTYPE, 0);
		// String msgId = recIntent.getStringExtra(PushConstants.EXTRA_MSGID);
		//
		// Log.d(TAG,
		// "Collect Activity start feedback info , package:"
		// + this.getPackageName() + " openType: " + openType
		// + " msgid: " + msgId);
		//
		// PushManager.activityStarted(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// PushManager.activityStoped(this);
	}

}
