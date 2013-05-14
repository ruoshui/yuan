/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.wang.yin.ui;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.wang.yin.personal.R;

public class CompassActivity extends Activity {
	private static final int EXIT_TIME = 2000;// ���ΰ����ؼ��ļ���ж�
	private final float MAX_ROATE_DEGREE = 1.0f;// �����תһ�ܣ���360��
	private SensorManager mSensorManager;// �������������
	private Sensor mOrientationSensor;// ����������
	// private LocationManager mLocationManager;// λ�ù������
	// private String mLocationProvider;// λ���ṩ�����ƣ�GPS�豸��������
	private float mDirection;// ��ǰ���㷽��
	private float mTargetDirection;// Ŀ�긡�㷽��
	private AccelerateInterpolator mInterpolator;// �����ӿ�ʼ���������仯����һ�����ٵĹ���,����һ����������
	protected final Handler mHandler = new Handler();
	private boolean mStopDrawing;// �Ƿ�ָֹͣ������ת�ı�־λ
	private boolean mChinease;// ϵͳ��ǰ�Ƿ�ʹ������
	private long firstExitTime = 0L;// ���������һ�ΰ����ؼ���ʱ��

	LocationApplication application;
	View mCompassView;
	CompassView mPointer;// ָ����view
	// TextView mLocationTextView;// ��ʾλ�õ�view
	TextView mLatitudeTV;// γ��
	TextView mLongitudeTV;// ����
	LinearLayout mDirectionLayout;// ��ʾ���򣨶�����������view
	LinearLayout mAngleLayout;// ��ʾ���������view
	View mViewGuide;
	ImageView mGuideAnimation;
	protected Handler invisiableHandler = new Handler() {
		public void handleMessage(Message msg) {
			mViewGuide.setVisibility(View.GONE);
		}
	};

	public void onWindowFocusChanged(boolean hasFocus) {
		AnimationDrawable anim = (AnimationDrawable) mGuideAnimation
				.getDrawable();
		anim.start();
	}

