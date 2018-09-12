package com.thingzdo.ui.control;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailSmartCarActivity extends TitledActivity
		implements
			OnClickListener,
			SensorEventListener {
	private static final String TAG = DetailSmartCarActivity.class
			.getSimpleName();

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
	private Calendar mCalendar;
	private long lasttimestamp = 0;

	private int value_left = 100;
	private int value_right = 100;
	private int value_step = 50;

	private RelativeLayout ll_controller_left;

	private CheckBox cb_sensor;

	private Button btn_forward;
	private Button btn_backward;
	private Button btn_left;
	private Button btn_right;
	private Button btn_stop;
	private Button btn_speedup;
	private Button btn_speeddown;
	MediaPlayer mp3_player = null;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailSmartCarActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailSmartCarActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_smartcar,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		setTitleRightButton(R.string.smartplug_title_plug_detail,
				R.drawable.title_btn_selector, this);

		if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
			setTitleLeftButton(R.string.smartplug_goback,
					R.drawable.title_btn_selector, this);
		} else {
			setTitleLeftButton(R.string.smartplug_esc,
					R.drawable.title_btn_selector, this);
		}

		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			setTitleRightButton(R.string.smartplug_mgr_reselect,
					R.drawable.title_btn_selector, this);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_TV_IRDATA_ACTION);
		filter.addAction(PubDefine.PLUG_TV_SERVER_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");

		UDPClient.getInstance().setIPAddress(mPlugIp);

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
	}

	private void initSensor() {
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
		if (null == mSensorManager) {
			Toast.makeText(this, "设备不支持重力传感器", Toast.LENGTH_SHORT).show();
		}

		// 参数三，检测的精准度
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL); // SENSOR_DELAY_GAME
	}

	private void destroySensor() {
		mSensorManager.unregisterListener(this, mSensor);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == null) {
			return;
		}

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			int x = (int) event.values[0];
			int y = (int) event.values[1];
			int z = (int) event.values[2];

			mCalendar = Calendar.getInstance();
			long stamp = mCalendar.getTimeInMillis() / 1000l; // 1393844912

			TextView tv_x = (TextView) findViewById(R.id.tv_x);
			TextView tv_y = (TextView) findViewById(R.id.tv_y);
			TextView tv_z = (TextView) findViewById(R.id.tv_z);
			tv_x.setText(String.valueOf(x));
			tv_y.setText(String.valueOf(y));
			tv_z.setText(String.valueOf(z));
			// solution 1:
			if ((stamp - lasttimestamp) > 1) {
				int THRESHOLD = 4;
				int type = 0;
				int maxvalue = 0;

				if (x >= THRESHOLD) { // LEFT
					type = 1;
					maxvalue = x;
				} else if (x <= -1 * THRESHOLD) { // RIGHT
					type = 2;
					maxvalue = -1 * x;
				} else if (y >= THRESHOLD) { // DOWN
					type = 3;
					maxvalue = y;

				} else if (y <= -1 * THRESHOLD) { // UP
					type = 4;
					maxvalue = y;

				} else if (z >= THRESHOLD) { // UNUSE

				} else if (z <= -1 * THRESHOLD) { // UNUSE

				} else {

				}

				// 检测手机在移动..
				if (type > 0) {
					lasttimestamp = stamp;

					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = type;
					msg.arg2 = maxvalue;
					mHandler.sendMessage(msg);
				}
			}

			// solution 2:
			// int px = Math.abs(mX - x);
			// int py = Math.abs(mY - y);
			// int pz = Math.abs(mZ - z);
			// Log.d(TAG, "pX:" + px + "  pY:" + py + "  pZ:" + pz +
			// "    stamp:"
			// + stamp + "  second:" + second);
			// int maxvalue = getMaxValue(px, py, pz);
			// if (maxvalue > 2 && (stamp - lasttimestamp) > 3) {
			// lasttimestamp = stamp;
			// Log.d(TAG, " sensor isMoveorchanged....");
			//
			// // 检测手机在移动..
			// Message msg = new Message();
			// msg.what = 1;
			// msg.arg1 = getMaxIndex(px, py, pz); // 1: x, 2: y, 3: z
			// msg.arg2 = maxvalue;
			// mHandler.sendMessage(msg);
			// }
			//
			// mX = x;
			// mY = y;
			// mZ = z;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1 :
					int index = msg.arg1;
					int maxvalue = msg.arg2;

					if (index == 1) {
						SendCommand("LEFT", value_left, value_right);
					} else if (index == 2) {
						SendCommand("RIGHT", value_left, value_right);
					} else if (index == 3) {
						SendCommand("BACKWARD", value_left, value_right);
					} else if (index == 4) {
						SendCommand("FORWARD", value_left, value_right);
					}
					break;
				default :
					break;
			}
		};
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();

		// 播放声音的初始化
		if (mp3_player == null) {
			mp3_player = new MediaPlayer();
			mp3_player = MediaPlayer.create(this, R.raw.aircondi);
			mp3_player.setLooping(false);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mp3_player != null) {
			mp3_player.release();
			mp3_player = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mDetailRev);

		destroySensor();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread.setRunning(false);
		}
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.titlebar_leftbutton : // WiFi模式 退出时，需要close掉 TCP连接
				disconnectSocket();
				finish();
				break;
			case R.id.titlebar_rightbutton :
				Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
				// Internet模式：“详情”界面
				if (btn_TitleRight.getText().equals(
						getString(R.string.smartplug_title_plug_detail))) {
					Intent intent = new Intent();
					intent.putExtra("PLUGID", mPlugId);
					intent.setClass(DetailSmartCarActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailSmartCarActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.cb_sensor :
				boolean result = cb_sensor.isChecked();
				if (result == true) {
					ll_controller_left.setVisibility(View.GONE);
					initSensor();
				} else {
					ll_controller_left.setVisibility(View.VISIBLE);
					destroySensor();
				}
				break;
			case R.id.btn_forward :
				SendCommand("FORWARD", value_left, value_right);
				break;
			case R.id.btn_backward :
				SendCommand("BACKWARD", value_left, value_right);
				break;
			case R.id.btn_left :
				SendCommand("LEFT", value_left / 2, value_right);
				break;
			case R.id.btn_right :
				SendCommand("RIGHT", value_left, value_right / 2);
				break;
			case R.id.btn_stop :
				SendCommand("STOP", value_left, value_right);
				break;
			case R.id.btn_speedup :
				value_left += value_step;
				value_right += value_step;
				SendCommand("SPEEDUP", value_left, value_right);
				break;
			case R.id.btn_speeddown :
				value_left -= value_step;
				value_right -= value_step;
				SendCommand("SPEEDDOWN", value_left, value_right);
				break;
			default :
				break;
		}
	}

	private void SendCommand(String mode, int vleft, int vright) {
		mp3_player.start();

		// 0,APPPASSTHROUGH,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712,3_1#
		String command_base = "0,ACTION," + PubStatus.g_CurUserName + ","
				+ mPlugId + "," + mode + "," + String.valueOf(vleft) + ","
				+ String.valueOf(vright);
		int command_base_length = command_base.getBytes().length + 1;
		// String command = "APPPASSTHROUGH," + UserName + "," + ModuldID + ","
		// + String.valueOf(command_base_length) + "," + command_base
		// + "#";

		StringBuffer sb = new StringBuffer();
		sb.append("APPPASSTHROUGH")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(command_base_length))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(command_base);

		sendMsg(true, sb.toString(), true);
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(mPlug.mPlugName);

		ll_controller_left = (RelativeLayout) findViewById(R.id.ll_controller_left);
		btn_forward = (Button) findViewById(R.id.btn_forward);
		btn_backward = (Button) findViewById(R.id.btn_backward);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_speedup = (Button) findViewById(R.id.btn_speedup);
		btn_speeddown = (Button) findViewById(R.id.btn_speeddown);

		cb_sensor = (CheckBox) findViewById(R.id.cb_sensor);
		cb_sensor.setChecked(false);
		cb_sensor.setOnClickListener(this);

		btn_forward.setOnClickListener(this);
		btn_backward.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_speedup.setOnClickListener(this);
		btn_speeddown.setOnClickListener(this);
	}

	private void disconnectSocket() {
		// WiFi 直连模式下，退出或者重选时，必须close TCP连接；
		/*
		 * if (PubDefine.g_Connect_Mode ==
		 * PubDefine.SmartPlug_Connect_Mode.WiFi) {
		 * SmartPlugWifiMgr.disconnectSocket(); }
		 */

		return;
	}

	private void updateUI() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}
		setTitle(mPlug.mPlugName);
	}

}
