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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import cn.wang.yin.hessian.api.Remot;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.RemoteFactoryUtils;

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
		button1 = (Button) findViewById(R.id.button1);
		listView1 = (ListView) findViewById(R.id.listView1);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String express_num = editText1.getText().toString();
				if (StringUtils.isNotBlank(express_num)) {
					num = express_num;
					Thread t=new Thread(scanrunnable);
					t.run();
				}
			}
		});

	}

	public void fresh(List<ExpressData> datas) {
		// List<ExpressData> datas = (List<ExpressData>) msg.obj;
		for (ExpressData bean : datas) {
			all.add(bean.getContext());
		}
		ArrayAdapter<String> adapterzhouqi = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, all);
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
						.setTitle("��ʾ")
						.setMessage("��Ҫ���磬�����ֻ���ǰ���粻���ã���������������")
						.setPositiveButton("ȷ��",
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
				remot = RemoteFactoryUtils.getReport();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msg.what = FAIL;
				e.printStackTrace();
			}
			try {
				Express bean = remot.scanExpress(num);
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
