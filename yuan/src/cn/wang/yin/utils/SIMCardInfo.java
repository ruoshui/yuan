package cn.wang.yin.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * class name��SIMCardInfo<BR>
 * class description����ȡSim����Ϣ<BR>
 * PS�� �����ڼ������Ȩ�� <BR>
 * Date:2012-3-12<BR>
 * 
 * @version 1.00
 * @author CODYY)peijiangping
 */
public class SIMCardInfo {
	/**
	 * TelephonyManager�ṩ�豸�ϻ�ȡͨѶ������Ϣ����ڡ� Ӧ�ó������ʹ������෽��ȷ���ĵ��ŷ����̺͹��� �Լ�ĳЩ���͵��û�������Ϣ��
	 * Ӧ�ó���Ҳ����ע��һ�����������绰��״̬�ı仯������Ҫֱ��ʵ���������
	 * ʹ��Context.getSystemService(Context.TELEPHONY_SERVICE)����ȡ������ʵ����
	 */
	public static Context cxt;

	private static TelephonyManager telephonyManager;
	/**
	 * �����ƶ��û�ʶ����
	 */
	private String IMSI;

	public SIMCardInfo() {
		telephonyManager = (TelephonyManager) cxt
				.getSystemService(Context.TELEPHONY_SERVICE);

	}