	// ����Ǹ���ָ������ת���̣߳�handler�����ʹ�ã�ÿ20�����ⷽ��仯ֵ����Ӧ����ָ������ת
	protected Runnable mCompassViewUpdater = new Runnable() {
		@Override
		public void run() {
			if (mPointer != null && !mStopDrawing) {
				if (mDirection != mTargetDirection) {

					// calculate the short routine
					float to = mTargetDirection;
					if (to - mDirection > 180) {
						to -= 360;
					} else if (to - mDirection < -180) {
						to += 360;
					}

					// limit the max speed to MAX_ROTATE_DEGREE
					float distance = to - mDirection;
					if (Math.abs(distance) > MAX_ROATE_DEGREE) {
						distance = distance > 0 ? MAX_ROATE_DEGREE
								: (-1.0f * MAX_ROATE_DEGREE);
					}

					// need to slow down if the distance is short
					mDirection = normalizeDegree(mDirection
							+ ((to - mDirection) * mInterpolator
									.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
											: 0.3f)));// ����һ�����ٶ���ȥ��תͼƬ����ϸ��
					mPointer.updateDirection(mDirection);// ����ָ������ת
				}

				updateDirection();// ���·���ֵ

				mHandler.postDelayed(mCompassViewUpdater, 20);// 20���׺�����ִ���Լ����ȶ�ʱ����
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = LocationApplication.getInstance();
		setContentView(R.layout.main);
		initResources();// ��ʼ��view
		initServices();// ��ʼ����������λ�÷���
		application.mTv = mLatitudeTV;
		application.mAddress = mLongitudeTV;

		if (application.mData != null)
			mLatitudeTV.setText(application.mData);
		if (application.address != null)
			mLatitudeTV.setText(application.address);
		application.mLocationClient.start();
	}

	@Override
	public void onBackPressed() {// ���Ƿ��ؼ�
		long curTime = System.currentTimeMillis();
		if (curTime - firstExitTime < EXIT_TIME) {// ���ΰ����ؼ���ʱ��С��2����˳�Ӧ��
			finish();
		} else {
			Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
			firstExitTime = curTime;
		}
	}

	@Override
	protected void onResume() {// �ڻָ��������������жϡ�����λ�ø��·���ʹ���������
		super.onResume();
		// if (mLocationProvider != null) {
		// updateLocation(mLocationManager
		// .getLastKnownLocation(mLocationProvider));
		// mLocationManager.requestLocationUpdates(mLocationProvider, 2000,
		// 10, mLocationListener);// 2����߾���仯10��ʱ����һ�ε���λ��
		// }
		// else {
		// mLocationTextView.setText(R.string.cannot_get_location);
		// }
		if (mOrientationSensor != null) {
			mSensorManager.registerListener(mOrientationSensorEventListener,
					mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
		} else {
			// Toast.makeText(this, R.string.cannot_get_sensor,
			// Toast.LENGTH_SHORT)
			// .show();
		}
		mStopDrawing = false;
		mHandler.postDelayed(mCompassViewUpdater, 20);// 20����ִ��һ�θ���ָ����ͼƬ��ת
	}

	@Override
	protected void onPause() {// ����ͣ������������ע�������������λ�ø��·���
		super.onPause();
		mStopDrawing = true;
		if (mOrientationSensor != null) {
			mSensorManager.unregisterListener(mOrientationSensorEventListener);
		}
		// if (mLocationProvider != null) {
		// mLocationManager.removeUpdates(mLocationListener);
		// }
	}

	// ��ʼ��view
	private void initResources() {
		mViewGuide = findViewById(R.id.view_guide);
		mViewGuide.setVisibility(View.VISIBLE);
		invisiableHandler.sendMessageDelayed(new Message(), 3000);
		mGuideAnimation = (ImageView) findViewById(R.id.guide_animation);
		mDirection = 0.0f;// ��ʼ����ʼ����
		mTargetDirection = 0.0f;// ��ʼ��Ŀ�귽��
		mInterpolator = new AccelerateInterpolator();// ʵ�������ٶ�������
		mStopDrawing = true;
		mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");// �ж�ϵͳ��ǰʹ�õ������Ƿ�Ϊ����

		mCompassView = findViewById(R.id.view_compass);// ʵ������һ��LinearLayout��װָ����ImageView��λ��TextView
		mPointer = (CompassView) findViewById(R.id.compass_pointer);// �Զ����ָ����view
		// mLocationTextView = (TextView)
		// findViewById(R.id.textview_location);// ��ʾλ����Ϣ��TextView
		mLongitudeTV = (TextView) findViewById(R.id.textview_location_longitude_degree);
		mLatitudeTV = (TextView) findViewById(R.id.textview_location_latitude_degree);
		mDirectionLayout = (LinearLayout) findViewById(R.id.layout_direction);// ������ʾ�������ƣ�������������LinearLayout
		mAngleLayout = (LinearLayout) findViewById(R.id.layout_angle);// ������ʾ������������LinearLayout

		// mPointer.setImageResource(mChinease ? R.drawable.compass_cn
		// : R.drawable.compass);// ���ϵͳʹ�����ģ��������ĵ�ָ����ͼƬ
	}

	// ��ʼ����������λ�÷���
	private void initServices() {
		// sensor manager
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager.getSensorList(
				Sensor.TYPE_ORIENTATION).get(0);
		// Log.i("way", mOrientationSensor.getName());

		// location manager
		// mLocationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// Criteria criteria = new Criteria();// �������󣬼�ָ���������˻��LocationProvider
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);// �ϸ߾���
		// criteria.setAltitudeRequired(false);// �Ƿ���Ҫ�߶���Ϣ
		// criteria.setBearingRequired(false);// �Ƿ���Ҫ������Ϣ
		// criteria.setCostAllowed(true);// �Ƿ��������
		// criteria.setPowerRequirement(Criteria.POWER_LOW);// ���õ͵��
		// mLocationProvider = mLocationManager.getBestProvider(criteria,
		// true);// ��ȡ������õ�Provider

	}

	// ���¶���������ʾ�ķ���
	private void updateDirection() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// ���Ƴ�layout�����е�view
		mDirectionLayout.removeAllViews();
		mAngleLayout.removeAllViews();

		// �����Ǹ���mTargetDirection������������ͼƬ�Ĵ���
		ImageView east = null;
		ImageView west = null;
		ImageView south = null;
		ImageView north = null;
		float direction = normalizeDegree(mTargetDirection * -1.0f);
		if (direction > 22.5f && direction < 157.5f) {
			// east
			east = new ImageView(this);
			east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
			east.setLayoutParams(lp);
		} else if (direction > 202.5f && direction < 337.5f) {
			// west
			west = new ImageView(this);
			west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
			west.setLayoutParams(lp);
		}

		if (direction > 112.5f && direction < 247.5f) {
			// south
			south = new ImageView(this);
			south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
			south.setLayoutParams(lp);
		} else if (direction < 67.5 || direction > 292.5f) {
			// north
			north = new ImageView(this);
			north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
			north.setLayoutParams(lp);
		}
		// �����Ǹ���ϵͳʹ�����ԣ�������Ӧ������ͼƬ��Դ
		if (mChinease) {
			// east/west should be before north/south
			if (east != null) {
				mDirectionLayout.addView(east);
			}
			if (west != null) {
				mDirectionLayout.addView(west);
			}
			if (south != null) {
				mDirectionLayout.addView(south);
			}
			if (north != null) {
				mDirectionLayout.addView(north);
			}
		} else {
			// north/south should be before east/west
			if (south != null) {
				mDirectionLayout.addView(south);
			}
			if (north != null) {
				mDirectionLayout.addView(north);
			}
			if (east != null) {
				mDirectionLayout.addView(east);
			}
			if (west != null) {
				mDirectionLayout.addView(west);
			}
		}
		// �����Ǹ��ݷ��������ʾ����ͼƬ����
		int direction2 = (int) direction;
		boolean show = false;
		if (direction2 >= 100) {
			mAngleLayout.addView(getNumberImage(direction2 / 100));
			direction2 %= 100;
			show = true;
		}
		if (direction2 >= 10 || show) {
			mAngleLayout.addView(getNumberImage(direction2 / 10));
			direction2 %= 10;
		}
		mAngleLayout.addView(getNumberImage(direction2));
		// ����������һ�����ͼƬ
		ImageView degreeImageView = new ImageView(this);
		degreeImageView.setImageResource(R.drawable.degree);
		degreeImageView.setLayoutParams(lp);
		mAngleLayout.addView(degreeImageView);
	}

