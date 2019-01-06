package com.thingzdo.ui.growlightble;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailGrowLightBleActivity extends DetailGrowLightBleBase
		implements
			OnClickListener {

	private TextView tv_light_curtime;
	private ImageView iv_light_control;
	private ImageView iv_light_control_manual;
	private ImageView iv_light_control_auto;
	private ImageView iv_light_control_timetask;
	private ImageView iv_light_control_timecurve;
	private ImageView iv_light_control_timecurve_new;
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
	private String sun_peroid = "1111111";
	private int i_light_01 = 0;
	private int i_light_02 = 0;
	private int i_light_03 = 0;
	private int i_light_04 = 0;
	private int i_light_05 = 0;

	private int i_Current_lushu = 5;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private boolean mOnline = false;

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
				sun_peroid = intent.getStringExtra("SUNPEROID");

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
				// spinner_workmode.setSelection(i_Current_WorkMode);

				saveData();
				updateUI();
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_BRIGHT_ACTION)) {
				i_light_01 = intent.getIntExtra("LIGHT01", 0);
				i_light_02 = intent.getIntExtra("LIGHT02", 0);
				i_light_03 = intent.getIntExtra("LIGHT03", 0);
				i_light_04 = intent.getIntExtra("LIGHT04", 0);
				i_light_05 = intent.getIntExtra("LIGHT05", 0);

				saveData();

				updateUI();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight, false);
		// super.onCreate(savedInstanceState,
		// R.layout.activity_plug_detail_growlight, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_QRY_STATUS_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_WORK_MODE_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_BRIGHT_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");
		mOnline = intent.getBooleanExtra("ONLINE", false);

		UDPClient.getInstance().setIPAddress(mPlugIp);

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		loadData();
		i_Current_WorkMode = 0; // Need Delete

		getStatus();

		updateUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		// loadData(); // Need Delete Note
		init();

		// Need Delete
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
		editor.putString("SUNPEROID" + mPlugId, sun_peroid);
		editor.putInt("LIGHT01" + mPlugId, i_light_01);
		editor.putInt("LIGHT02" + mPlugId, i_light_02);
		editor.putInt("LIGHT03" + mPlugId, i_light_03);
		editor.putInt("LIGHT04" + mPlugId, i_light_04);
		editor.putInt("LIGHT05" + mPlugId, i_light_05);

		editor.commit();
	}

	private void loadData() {
		// Must Be Modify
		i_Current_WorkMode = mSharedPreferences.getInt("WORKMODE" + mPlugId, 0);

		i_Current_Temperature = mSharedPreferences.getInt("CURTEMPERATURE"
				+ mPlugId, 0);
		i_Set_Temperature = mSharedPreferences.getInt("SETTEMPERATURE"
				+ mPlugId, 0);
		str_sundown = mSharedPreferences.getString("SUNUPTIME" + mPlugId,
				"06:00:00");
		str_sundown = mSharedPreferences.getString("SUNDOWNTIME" + mPlugId,
				"18:00:00");
		sun_peroid = mSharedPreferences.getString("SUNPEROID" + mPlugId,
				"1111111");

		i_light_01 = mSharedPreferences.getInt("LIGHT01" + mPlugId, 0);
		i_light_02 = mSharedPreferences.getInt("LIGHT02" + mPlugId, 0);
		i_light_03 = mSharedPreferences.getInt("LIGHT03" + mPlugId, 0);
		i_light_04 = mSharedPreferences.getInt("LIGHT04" + mPlugId, 0);
		i_light_05 = mSharedPreferences.getInt("LIGHT05" + mPlugId, 0);

		i_Current_lushu = mSharedPreferences.getInt("SETROUTES" + mPlugId, 5);
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
				// Button btn_TitleRight = (Button)
				// findViewById(R.id.titlebar_rightbutton);
				// // Internet模式：“详情”界面
				// if (btn_TitleRight.getText().equals(
				// getString(R.string.smartplug_title_plug_detail))) {
				// Intent intent = new Intent();
				// intent.putExtra("PLUGID", mPlugId);
				// intent.putExtra("ONLINE", mOnline);
				// intent.setClass(DetailGrowLightBleActivity.this,
				// PlugDetailInfoActivity.class);
				// startActivity(intent);
				// } else {
				// // WiFi直连：“重选”界面
				// // PubDefine.disconnect();
				// disconnectSocket();
				// Intent intent = new Intent();
				// intent.setClass(DetailGrowLightBleActivity.this,
				// AddSocketActivity2.class);
				// startActivity(intent);
				// if (PubDefine.SmartPlug_Connect_Mode.WiFi !=
				// PubDefine.g_Connect_Mode) {
				// finish();
				// }
				// }
				break;
			case R.id.iv_light_control :
				m_isOpen = !m_isOpen;
				light_control(m_isOpen);

				iv_light_control.setImageDrawable(getResources().getDrawable(
						m_isOpen == true
								? R.drawable.smp_growlight_on_big
								: R.drawable.smp_growlight_off_big));
				break;
			case R.id.iv_light_control_manual :
				// i_Current_WorkMode = 1; // Need Delete Note
				// setWorkMode(i_Current_WorkMode);
				setWorkMode(1);
				light_control_manual();
				break;
			case R.id.iv_light_control_auto :
				// i_Current_WorkMode = 2; // Need Delete Note
				// setWorkMode(i_Current_WorkMode);
				setWorkMode(2);
				light_control_auto();
				break;
			case R.id.iv_light_control_timecurve :
				// i_Current_WorkMode = 3; // Need Delete Note
				// setWorkMode(i_Current_WorkMode);
				setWorkMode(3);
				light_control_timecurve_new();
				break;
			case R.id.iv_light_control_timecurve_new :
				// i_Current_WorkMode = 3; // Need Delete Note
				// setWorkMode(i_Current_WorkMode);
				setWorkMode(3);
				light_control_timecurve_new();
				break;
			case R.id.iv_light_control_timetask :
				// i_Current_WorkMode = 4; // Need Delete Note
				// setWorkMode(i_Current_WorkMode);
				setWorkMode(4);
				light_control_timetask();
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
		String data = "0,100,100,100,100,100";
		switch (i_Current_lushu) {
			case 1 :
				data = "0,100,0,0,0,0";
				break;
			case 2 :
				data = "0,100,100,0,0,0";
				break;
			case 3 :
				data = "0,100,100,100,0,0";
				break;
			case 4 :
				data = "0,100,100,100,100,0";
				break;
			case 5 :
				data = "0,100,100,100,100,100";
				break;
		}

		if (isOpen == false) {
			data = "0,0,0,0,0,0";
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
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleManualActivity.class);
		startActivity(intent);
	}

	private void light_control_auto() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleAutoActivity.class);
		startActivity(intent);
	}

	private void light_control_timetask() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleTimeTaskActivity.class);
		startActivity(intent);

	}

	private void light_control_timecurve() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleTimeCurveActivity.class);
		startActivity(intent);

	}

	private void light_control_timecurve_new() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleTimeCurvePointActivity.class);
		startActivity(intent);

	}

	private void light_control_setting() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("PLUGIP", mPlugIp);
		intent.setClass(DetailGrowLightBleActivity.this,
				DetailGrowLightBleSettingActivity.class);
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
		iv_light_control_timecurve_new = (ImageView) findViewById(R.id.iv_light_control_timecurve_new);
		iv_light_control_setting = (ImageView) findViewById(R.id.iv_light_control_setting);

		iv_light_control.setImageResource(m_isOpen
				? R.drawable.smp_growlight_on_big
				: R.drawable.smp_growlight_off_big);

		spinner_workmode = (Spinner) findViewById(R.id.spinner_workmode);

		iv_light_control.setOnClickListener(this);
		iv_light_control_manual.setOnClickListener(this);
		iv_light_control_auto.setOnClickListener(this);
		iv_light_control_timetask.setOnClickListener(this);
		iv_light_control_timecurve.setOnClickListener(this);
		iv_light_control_timecurve_new.setOnClickListener(this);
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
			// spinner_workmode.setSelection(i_Current_WorkMode);
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

		// spinner_workmode.setSelection(i_Current_WorkMode);
		switch (i_Current_WorkMode) {
			case 1 :
				iv_light_control_manual
						.setImageResource(R.drawable.smp_growlight_hand_enable);
				iv_light_control_auto
						.setImageResource(R.drawable.smp_growlight_sun_disable);
				iv_light_control_timecurve
						.setImageResource(R.drawable.smp_growlight_time_curves_disable);
				iv_light_control_timetask
						.setImageResource(R.drawable.smp_growlight_timetask_disable);
				break;
			case 2 :
				iv_light_control_manual
						.setImageResource(R.drawable.smp_growlight_hand_disable);
				iv_light_control_auto
						.setImageResource(R.drawable.smp_growlight_sun_enable);
				iv_light_control_timecurve
						.setImageResource(R.drawable.smp_growlight_time_curves_disable);
				iv_light_control_timetask
						.setImageResource(R.drawable.smp_growlight_timetask_disable);
				break;
			case 3 :
				iv_light_control_manual
						.setImageResource(R.drawable.smp_growlight_hand_disable);
				iv_light_control_auto
						.setImageResource(R.drawable.smp_growlight_sun_disable);
				iv_light_control_timecurve
						.setImageResource(R.drawable.smp_growlight_time_curves_enable);
				iv_light_control_timetask
						.setImageResource(R.drawable.smp_growlight_timetask_disable);
				break;
			case 4 :
				iv_light_control_manual
						.setImageResource(R.drawable.smp_growlight_hand_disable);
				iv_light_control_auto
						.setImageResource(R.drawable.smp_growlight_sun_disable);
				iv_light_control_timecurve
						.setImageResource(R.drawable.smp_growlight_time_curves_disable);
				iv_light_control_timetask
						.setImageResource(R.drawable.smp_growlight_timetask_enable);
				break;
			default :
				iv_light_control_manual
						.setImageResource(R.drawable.smp_growlight_hand_disable);
				iv_light_control_auto
						.setImageResource(R.drawable.smp_growlight_sun_disable);
				iv_light_control_timecurve
						.setImageResource(R.drawable.smp_growlight_time_curves_disable);
				iv_light_control_timetask
						.setImageResource(R.drawable.smp_growlight_timetask_disable);
				break;
		}

		if (i_light_01 == 0 && i_light_02 == 0 && i_light_03 == 0
				&& i_light_04 == 0 && i_light_05 == 0) {
			m_isOpen = false;
		} else {
			m_isOpen = true;
		}

		iv_light_control.setImageDrawable(getResources().getDrawable(
				m_isOpen == true
						? R.drawable.smp_growlight_on_big
						: R.drawable.smp_growlight_off_big));

		// // 使用定时器来 更新 时间
		// new Timer("GrowLightTime").schedule(new TimerTask() {
		// @Override
		// public void run() {
		// // show NE Time
		// getNextSecond();
		// mHandler.sendEmptyMessage(0);
		// // tv_light_curtime.setText(str_curtime);
		// }
		// }, 500, 1000);
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
