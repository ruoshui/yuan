package cn.wang.yin.ui.base;

import java.io.Serializable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import cn.wang.yin.personal.service.PersonService;
import cn.wang.yin.ui.Base;

public class PersonBaseActivity extends Base implements Serializable {
	public PersonBaseActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �ж��Ƿ��¼

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog.setTitle("����");
			dialog.setMessage("��ȷ��Ҫ�˳���ǰ����");
			dialog.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getApplicationContext(),
									PersonService.class);
							stopService(intent);
							finish();
							onDestroy();

						}
					});
			dialog.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dialog.show();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}

}