	// ��ȡ���������Ӧ��ͼƬ������ImageView
	private ImageView getNumberImage(int number) {
		ImageView image = new ImageView(this);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		switch (number) {
		case 0:
			image.setImageResource(R.drawable.number_0);
			break;
		case 1:
			image.setImageResource(R.drawable.number_1);
			break;
		case 2:
			image.setImageResource(R.drawable.number_2);
			break;
		case 3:
			image.setImageResource(R.drawable.number_3);
			break;
		case 4:
			image.setImageResource(R.drawable.number_4);
			break;
		case 5:
			image.setImageResource(R.drawable.number_5);
			break;
		case 6:
			image.setImageResource(R.drawable.number_6);
			break;
		case 7:
			image.setImageResource(R.drawable.number_7);
			break;
		case 8:
			image.setImageResource(R.drawable.number_8);
			break;
		case 9:
			image.setImageResource(R.drawable.number_9);
			break;
		}
		image.setLayoutParams(lp);
		return image;
	}

	// ����λ����ʾ
	private void updateLocation(Location location) {
		if (location == null) {
			// mLocationTextView.setText(R.string.getting_location);
			return;
		} else {
			// StringBuilder sb = new StringBuilder();
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			String latitudeStr;
			String longitudeStr;
			if (latitude >= 0.0f) {
				latitudeStr = getString(R.string.location_north,
						getLocationString(latitude));
			} else {
				latitudeStr = getString(R.string.location_south,
						getLocationString(-1.0 * latitude));
			}

			// sb.append("    ");

			if (longitude >= 0.0f) {
				longitudeStr = getString(R.string.location_east,
						getLocationString(longitude));
			} else {
				longitudeStr = getString(R.string.location_west,
						getLocationString(-1.0 * longitude));
			}
			mLatitudeTV.setText(latitudeStr);
			mLongitudeTV.setText(longitudeStr);
			// mLocationTextView.setText(sb.toString());//
			// ��ʾ��γ�ȣ���ʵ��������������룬��ʾ�����ַ
		}
	}

	// �Ѿ�γ��ת���ɶȷ�����ʾ
	private String getLocationString(double input) {
		int du = (int) input;
		int fen = (((int) ((input - du) * 3600))) / 60;
		int miao = (((int) ((input - du) * 3600))) % 60;
		return String.valueOf(du) + "��" + String.valueOf(fen) + "��"
				+ String.valueOf(miao) + "��";
	}

	// ���򴫸����仯����
	private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float direction = event.values[mSensorManager.DATA_X] * -1.0f;
			mTargetDirection = normalizeDegree(direction);// ��ֵ��ȫ�ֱ�������ָ������ת
			// Log.i("way", event.values[mSensorManager.DATA_Y] + "");
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	// �������򴫸�����ȡ��ֵ
	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}

	// λ����Ϣ���¼���
	// LocationListener mLocationListener = new LocationListener() {
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	// if (status != LocationProvider.OUT_OF_SERVICE) {
	// updateLocation(mLocationManager
	// .getLastKnownLocation(mLocationProvider));
	// }
	// // else {
	// // mLocationTextView.setText(R.string.cannot_get_location);
	// // }
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	// }
	//
	// @Override
	// public void onLocationChanged(Location location) {
	// updateLocation(location);// ����λ��
	// }
	// };
}
