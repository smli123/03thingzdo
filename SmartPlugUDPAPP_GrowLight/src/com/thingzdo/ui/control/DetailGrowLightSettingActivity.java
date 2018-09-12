package com.thingzdo.ui.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;

public class DetailGrowLightSettingActivity extends TitledActivity
		implements
			OnClickListener {

	private RelativeLayout ll_curtime;
	private RelativeLayout ll_temperature_control;
	private RelativeLayout ll_lushu_control;
	private TextView tv_light_curtime;
	private TextView tv_light_temperature_control;
	private TextView tv_light_lushu_control;

	private int i_Current_Temperature = 0;
	private int i_Set_Temperature = 0;
	private int i_Current_lushu = 5;
	private String str_curtime = "00:00:00";
	private String str_curtime_simple = "00:00:00";

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private Resources mResource;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightSettingActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailGrowLightSettingActivity.this, intent)) {
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
				R.layout.activity_plug_detail_growlight_setting, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		mResource = SmartPlugApplication.getInstance().getResources();

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

		loadData();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		updateUI();
	}

	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putInt("CURTEMPERATURE" + mPlugId, i_Current_Temperature);
		editor.putInt("SETTEMPERATURE" + mPlugId, i_Set_Temperature);
		editor.putInt("SETROUTES" + mPlugId, i_Current_lushu);

		editor.commit();
	}

	private void loadData() {
		i_Current_Temperature = mSharedPreferences.getInt("CURTEMPERATURE"
				+ mPlugId, 0);
		i_Set_Temperature = mSharedPreferences.getInt("SETTEMPERATURE"
				+ mPlugId, 0);
		i_Current_lushu = mSharedPreferences.getInt("SETROUTES" + mPlugId, 5);
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
					intent.setClass(DetailGrowLightSettingActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightSettingActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.ll_temperature_control :
				control_temperature();
				break;
			case R.id.ll_lushu_control :
				control_lushu();
				break;
			default :
				break;
		}
	}

	private void control_temperature() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.setClass(DetailGrowLightSettingActivity.this,
				DetailGrowLigthSettingTemperatureControl.class);
		startActivity(intent);
	}

	private void control_lushu() {

		ActionSheetDialog dlg = new ActionSheetDialog(
				DetailGrowLightSettingActivity.this)
				.builder()
				.setTitle(
						DetailGrowLightSettingActivity.this
								.getString(R.string.smartplug_growlight_lushu_control_memo))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		for (int i = 1; i <= 5; i++) {
			dlg.addSheetItem(
					String.valueOf(i)
							+ mResource
									.getString(R.string.smartplug_growlight_lushu_control_danwei),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 0;
							msg.arg1 = which + 1;
							mHandler.sendMessage(msg);
						}
					});
		}
		dlg.show();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					i_Current_lushu = msg.arg1;
					saveData();

					tv_light_lushu_control
							.setText(String.valueOf(i_Current_lushu)
									+ mResource
											.getString(R.string.smartplug_growlight_lushu_control_danwei));
					break;
				case 1 :
					break;
				default :
					break;
			}
		};
	};

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(SmartPlugApplication.getInstance().getResources()
				.getString(R.string.smartplug_growlight_setting));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		tv_light_curtime = (TextView) findViewById(R.id.tv_light_curtime);
		tv_light_temperature_control = (TextView) findViewById(R.id.tv_light_temperature_control);
		tv_light_lushu_control = (TextView) findViewById(R.id.tv_light_lushu_control);

		ll_curtime = (RelativeLayout) findViewById(R.id.ll_curtime);
		ll_temperature_control = (RelativeLayout) findViewById(R.id.ll_temperature_control);
		ll_lushu_control = (RelativeLayout) findViewById(R.id.ll_lushu_control);

		ll_temperature_control.setOnClickListener(this);
		ll_lushu_control.setOnClickListener(this);
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

		tv_light_temperature_control.setText(String.valueOf(i_Set_Temperature));
		tv_light_lushu_control
				.setText(String.valueOf(i_Current_lushu)
						+ mResource
								.getString(R.string.smartplug_growlight_lushu_control_danwei));
	}
}
