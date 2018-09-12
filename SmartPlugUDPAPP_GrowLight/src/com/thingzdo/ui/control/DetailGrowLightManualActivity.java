package com.thingzdo.ui.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
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

public class DetailGrowLightManualActivity extends TitledActivity
		implements
			OnClickListener,
			SeekBar.OnSeekBarChangeListener {

	private SeekBar sb_light_01;
	private SeekBar sb_light_02;
	private SeekBar sb_light_03;
	private SeekBar sb_light_04;
	private SeekBar sb_light_05;

	private TextView tv_light_right_01;
	private TextView tv_light_right_02;
	private TextView tv_light_right_03;
	private TextView tv_light_right_04;
	private TextView tv_light_right_05;

	private int value_light_01_pos = 0;
	private int value_light_02_pos = 0;
	private int value_light_03_pos = 0;
	private int value_light_04_pos = 0;
	private int value_light_05_pos = 0;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private int i_light_01 = 0;
	private int i_light_02 = 0;
	private int i_light_03 = 0;
	private int i_light_04 = 0;
	private int i_light_05 = 0;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightManualActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailGrowLightManualActivity.this, intent)) {
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
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_manual, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
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

	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putInt("LIGHT01" + mPlugId, i_light_01);
		editor.putInt("LIGHT02" + mPlugId, i_light_02);
		editor.putInt("LIGHT03" + mPlugId, i_light_03);
		editor.putInt("LIGHT04" + mPlugId, i_light_04);
		editor.putInt("LIGHT05" + mPlugId, i_light_05);

		editor.commit();
	}

	private void loadData() {
		i_light_01 = mSharedPreferences.getInt("LIGHT01" + mPlugId, 0);
		i_light_02 = mSharedPreferences.getInt("LIGHT02" + mPlugId, 0);
		i_light_03 = mSharedPreferences.getInt("LIGHT03" + mPlugId, 0);
		i_light_04 = mSharedPreferences.getInt("LIGHT04" + mPlugId, 0);
		i_light_05 = mSharedPreferences.getInt("LIGHT05" + mPlugId, 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
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
					intent.setClass(DetailGrowLightManualActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightManualActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			default :
				break;
		}
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(SmartPlugApplication.getInstance().getResources()
				.getString(R.string.smartplug_growlight_manual));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		sb_light_01 = (SeekBar) findViewById(R.id.sb_light_01);
		sb_light_02 = (SeekBar) findViewById(R.id.sb_light_02);
		sb_light_03 = (SeekBar) findViewById(R.id.sb_light_03);
		sb_light_04 = (SeekBar) findViewById(R.id.sb_light_04);
		sb_light_05 = (SeekBar) findViewById(R.id.sb_light_05);

		tv_light_right_01 = (TextView) findViewById(R.id.tv_light_right_01);
		tv_light_right_02 = (TextView) findViewById(R.id.tv_light_right_02);
		tv_light_right_03 = (TextView) findViewById(R.id.tv_light_right_03);
		tv_light_right_04 = (TextView) findViewById(R.id.tv_light_right_04);
		tv_light_right_05 = (TextView) findViewById(R.id.tv_light_right_05);

		if (null != sb_light_01) {
			sb_light_01.setProgress(value_light_01_pos);
			sb_light_01.setOnSeekBarChangeListener(this);
			tv_light_right_01.setText(String.valueOf(value_light_01_pos));
		}
		if (null != sb_light_02) {
			sb_light_02.setProgress(value_light_02_pos);
			sb_light_02.setOnSeekBarChangeListener(this);
			tv_light_right_02.setText(String.valueOf(value_light_02_pos));
		}
		if (null != sb_light_03) {
			sb_light_03.setProgress(value_light_03_pos);
			sb_light_03.setOnSeekBarChangeListener(this);
			tv_light_right_03.setText(String.valueOf(value_light_03_pos));
		}
		if (null != sb_light_04) {
			sb_light_04.setProgress(value_light_04_pos);
			sb_light_04.setOnSeekBarChangeListener(this);
			tv_light_right_04.setText(String.valueOf(value_light_04_pos));
		}
		if (null != sb_light_05) {
			sb_light_05.setProgress(value_light_05_pos);
			sb_light_05.setOnSeekBarChangeListener(this);
			tv_light_right_01.setText(String.valueOf(value_light_05_pos));
		}
	}

	@Override
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		switch (bar.getId()) {
			case R.id.sb_light_01 :
				tv_light_right_01.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_02 :
				tv_light_right_02.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_03 :
				tv_light_right_03.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_04 :
				tv_light_right_04.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_05 :
				tv_light_right_05.setText(String.valueOf(bar.getProgress()));
				break;
			default :
				break;
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		switch (bar.getId()) {
			case R.id.sb_light_01 :
				value_light_01_pos = bar.getProgress();
				tv_light_right_01.setText(String.valueOf(value_light_01_pos));

				LightPos();
				break;
			case R.id.sb_light_02 :
				value_light_02_pos = bar.getProgress();
				tv_light_right_02.setText(String.valueOf(value_light_02_pos));

				LightPos();
				break;
			case R.id.sb_light_03 :
				value_light_03_pos = bar.getProgress();
				tv_light_right_03.setText(String.valueOf(value_light_03_pos));

				LightPos();
				break;
			case R.id.sb_light_04 :
				value_light_04_pos = bar.getProgress();
				tv_light_right_04.setText(String.valueOf(value_light_04_pos));

				LightPos();
				break;
			case R.id.sb_light_05 :
				value_light_05_pos = bar.getProgress();
				tv_light_right_05.setText(String.valueOf(value_light_02_pos));

				LightPos();
				break;
			default :
				break;
		}
	}

	private void LightPos() {
		String data = String.valueOf(value_light_01_pos) + ","
				+ String.valueOf(value_light_02_pos) + ","
				+ String.valueOf(value_light_03_pos) + ","
				+ String.valueOf(value_light_04_pos) + ","
				+ String.valueOf(value_light_05_pos);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_BRIGHT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data);

		sendMsg(true, sb.toString(), true);
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