	public SIMCardInfo(Context context) {
		context = cxt;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public static void init(Context context) {
		if (cxt == null)
			cxt = context;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}
//	 /*
//	   * �绰״̬��
//	   * 1.tm.CALL_STATE_IDLE=0          �޻
//	   * 2.tm.CALL_STATE_RINGING=1  ����
//	   * 3.tm.CALL_STATE_OFFHOOK=2  ժ��
//	   */
//	  tm.getCallState();//int
//	  
//	  /*
//	   * �绰��λ��
//	   * 
//	   */
//	  tm.getCellLocation();//CellLocation
//	  
//	  /*
//	   * Ψһ���豸ID��
//	   * GSM�ֻ��� IMEI �� CDMA�ֻ��� MEID. 
//	   * Return null if device ID is not available.
//	   */
//	  tm.getDeviceId();//String
//	  
//	  /*
//	   * �豸������汾�ţ�
//	   * ���磺the IMEI/SV(software version) for GSM phones.
//	   * Return null if the software version is not available. 
//	   */
//	  tm.getDeviceSoftwareVersion();//String
//	  
//	  /*
//	   * �ֻ��ţ�
//	   * GSM�ֻ��� MSISDN.
//	   * Return null if it is unavailable. 
//	   */
//	  tm.getLine1Number();//String
//	  
//	  /*
//	   * �����ĵ绰����Ϣ:
//	   * ���ͣ�List<NeighboringCellInfo> 
//	   * ��ҪȨ�ޣ�android.Manifest.permission#ACCESS_COARSE_UPDATES
//	   */
//	  tm.getNeighboringCellInfo();//List<NeighboringCellInfo>
//	  
//	  /*
//	   * ��ȡISO��׼�Ĺ����룬�����ʳ�;���š�
//	   * ע�⣺�����û���������ע�����Ч��
//	   *       ��CDMA�����н��Ҳ���ɿ���
//	   */
//	  tm.getNetworkCountryIso();//String
//	  
//	  /*
//	   * MCC+MNC(mobile country code + mobile network code)
//	   * ע�⣺�����û���������ע��ʱ��Ч��
//	   *    ��CDMA�����н��Ҳ���ɿ���
//	   */
//	  tm.getNetworkOperator();//String
//	  
//	  /*
//	   * ������ĸ�����current registered operator(��ǰ��ע����û�)������
//	   * ע�⣺�����û���������ע��ʱ��Ч��
//	   *    ��CDMA�����н��Ҳ���ɿ���
//	   */
//	  tm.getNetworkOperatorName();//String
//	  
//	  /*
//	   * ��ǰʹ�õ��������ͣ�
//	   * ���磺 NETWORK_TYPE_UNKNOWN  ��������δ֪  0
//	     NETWORK_TYPE_GPRS     GPRS����  1
//	     NETWORK_TYPE_EDGE     EDGE����  2
//	     NETWORK_TYPE_UMTS     UMTS����  3
//	     NETWORK_TYPE_HSDPA    HSDPA����  8 
//	     NETWORK_TYPE_HSUPA    HSUPA����  9
//	     NETWORK_TYPE_HSPA     HSPA����  10
//	     NETWORK_TYPE_CDMA     CDMA����,IS95A �� IS95B.  4
//	     NETWORK_TYPE_EVDO_0   EVDO����, revision 0.  5
//	     NETWORK_TYPE_EVDO_A   EVDO����, revision A.  6
//	     NETWORK_TYPE_1xRTT    1xRTT����  7
//	   */
//	  tm.getNetworkType();//int
//	  
//	  /*
//	   * �ֻ����ͣ�
//	   * ���磺 PHONE_TYPE_NONE  ���ź�
//	     PHONE_TYPE_GSM   GSM�ź�
//	     PHONE_TYPE_CDMA  CDMA�ź�
//	   */
//	  tm.getPhoneType();//int
//	  
//	  /*
//	   * Returns the ISO country code equivalent for the SIM provider's country code.
//	   * ��ȡISO�����룬�൱���ṩSIM���Ĺ����롣
//	   * 
//	   */
//	  tm.getSimCountryIso();//String
//	  
//	  /*
//	   * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.
//	   * ��ȡSIM���ṩ���ƶ���������ƶ�������.5��6λ��ʮ��������.
//	   * SIM����״̬������ SIM_STATE_READY(ʹ��getSimState()�ж�).
//	   */
//	  tm.getSimOperator();//String
//	  
//	  /*
//	   * ���������ƣ�
//	   * ���磺�й��ƶ�����ͨ
//	   * SIM����״̬������ SIM_STATE_READY(ʹ��getSimState()�ж�).
//	   */
//	  tm.getSimOperatorName();//String
//	  
//	  /*
//	   * SIM�������кţ�
//	   * ��ҪȨ�ޣ�READ_PHONE_STATE
//	   */
//	  tm.getSimSerialNumber();//String
//	  
//	  /*
//	   * SIM��״̬��Ϣ��
//	   *  SIM_STATE_UNKNOWN          δ֪״̬ 0
//	   SIM_STATE_ABSENT           û�忨 1
//	   SIM_STATE_PIN_REQUIRED     ����״̬����Ҫ�û���PIN����� 2
//	   SIM_STATE_PUK_REQUIRED     ����״̬����Ҫ�û���PUK����� 3
//	   SIM_STATE_NETWORK_LOCKED   ����״̬����Ҫ�����PIN����� 4
//	   SIM_STATE_READY            ����״̬ 5
//	   */
//	  tm.getSimState();//int
//	  
//	  /*
//	   * Ψһ���û�ID��
//	   * ���磺IMSI(�����ƶ��û�ʶ����) for a GSM phone.
//	   * ��ҪȨ�ޣ�READ_PHONE_STATE
//	   */
//	  tm.getSubscriberId();//String
//	  
//	  /*
//	   * ȡ�ú������ʼ���صı�ǩ����Ϊʶ���
//	   * ��ҪȨ�ޣ�READ_PHONE_STATE
//	   */
//	  tm.getVoiceMailAlphaTag();//String
//	  
//	  /*
//	   * ��ȡ�����ʼ����룺
//	   * ��ҪȨ�ޣ�READ_PHONE_STATE
//	   */
//	  tm.getVoiceMailNumber();//String
//	  
//	  /*
//	   * ICC���Ƿ����
//	   */
//	  tm.hasIccCard();//boolean
//	  
//	  /*
//	   * �Ƿ�����:
//	   * (��GSM��;��)
//	   */
//	  tm.isNetworkRoaming();//
	/**
	 * Role:��ȡ��ǰ���õĵ绰���� <BR>
	 * Date:2012-3-12 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public String getNativePhoneNumber() {
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	/**
	 * Role:Telecom service providers��ȡ�ֻ���������Ϣ <BR>
	 * ��Ҫ����Ȩ��<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/> <BR>
	 * Date:2012-3-12 <BR>
	 * 
	 * @author CODYY)peijiangping
	 */
	public String getProvidersName() {
		String ProvidersName = null;
		// ����Ψһ���û�ID;�������ſ��ı�������
		IMSI = telephonyManager.getSubscriberId();
		// IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "�й��ƶ�";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "�й���ͨ";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "�й�����";
		}
		return ProvidersName;
	}
}
