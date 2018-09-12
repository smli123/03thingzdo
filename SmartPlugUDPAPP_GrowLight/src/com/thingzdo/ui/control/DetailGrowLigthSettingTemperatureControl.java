package com.thingzdo.ui.control;

import java.util.Timer;
import java.util.TimerTask;

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

public class DetailGrowLigthSettingTemperatureControl extends TitledActivity
		implements
			OnClickListener,
			SeekBar.OnSeekBarChangeListener {

	private TextView tv_light_curtemperature;
	private TextView tv_set_temperature_result;
	private SeekBar sb_set_temperature;

	private int i_Current_Temperature = 0;
	private int i_Set_Temperature = 0;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLigthSettingTemperatureControl.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_TEMPERATURE_ACTION)) {
				i_Set_Temperature = intent.getIntExtra("SETTEMPERATURE", 0);

				saveData();
				updateUI();
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_QRY_STATUS_ACTION)) {

				i_Current_Temperature = intent.getIntExtra("CURTEMPERATURE", 0);

				saveData();
				updateUI();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(
				savedInstanceState,
				R.layout.activity_plug_detail_growlight_setting_temperature_control,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_TEMPERATURE_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_QRY_STATUS_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");

		UDPClient.getInstance().setIPAddress(mPlugIp);

		loadData();

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		updateUI();

		// 使用定时器来 更新 时间
		new Timer("GrowLightCurrentTemperature").schedule(new TimerTask() {
			@Override
			public void run() {
				getCurrentTemperature();
			}
		}, 1000, 10000);
	}

	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putInt("CURTEMPERATURE" + mPlugId, i_Current_Temperature);
		editor.putInt("SETTEMPERATURE" + mPlugId, i_Set_Temperature);

		editor.commit();
	}

	private void loadData() {
		i_Current_Temperature = mSharedPreferences.getInt("CURTEMPERATURE"
				+ mPlugId, 0);
		i_Set_Temperature = mSharedPreferences.getInt("SETTEMPERATURE"
				+ mPlugId, 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();

		loadData();
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
					intent.setClass(
							DetailGrowLigthSettingTemperatureControl.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(
							DetailGrowLigthSettingTemperatureControl.this,
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
				.getString(R.string.smartplug_growlight_setting));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		tv_light_curtemperature = (TextView) findViewById(R.id.tv_light_curtemperature);
		tv_set_temperature_result = (TextView) findViewById(R.id.tv_set_temperature_result);
		sb_set_temperature = (SeekBar) findViewById(R.id.sb_set_temperature);

		if (null != sb_set_temperature) {
			sb_set_temperature
					.setProgress(re_adjust_progressbar(i_Set_Temperature));
			sb_set_temperature.setOnSeekBarChangeListener(this);
			tv_set_temperature_result
					.setText(String.valueOf(i_Set_Temperature));
		}

	}
	@Override
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		switch (bar.getId()) {
			case R.id.sb_set_temperature :
				int value = adjust_progressbar(bar.getProgress());
				tv_set_temperature_result.setText(String.valueOf(value));
				break;
			default :
				break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		switch (bar.getId()) {
			case R.id.sb_set_temperature :
				// adjust Bar's progress position. step = 5;
				int value = adjust_progressbar(bar.getProgress());

				i_Set_Temperature = value;
				tv_set_temperature_result.setText(String.valueOf(value));
				saveData();

				SetTemperator();
				break;
			default :
				break;
		}
	}

	private void getCurrentTemperature() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_STATUS)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}
	private void SetTemperator() {
		String data = String.valueOf(i_Set_Temperature);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_TEMPERATURE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data);

		sendMsg(true, sb.toString(), true);
	}

	private int adjust_progressbar(int bar_pos) {
		int trans_pos = bar_pos + 35;
		return trans_pos;
	}

	private int re_adjust_progressbar(int value) {
		int trans_pos = value - 35;
		return trans_pos;
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

		tv_light_curtemperature.setText(String.valueOf(i_Current_Temperature));
		tv_set_temperature_result.setText(String.valueOf(i_Set_Temperature));
		sb_set_temperature
				.setProgress(re_adjust_progressbar(i_Set_Temperature));
	}
}
