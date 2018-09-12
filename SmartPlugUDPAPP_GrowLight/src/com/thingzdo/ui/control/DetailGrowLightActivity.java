package com.thingzdo.ui.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class DetailGrowLightActivity extends TitledActivity
		implements
			OnClickListener {

	private TextView tv_light_curtime;
	private ImageView iv_light_control;
	private ImageView iv_light_control_manual;
	private ImageView iv_light_control_auto;
	private ImageView iv_light_control_timetask;
	private ImageView iv_light_control_timecurve;
	private ImageView iv_light_control_setting;

	private Spinner spinner_workmode;
	private List<String> irlist = new ArrayList<String>();

	private int i_Current_WorkMode = 0; // 0：未知 1: 手工 2： 自动 3. 时间曲线 4. 定时任务
	private int i_Current_Temperature = 0;
	private int i_Set_Temperature = 0;
	private String str_curtime = "00:00:00";
	private String str_curtime_simple = "00:00:00";
	private String str_sunup = "06:00:00";
	private String str_sundown = "18:00:00";
	private int i_light_01 = 0;
	private int i_light_02 = 0;
	private int i_light_03 = 0;
	private int i_light_04 = 0;
	private int i_light_05 = 0;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private boolean m_isOpen = false;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {

			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_QRY_STATUS_ACTION)) {

				i_Current_WorkMode = intent.getIntExtra("WORKMODE", 0);
				i_Current_Temperature = intent.getIntExtra("CURTEMPERATURE", 0);
				// i_Set_Temperature = intent.getIntExtra("SETTEMPERATURE", 0);
				str_curtime = intent.getStringExtra("CURTIME");
				str_sunup = intent.getStringExtra("SUNUPTIME");
				str_sundown = intent.getStringExtra("SUNDOWNTIME");

				i_light_01 = intent.getIntExtra("LIGHT01", 0);
				i_light_02 = intent.getIntExtra("LIGHT02", 0);
				i_light_03 = intent.getIntExtra("LIGHT03", 0);
				i_light_04 = intent.getIntExtra("LIGHT04", 0);
				i_light_05 = intent.getIntExtra("LIGHT05", 0);

				saveData();
				updateUI();
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_WORK_MODE_ACTION)) {
				i_Current_WorkMode = intent.getIntExtra("WORKMODE", 0);
				spinner_workmode.setSelection(i_Current_WorkMode);

				saveData();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight, false);
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

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_QRY_STATUS_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_WORK_MODE_ACTION);
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

		loadData();

		getStatus();

		updateUI();
	}
	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putInt("WORKMODE" + mPlugId, i_Current_WorkMode);
		editor.putInt("CURTEMPERATURE" + mPlugId, i_Current_Temperature);
		editor.putInt("SETTEMPERATURE" + mPlugId, i_Set_Temperature);
		editor.putString("SUNUPTIME" + mPlugId, str_sunup);
		editor.putString("SUNDOWNTIME" + mPlugId, str_sundown);
		editor.putInt("LIGHT01" + mPlugId, i_light_01);
		editor.putInt("LIGHT02" + mPlugId, i_light_02);
		editor.putInt("LIGHT03" + mPlugId, i_light_03);
		editor.putInt("LIGHT04" + mPlugId, i_light_04);
		editor.putInt("LIGHT05" + mPlugId, i_light_05);

		editor.commit();
	}

	private void loadData() {
		i_Current_WorkMode = mSharedPreferences.getInt("WORKMODE" + mPlugId, 0);
		i_Current_Temperature = mSharedPreferences.getInt("CURTEMPERATURE"
				+ mPlugId, 0);
		i_Set_Temperature = mSharedPreferences.getInt("SETTEMPERATURE"
				+ mPlugId, 0);
		str_sundown = mSharedPreferences.getString("SUNUPTIME" + mPlugId,
				"06:00:00");
		str_sundown = mSharedPreferences.getString("SUNDOWNTIME" + mPlugId,
				"18:00:00");

		i_light_01 = mSharedPreferences.getInt("LIGHT01" + mPlugId, 0);
		i_light_02 = mSharedPreferences.getInt("LIGHT02" + mPlugId, 0);
		i_light_03 = mSharedPreferences.getInt("LIGHT03" + mPlugId, 0);
		i_light_04 = mSharedPreferences.getInt("LIGHT04" + mPlugId, 0);
		i_light_05 = mSharedPreferences.getInt("LIGHT05" + mPlugId, 0);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					tv_light_curtime.setText(str_curtime_simple);
					break;
				case 1 :
					break;
				default :
					break;
			}
		};
	};

	class spinnerIRListener
			implements
				android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			i_Current_WorkMode = position;
			String str_workmode_name = parent.getItemAtPosition(position)
					.toString();

			setWorkMode(i_Current_WorkMode);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private void getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_STATUS)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void setWorkMode(int data_type) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_WORKMODE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data_type);

		sendMsg(true, sb.toString(), true);
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
					intent.setClass(DetailGrowLightActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.iv_light_control :
				m_isOpen = !m_isOpen;
				light_control(m_isOpen);

				iv_light_control.setImageDrawable(getResources().getDrawable(
						m_isOpen == true
								? R.drawable.smp_light_on_big
								: R.drawable.smp_light_off_big));
				break;
			case R.id.iv_light_control_manual :
				light_control_manual();
				break;
			case R.id.iv_light_control_auto :
				light_control_auto();
				break;
			case R.id.iv_light_control_timetask :
				light_control_timetask();
				break;
			case R.id.iv_light_control_timecurve :
				light_control_timecurve();
				break;
			case R.id.iv_light_control_setting :
				light_control_setting();
				break;
			default :
				break;
		}
	}

	private void query_status() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_STATUS)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void light_control(boolean isOpen) {
		String data = "255,255,255,255,255";
		if (isOpen == false) {
			data = "0,0,0,0,0";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_BRIGHT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data);

		sendMsg(true, sb.toString(), true);
	}

	private void light_control_manual() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightActivity.this,
				DetailGrowLightManualActivity.class);
		startActivity(intent);
	}

	private void light_control_auto() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightActivity.this,
				DetailGrowLightAutoActivity.class);
		startActivity(intent);
	}

	private void light_control_timetask() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightActivity.this,
				DetailGrowLightTimeTaskActivity.class);
		startActivity(intent);

	}

	private void light_control_timecurve() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightActivity.this,
				DetailGrowLightTimeCurveActivity.class);
		startActivity(intent);

	}

	private void light_control_setting() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightActivity.this,
				DetailGrowLightSettingActivity.class);
		startActivity(intent);

	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		tv_light_curtime = (TextView) findViewById(R.id.tv_light_curtime);
		iv_light_control = (ImageView) findViewById(R.id.iv_light_control);
		iv_light_control_manual = (ImageView) findViewById(R.id.iv_light_control_manual);
		iv_light_control_auto = (ImageView) findViewById(R.id.iv_light_control_auto);
		iv_light_control_timetask = (ImageView) findViewById(R.id.iv_light_control_timetask);
		iv_light_control_timecurve = (ImageView) findViewById(R.id.iv_light_control_timecurve);
		iv_light_control_setting = (ImageView) findViewById(R.id.iv_light_control_setting);

		iv_light_control.setImageResource(m_isOpen
				? R.drawable.smp_light_on_big
				: R.drawable.smp_light_off_big);

		spinner_workmode = (Spinner) findViewById(R.id.spinner_workmode);

		iv_light_control.setOnClickListener(this);
		iv_light_control_manual.setOnClickListener(this);
		iv_light_control_auto.setOnClickListener(this);
		iv_light_control_timetask.setOnClickListener(this);
		iv_light_control_timecurve.setOnClickListener(this);
		iv_light_control_setting.setOnClickListener(this);

		// init spinner data
		irlist.clear();
		Resources res = SmartPlugApplication.getInstance().getResources();
		irlist.add(res.getString(R.string.smartplug_growlight_unknown));
		irlist.add(res.getString(R.string.smartplug_growlight_manual));
		irlist.add(res.getString(R.string.smartplug_growlight_auto));
		irlist.add(res.getString(R.string.smartplug_growlight_timecurve));
		irlist.add(res.getString(R.string.smartplug_growlight_timetask));

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.activity_detail_aircon2item, R.id.tv_item, irlist);
		spinner_workmode.setAdapter(adapter);
		spinner_workmode.setPrompt("测试");
		spinner_workmode.setOnItemSelectedListener(new spinnerIRListener());

		if (irlist.size() > 0) {
			spinner_workmode.setSelection(i_Current_WorkMode);
		}
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

		spinner_workmode.setSelection(i_Current_WorkMode);

		// 使用定时器来 更新 时间
		new Timer("GrowLightTime").schedule(new TimerTask() {
			@Override
			public void run() {
				// show NE Time
				getNextSecond();
				mHandler.sendEmptyMessage(0);
				// tv_light_curtime.setText(str_curtime);
			}
		}, 500, 1000);
	}

	private String getNextSecond() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(str_curtime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.SECOND, 1);
		str_curtime = sdf.format(cal.getTime());

		sdf = new SimpleDateFormat("hh:mm:ss");
		try {
			cal.setTime(sdf.parse(str_curtime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		str_curtime_simple = sdf.format(cal.getTime());

		return str_curtime;
	}

}
