package cn.wang.yin.personal.service.listenner;

import android.content.Context;

import cn.wang.yin.utils.CollectGpsUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationProvider {
	private static LocationClient mLocationClient = null;

	private static MyBDListener listener = new MyBDListener();

	Context context;

	public LocationProvider(Context context) {
		super();
		this.context = context;
	}

	public void startLocation() {
		mLocationClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������Ϊbd09ll
		option.setPriority(LocationClientOption.NetWorkFirst); // ������������
		option.setProdName("demo"); // ���ò�Ʒ������
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(listener);
		mLocationClient.start();// ���������ȡλ�÷ֿ����Ϳ��Ծ������ں����ʹ���л�ȡ��λ��
	}

	/**
	 * ֹͣ��������Դ����
	 */
	public void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
			mLocationClient = null;
		}
	}

	/**
	 * ����λ�ò����浽SItude��
	 */
	public void updateListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}
	}

	private static class MyBDListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location.getCity() == null) {
				int type = mLocationClient.requestLocation();
				return;
			}
			CollectGpsUtil.saveGps(location);
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// return
		}

	}
}