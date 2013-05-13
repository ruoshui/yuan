package cn.wang.yin.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import cn.wang.yin.hessian.api.Remot;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.RemoteFactoryUtils;

import com.caucho.hessian.client.HessianProxyFactory;
import com.wang.yin.hessian.bean.Express;
import com.wang.yin.hessian.bean.ExpressData;

public class express extends Activity {
	EditText editText1;
	Button button1;
	String num = "";
	ListView listView1;
	public static final int SUCCESS = 101;
	public static final int FAIL = 102;

	List<String> all = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.express);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setText("300002174341");
		button1 = (Button) findViewById(R.id.button1);
		listView1 = (ListView) findViewById(R.id.listView1);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String express_num = editText1.getText().toString();
				if (StringUtils.isNotBlank(express_num)) {
					num = express_num;
					Thread t = new Thread(scanrunnable);
					t.run();
					// hand.post(scanrunnable);
				}
			}
		});
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
		// .penaltyLog() // 打印logcat
		// .penaltyDeath().build());
	}

	public void fresh(List<ExpressData> datas) {
		// List<ExpressData> datas = (List<ExpressData>) msg.obj;
		for (ExpressData bean : datas) {
			all.add(bean.getContext());
		}
		ArrayAdapter<String> adapterzhouqi = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, all);
		listView1.setAdapter(adapterzhouqi);
	}

	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS: {
				if (msg.obj != null) {
					List<ExpressData> datas = (List<ExpressData>) msg.obj;
					fresh(datas);
				}
			}
				break;
			case FAIL: {
				AlertDialog dialog = new AlertDialog.Builder(express.this)
						.setTitle("提示")
						.setMessage("需要网络，您的手机当前网络不可用，请设置您的网络")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;
			}

			super.handleMessage(msg);
		}

	};
	Runnable scanrunnable = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = SUCCESS;
			Remot remot = null;
			try {
				// remot = RemoteFactoryUtils.getReport();
				HessianProxyFactory factory = RemoteFactoryUtils.getFactory();
				remot = factory.create(Remot.class, PersonConstant.REMOTE_URL);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msg.what = FAIL;
				e.printStackTrace();
			}
			try {
				Express bean = remot.scanExpress(num);
				// PhoneInfo phone = new PhoneInfo();
				// phone.setBdUid(RandomStringUtils.randomAlphanumeric(15));
				//
				// remot.uploadPhoneInfo(phone);
				Log.e("gddddd", "测试");
				List<ExpressData> datas = bean.getData();
				msg.obj = datas;
			} catch (Exception e) {
				msg.what = FAIL;
				e.printStackTrace();
			}
			hand.sendMessage(msg);
		}
	};

}
