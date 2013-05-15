package cn.wang.yin.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import cn.wang.yin.personal.R;
import cn.wang.yin.ui.fragment.FragmentExecute;
import cn.wang.yin.ui.fragment.FragmentSearch;
import cn.wang.yin.ui.fragment.FragmentSetting;
import cn.wang.yin.ui.fragment.FragmentTeam;
import cn.wang.yin.ui.widget.BottomBar;
import cn.wang.yin.ui.widget.BottomBar.OnItemChangedListener;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonDbUtils;

/**
 * This demo shows how to use FragmentActivity to build the frame of a common
 * application. To replace the deprecated class such as TabActivity,
 * ActivityGroup,and so on.
 * 
 * ���demoչʾ�����ʹ��FragmentActivity������Ӧ�ó���Ŀ��
 * ����ʹ����������ԭ����TabActivity��ActivityGroup�ȵ�
 * 
 * @author MichaelYe
 * @since 2012-9-6
 * @see http://developer.android.com/reference/android/app/Fragment.html
 * @see http://developer.android.com/training/basics/fragments/index.html
 * @see http://developer.android.com/guide/components/fragments.html
 * */
public class FragmentMain extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!PersonDbUtils.getValue(PersonConstant.USER_FIRST_OPEN, false)) {
			startActivityForResult((new Intent().setClass(FragmentMain.this,
					GuideViewActivity.class)),
					PersonConstant.ETONG_RESULTCODE_INDEX);

		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_main);
		final BottomBar bottomBar = (BottomBar) findViewById(R.id.ll_bottom_bar);
		bottomBar.setOnItemChangedListener(new OnItemChangedListener() {

			@Override
			public void onItemChanged(int index) {

				showDetails(index);
			}
		});
		bottomBar.setSelectedState(0);

		// bottomBar.hideIndicate();//�������ԭ�����ƺ�ɫСͼ��Ŀɼ���
		// bottomBar.showIndicate(12);

	}

	private void showDetails(int index) {
		Fragment details = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.details);
		switch (index) {
		case 0:
			details = new FragmentExecute();
			break;
		case 1:
			details = new FragmentExpress();
			break;
		case 2:
			details = new SaveImageFragment();
			break;
		case 3:
			details = new FragmentSearch();
			break;
		case 4:
			details = new FragmentSetting();
			break;
		}
		// Execute a transaction, replacing any existing
		// fragment with this one inside the frame.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.details, details);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// ft.addToBackStack(null);//���д�����Է���֮ǰ�Ĳ���������������£������߶���ʾ������£�
		ft.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// super.onResume();
			super.onStop();

			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}

	public static void launch(Context c) {
		Intent intent = new Intent(c, FragmentMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}
}
