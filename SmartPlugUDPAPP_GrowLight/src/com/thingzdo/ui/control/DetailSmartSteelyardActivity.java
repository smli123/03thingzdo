package com.thingzdo.ui.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailSmartSteelyardActivity extends TitledActivity
		implements
			OnClickListener {
	private static final String TAG = DetailSmartSteelyardActivity.class
			.getSimpleName();

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private TextView tv_gravity;
	private Button btn_query;
	private Button btn_qupi;
	MediaPlayer mp3_player = null;

	private double d_weight = 0.0f;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailSmartSteelyardActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_STEELYARD_GETWEIGHT_ACTION)) {
				d_weight = intent.getDoubleExtra("STATUS", 0.0);

				updateUI();
			}
			if (intent.getAction().equals(
					PubDefine.PLUG_STEELYARD_GETMAOPI_ACTION)) {
				d_weight = intent.getDoubleExtra("STATUS", 0.0);
				updateUI();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_detail_smartsteelyard, false);
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
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_STEELYARD_GETWEIGHT_ACTION);
		filter.addAction(PubDefine.PLUG_STEELYARD_GETMAOPI_ACTION);
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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1 :
					int index = msg.arg1;
					int maxvalue = msg.arg2;

					if (index == 1) {
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
					intent.setClass(DetailSmartSteelyardActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailSmartSteelyardActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.btn_query :
				Query_Gravity();
				break;
			case R.id.btn_qupi :
				Qupi_Gravity();
				break;
			default :
				break;
		}
	}

	private void Query_Gravity() {
		mp3_player.start();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_STEELYARD_APPGETWEIGHT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void Qupi_Gravity() {
		mp3_player.start();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_STEELYARD_APPGETMAOPI)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(mPlug.mPlugName);

		tv_gravity = (TextView) findViewById(R.id.tv_gravity);
		btn_query = (Button) findViewById(R.id.btn_query);
		btn_qupi = (Button) findViewById(R.id.btn_qupi);

		// btn_query.setBackgroundColor(R.color.blue);
		// btn_qupi.setBackgroundColor(R.color.blue);

		btn_query.setOnClickListener(this);
		btn_qupi.setOnClickListener(this);
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

		tv_gravity.setText(String.valueOf(d_weight) + "克");
	}

}
