package com.thingzdo.ui.control;

import java.util.ArrayList;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugGrowLightTimerCurvePointHelper;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.GrowLightTimerCurvePointDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ScreenInfo;
import com.thingzdo.ui.wheelutils.WheelMain;
import com.thingzdo.util.ThingzdoCheckBox;

public class DetailGrowLightPointActivity extends TitledActivity
		implements
			OnClickListener,
			SeekBar.OnSeekBarChangeListener {
	private static int OPERATOR_ADD = 0;
	private static int OPERATOR_DEL = 1;
	private static int OPERATOR_MODIFY = 2;
	private static int OPERATOR_SELF_ADAPTER = 3;

	private int Activity_Operator_Mode = OPERATOR_ADD;
	private Context mContext;
	private String[] mSelDays = {"0", "0", "0", "0", "0", "0", "0"};
	private String[] mDays = null;

	private WheelMain wheelMain;

	private Spinner spinner_channel;
	private Spinner spinner_type;
	private ThingzdoCheckBox cb_enable;
	private TextView tv_peroid;

	private RelativeLayout rl_function_base_enable;
	private RelativeLayout rl_function_base_type;
	private RelativeLayout rl_function_base_peroid;
	private RelativeLayout rl_function_control;

	private TextView tv_light_01;
	private TextView tv_light_02;
	private TextView tv_light_03;
	private TextView tv_light_04;
	private TextView tv_light_05;
	private TextView tv_light_06;
	private TextView tv_light_07;
	private TextView tv_light_08;
	private TextView tv_light_09;
	private TextView tv_light_10;
	private TextView tv_light_11;
	private TextView tv_light_12;
	private TextView tv_light_13;
	private TextView tv_light_14;
	private TextView tv_light_15;
	private TextView tv_light_16;
	private TextView tv_light_17;
	private TextView tv_light_18;
	private TextView tv_light_19;
	private TextView tv_light_20;
	private TextView tv_light_21;
	private TextView tv_light_22;
	private TextView tv_light_23;
	private TextView tv_light_24;

	private TextView tv_light_right_01;
	private TextView tv_light_right_02;
	private TextView tv_light_right_03;
	private TextView tv_light_right_04;
	private TextView tv_light_right_05;
	private TextView tv_light_right_06;
	private TextView tv_light_right_07;
	private TextView tv_light_right_08;
	private TextView tv_light_right_09;
	private TextView tv_light_right_10;
	private TextView tv_light_right_11;
	private TextView tv_light_right_12;
	private TextView tv_light_right_13;
	private TextView tv_light_right_14;
	private TextView tv_light_right_15;
	private TextView tv_light_right_16;
	private TextView tv_light_right_17;
	private TextView tv_light_right_18;
	private TextView tv_light_right_19;
	private TextView tv_light_right_20;
	private TextView tv_light_right_21;
	private TextView tv_light_right_22;
	private TextView tv_light_right_23;
	private TextView tv_light_right_24;

	private SeekBar sb_light_01;
	private SeekBar sb_light_02;
	private SeekBar sb_light_03;
	private SeekBar sb_light_04;
	private SeekBar sb_light_05;
	private SeekBar sb_light_06;
	private SeekBar sb_light_07;
	private SeekBar sb_light_08;
	private SeekBar sb_light_09;
	private SeekBar sb_light_10;
	private SeekBar sb_light_11;
	private SeekBar sb_light_12;
	private SeekBar sb_light_13;
	private SeekBar sb_light_14;
	private SeekBar sb_light_15;
	private SeekBar sb_light_16;
	private SeekBar sb_light_17;
	private SeekBar sb_light_18;
	private SeekBar sb_light_19;
	private SeekBar sb_light_20;
	private SeekBar sb_light_21;
	private SeekBar sb_light_22;
	private SeekBar sb_light_23;
	private SeekBar sb_light_24;

	private int value_light_01_pos = 0;
	private int value_light_02_pos = 0;
	private int value_light_03_pos = 0;
	private int value_light_04_pos = 0;
	private int value_light_05_pos = 0;
	private int value_light_06_pos = 0;
	private int value_light_07_pos = 0;
	private int value_light_08_pos = 0;
	private int value_light_09_pos = 0;
	private int value_light_10_pos = 0;
	private int value_light_11_pos = 0;
	private int value_light_12_pos = 0;
	private int value_light_13_pos = 0;
	private int value_light_14_pos = 0;
	private int value_light_15_pos = 0;
	private int value_light_16_pos = 0;
	private int value_light_17_pos = 0;
	private int value_light_18_pos = 0;
	private int value_light_19_pos = 0;
	private int value_light_20_pos = 0;
	private int value_light_21_pos = 0;
	private int value_light_22_pos = 0;
	private int value_light_23_pos = 0;
	private int value_light_24_pos = 0;

	private String value_light_01_time = "00:00";
	private String value_light_02_time = "01:00";
	private String value_light_03_time = "02:00";
	private String value_light_04_time = "03:00";
	private String value_light_05_time = "04:00";
	private String value_light_06_time = "05:00";
	private String value_light_07_time = "06:00";
	private String value_light_08_time = "07:00";
	private String value_light_09_time = "08:00";
	private String value_light_10_time = "09:00";
	private String value_light_11_time = "10:00";
	private String value_light_12_time = "11:00";
	private String value_light_13_time = "12:00";
	private String value_light_14_time = "13:00";
	private String value_light_15_time = "14:00";
	private String value_light_16_time = "15:00";
	private String value_light_17_time = "16:00";
	private String value_light_18_time = "17:00";
	private String value_light_19_time = "18:00";
	private String value_light_20_time = "19:00";
	private String value_light_21_time = "20:00";
	private String value_light_22_time = "21:00";
	private String value_light_23_time = "22:00";
	private String value_light_24_time = "23:00";

	private List<String> irlist_channel = new ArrayList<String>();
	private List<String> irlist_type = new ArrayList<String>();

	private int i_Current_Channel = 0;
	private int i_Current_Type = 0;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugGrowLightTimerCurvePointHelper mTimerHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer = new ArrayList<GrowLightTimerCurvePointDefine>();

	private int i_Current_lushu = 5;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightPointActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_TIMECURVEPOINT_ACTION)) {
				String moduleID = intent.getStringExtra("MODULEID");
				if (moduleID.equals(mPlugId) == true) {
					finish();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_point, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		mContext = this;

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_TIMECURVEPOINT_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		mTimerHelper = new SmartPlugGrowLightTimerCurvePointHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");

		loadData();

		String operator_mode = intent.getStringExtra("OPERATE");
		if (operator_mode.equals("ADD") == true) {
			Activity_Operator_Mode = OPERATOR_ADD;
		} else if (operator_mode.equals("DEL") == true) {
			Activity_Operator_Mode = OPERATOR_DEL;
		} else if (operator_mode.equals("MODIFY") == true) {
			Activity_Operator_Mode = OPERATOR_MODIFY;
		} else if (operator_mode.equals("SELF_ADAPTER") == true) {
			Activity_Operator_Mode = OPERATOR_SELF_ADAPTER;
		}

		UDPClient.getInstance().setIPAddress(mPlugIp);

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
	}

	private void loadData() {
		i_Current_lushu = mSharedPreferences.getInt("SETROUTES" + mPlugId, 5);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		// init();
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
				// OK for Add new TimeCurvePoint
				if (Activity_Operator_Mode == OPERATOR_ADD) {
					addTimeCurvePoint();
				} else if (Activity_Operator_Mode == OPERATOR_DEL) {
					delTimeCurvePoint();
				} else if (Activity_Operator_Mode == OPERATOR_MODIFY) {
					addTimeCurvePoint();
				} else if (Activity_Operator_Mode == OPERATOR_SELF_ADAPTER) {
					addTimeCurvePoint();
				}

				break;
			case R.id.tv_peroid :
				set_peroid();
				break;
			default :
				break;
		}
	}

	private void addTimeCurvePoint() {
		String strEnable = "1";
		int count = 24;
		String points = "";
		points = value_light_01_time + "," + String.valueOf(value_light_01_pos)
				+ "," + value_light_02_time + ","
				+ String.valueOf(value_light_02_pos) + ","
				+ value_light_03_time + ","
				+ String.valueOf(value_light_03_pos) + ","
				+ value_light_04_time + ","
				+ String.valueOf(value_light_04_pos) + ","
				+ value_light_05_time + ","
				+ String.valueOf(value_light_05_pos) + ","
				+ value_light_06_time + ","
				+ String.valueOf(value_light_06_pos) + ","
				+ value_light_07_time + ","
				+ String.valueOf(value_light_07_pos) + ","
				+ value_light_08_time + ","
				+ String.valueOf(value_light_08_pos) + ","
				+ value_light_09_time + ","
				+ String.valueOf(value_light_09_pos) + ","
				+ value_light_10_time + ","
				+ String.valueOf(value_light_10_pos) + ","
				+ value_light_11_time + ","
				+ String.valueOf(value_light_11_pos) + ","
				+ value_light_12_time + ","
				+ String.valueOf(value_light_12_pos) + ","
				+ value_light_13_time + ","
				+ String.valueOf(value_light_13_pos) + ","
				+ value_light_14_time + ","
				+ String.valueOf(value_light_14_pos) + ","
				+ value_light_15_time + ","
				+ String.valueOf(value_light_15_pos) + ","
				+ value_light_16_time + ","
				+ String.valueOf(value_light_16_pos) + ","
				+ value_light_17_time + ","
				+ String.valueOf(value_light_17_pos) + ","
				+ value_light_18_time + ","
				+ String.valueOf(value_light_18_pos) + ","
				+ value_light_19_time + ","
				+ String.valueOf(value_light_19_pos) + ","
				+ value_light_20_time + ","
				+ String.valueOf(value_light_20_pos) + ","
				+ value_light_21_time + ","
				+ String.valueOf(value_light_21_pos) + ","
				+ value_light_22_time + ","
				+ String.valueOf(value_light_22_pos) + ","
				+ value_light_23_time + ","
				+ String.valueOf(value_light_23_pos) + ","
				+ value_light_24_time + ","
				+ String.valueOf(value_light_24_pos);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_TIMECURVE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(i_Current_Channel)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(tv_peroid.getTag())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(strEnable)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(i_Current_Type)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(count))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(points);

		sendMsg(true, sb.toString(), true);
	}

	private void delTimeCurvePoint() {
		String strEnable = "0";
		int count = 24;
		String points = "";
		points = value_light_01_time + "," + String.valueOf(value_light_01_pos)
				+ "," + value_light_02_time + ","
				+ String.valueOf(value_light_02_pos) + ","
				+ value_light_03_time + ","
				+ String.valueOf(value_light_03_pos) + ","
				+ value_light_04_time + ","
				+ String.valueOf(value_light_04_pos) + ","
				+ value_light_05_time + ","
				+ String.valueOf(value_light_05_pos) + ","
				+ value_light_06_time + ","
				+ String.valueOf(value_light_06_pos) + ","
				+ value_light_07_time + ","
				+ String.valueOf(value_light_07_pos) + ","
				+ value_light_08_time + ","
				+ String.valueOf(value_light_08_pos) + ","
				+ value_light_09_time + ","
				+ String.valueOf(value_light_09_pos) + ","
				+ value_light_10_time + ","
				+ String.valueOf(value_light_10_pos) + ","
				+ value_light_11_time + ","
				+ String.valueOf(value_light_11_pos) + ","
				+ value_light_12_time + ","
				+ String.valueOf(value_light_12_pos) + ","
				+ value_light_13_time + ","
				+ String.valueOf(value_light_13_pos) + ","
				+ value_light_14_time + ","
				+ String.valueOf(value_light_14_pos) + ","
				+ value_light_15_time + ","
				+ String.valueOf(value_light_15_pos) + ","
				+ value_light_16_time + ","
				+ String.valueOf(value_light_16_pos) + ","
				+ value_light_17_time + ","
				+ String.valueOf(value_light_17_pos) + ","
				+ value_light_18_time + ","
				+ String.valueOf(value_light_18_pos) + ","
				+ value_light_19_time + ","
				+ String.valueOf(value_light_19_pos) + ","
				+ value_light_20_time + ","
				+ String.valueOf(value_light_20_pos) + ","
				+ value_light_21_time + ","
				+ String.valueOf(value_light_21_pos) + ","
				+ value_light_22_time + ","
				+ String.valueOf(value_light_22_pos) + ","
				+ value_light_23_time + ","
				+ String.valueOf(value_light_23_pos) + ","
				+ value_light_24_time + ","
				+ String.valueOf(value_light_24_pos);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_TIMECURVE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(i_Current_Channel)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(tv_peroid.getTag())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(strEnable)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(i_Current_Type)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(count))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(points);

		sendMsg(true, sb.toString(), true);
	}

	// private void queryTimeCurvePoint() {
	// StringBuffer sb = new StringBuffer();
	// sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_TIMECURVE)
	// .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	// .append(PubStatus.getUserName())
	// .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
	// .append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
	// .append(i_Current_Channel);
	//
	// sendMsg(true, sb.toString(), true);
	// }

	private void set_peroid() {
		Intent intent = new Intent();
		intent.setClass(this, SetPeriodActivity.class);
		intent.putExtra("selected_days", mSelDays);
		startActivityForResult(intent, 0);

		// showDialog(tv_peroid, tv_peroid.getText().toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (null == data) {
			return;
		}
		if (0 == resultCode) {
			mSelDays = data.getStringArrayExtra("selected_days");
		}

		mHandler.sendEmptyMessage(0);

		// tv_peroid.setTag(getPeriodValue());
		// tv_peroid.setText(getPeriodText());
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					tv_peroid.setTag(getPeriodValue());
					tv_peroid.setText(getPeriodText());
					break;
				case 1 :
					break;
				default :
					break;
			}
		};
	};

	private String getPeriodValue() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mSelDays.length; i++) {
			sb.append(mSelDays[i]);
		}
		return sb.toString();
	}

	private String getPeriodText() {
		StringBuffer sbDay = new StringBuffer();
		for (int i = 0; i < mSelDays.length; i++) {
			if (mSelDays[i].equals("1")) {
				if (i < mSelDays.length - 1) {
					sbDay.append(mDays[i]).append(" ");
				} else {
					sbDay.append(mDays[i]);
				}
			}
		}

		return sbDay.toString();
	}

	private void showDialog(TextView view, String timer) {
		String[] time_val_t = timer.split(":");
		String[] time_val = {"00", "00", "00"};

		if (time_val_t.length > 0) {
			time_val[0] = time_val_t[0];
		}
		if (time_val_t.length > 1) {
			time_val[1] = time_val_t[1];
		}
		if (time_val_t.length > 2) {
			time_val[2] = time_val_t[2];
		}
		final TextView mView = view;

		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(
				DetailGrowLightPointActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(Integer.parseInt(time_val[0]),
				Integer.parseInt(time_val[1]), Integer.parseInt(time_val[2]));
		final MyAlertDialog dialog = new MyAlertDialog(mContext)
				.builder()
				.setTitle(mContext.getString(R.string.timer_task_selecttime))
				.setView(timepickerview)
				.setNegativeButton(
						mContext.getString(R.string.smartplug_cancel),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
		dialog.setPositiveButton(mContext.getString(R.string.smartplug_ok),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String[] time = wheelMain.getTime().split(":");
						StringBuffer sb = new StringBuffer();
						if (1 == time[0].length()) {
							sb.append("0").append(time[0]).append(":");
						} else {
							sb.append(time[0]).append(":");
						}
						if (1 == time[1].length()) {
							sb.append("0").append(time[1]).append(":");
						} else {
							sb.append(time[1]).append(":");
						}
						if (1 == time[2].length()) {
							sb.append("0").append(time[2]);
						} else {
							sb.append(time[2]);
						}

						mView.setText(sb);
					}
				});
		dialog.show();
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(SmartPlugApplication.getInstance().getResources()
				.getString(R.string.smartplug_growlight_timecurve));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_ok,
				R.drawable.title_btn_selector, this);

		spinner_channel = (Spinner) findViewById(R.id.spinner_channel);
		spinner_type = (Spinner) findViewById(R.id.spinner_type);
		cb_enable = (ThingzdoCheckBox) findViewById(R.id.cb_enable);
		tv_peroid = (TextView) findViewById(R.id.tv_peroid);
		rl_function_control = (RelativeLayout) findViewById(R.id.rl_function_control);

		rl_function_base_enable = (RelativeLayout) findViewById(R.id.rl_function_base_enable);
		rl_function_base_type = (RelativeLayout) findViewById(R.id.rl_function_base_type);
		rl_function_base_peroid = (RelativeLayout) findViewById(R.id.rl_function_base_peroid);

		if (Activity_Operator_Mode == OPERATOR_DEL) {
			rl_function_base_enable.setVisibility(View.GONE);
			rl_function_base_type.setVisibility(View.GONE);
			rl_function_base_peroid.setVisibility(View.GONE);
			rl_function_control.setVisibility(View.GONE);

		} else {
			rl_function_control.setVisibility(View.VISIBLE);
		}

		tv_peroid.setOnClickListener(this);

		// init spinner Channel data
		irlist_channel.clear();
		Resources res = SmartPlugApplication.getInstance().getResources();

		switch (i_Current_lushu) {
			case 1 :
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_1));
				break;
			case 2 :
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_1));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_2));
				break;
			case 3 :
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_1));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_2));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_3));
				break;
			case 4 :
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_1));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_2));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_3));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_4));
				break;
			case 5 :
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_1));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_2));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_3));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_4));
				irlist_channel
						.add(res.getString(R.string.smartplug_growlight_lushu_control_5));
				break;
		}

		ArrayAdapter<String> adapter_channel = new ArrayAdapter<String>(this,
				R.layout.activity_detail_aircon2item, R.id.tv_item,
				irlist_channel);
		spinner_channel.setAdapter(adapter_channel);
		spinner_channel.setPrompt("测试");
		spinner_channel.setOnItemSelectedListener(new spinnerChannelListener());

		if (irlist_channel.size() > 0) {
			spinner_channel.setSelection(i_Current_Channel);
		}

		// init spinner Type data
		irlist_type.clear();
		irlist_type.add(res.getString(R.string.smartplug_growlight_type_1));
		irlist_type.add(res.getString(R.string.smartplug_growlight_type_2));

		ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this,
				R.layout.activity_detail_aircon2item, R.id.tv_item, irlist_type);
		spinner_type.setAdapter(adapter_type);
		spinner_type.setPrompt("测试");
		spinner_type.setOnItemSelectedListener(new spinnerTypeListener());

		if (irlist_type.size() > 0) {
			spinner_type.setSelection(i_Current_Type);
		}

		tv_light_01 = (TextView) findViewById(R.id.tv_light_01);
		tv_light_02 = (TextView) findViewById(R.id.tv_light_02);
		tv_light_03 = (TextView) findViewById(R.id.tv_light_03);
		tv_light_04 = (TextView) findViewById(R.id.tv_light_04);
		tv_light_05 = (TextView) findViewById(R.id.tv_light_05);
		tv_light_06 = (TextView) findViewById(R.id.tv_light_06);
		tv_light_07 = (TextView) findViewById(R.id.tv_light_07);
		tv_light_08 = (TextView) findViewById(R.id.tv_light_08);
		tv_light_09 = (TextView) findViewById(R.id.tv_light_09);
		tv_light_10 = (TextView) findViewById(R.id.tv_light_10);
		tv_light_11 = (TextView) findViewById(R.id.tv_light_11);
		tv_light_12 = (TextView) findViewById(R.id.tv_light_12);
		tv_light_13 = (TextView) findViewById(R.id.tv_light_13);
		tv_light_14 = (TextView) findViewById(R.id.tv_light_14);
		tv_light_15 = (TextView) findViewById(R.id.tv_light_15);
		tv_light_16 = (TextView) findViewById(R.id.tv_light_16);
		tv_light_17 = (TextView) findViewById(R.id.tv_light_17);
		tv_light_18 = (TextView) findViewById(R.id.tv_light_18);
		tv_light_19 = (TextView) findViewById(R.id.tv_light_19);
		tv_light_20 = (TextView) findViewById(R.id.tv_light_20);
		tv_light_21 = (TextView) findViewById(R.id.tv_light_21);
		tv_light_22 = (TextView) findViewById(R.id.tv_light_22);
		tv_light_23 = (TextView) findViewById(R.id.tv_light_23);
		tv_light_24 = (TextView) findViewById(R.id.tv_light_24);

		tv_light_right_01 = (TextView) findViewById(R.id.tv_light_right_01);
		tv_light_right_02 = (TextView) findViewById(R.id.tv_light_right_02);
		tv_light_right_03 = (TextView) findViewById(R.id.tv_light_right_03);
		tv_light_right_04 = (TextView) findViewById(R.id.tv_light_right_04);
		tv_light_right_05 = (TextView) findViewById(R.id.tv_light_right_05);
		tv_light_right_06 = (TextView) findViewById(R.id.tv_light_right_06);
		tv_light_right_07 = (TextView) findViewById(R.id.tv_light_right_07);
		tv_light_right_08 = (TextView) findViewById(R.id.tv_light_right_08);
		tv_light_right_09 = (TextView) findViewById(R.id.tv_light_right_09);
		tv_light_right_10 = (TextView) findViewById(R.id.tv_light_right_10);
		tv_light_right_11 = (TextView) findViewById(R.id.tv_light_right_11);
		tv_light_right_12 = (TextView) findViewById(R.id.tv_light_right_12);
		tv_light_right_13 = (TextView) findViewById(R.id.tv_light_right_13);
		tv_light_right_14 = (TextView) findViewById(R.id.tv_light_right_14);
		tv_light_right_15 = (TextView) findViewById(R.id.tv_light_right_15);
		tv_light_right_16 = (TextView) findViewById(R.id.tv_light_right_16);
		tv_light_right_17 = (TextView) findViewById(R.id.tv_light_right_17);
		tv_light_right_18 = (TextView) findViewById(R.id.tv_light_right_18);
		tv_light_right_19 = (TextView) findViewById(R.id.tv_light_right_19);
		tv_light_right_20 = (TextView) findViewById(R.id.tv_light_right_20);
		tv_light_right_21 = (TextView) findViewById(R.id.tv_light_right_21);
		tv_light_right_22 = (TextView) findViewById(R.id.tv_light_right_22);
		tv_light_right_23 = (TextView) findViewById(R.id.tv_light_right_23);
		tv_light_right_24 = (TextView) findViewById(R.id.tv_light_right_24);

		sb_light_01 = (SeekBar) findViewById(R.id.sb_light_01);
		sb_light_02 = (SeekBar) findViewById(R.id.sb_light_02);
		sb_light_03 = (SeekBar) findViewById(R.id.sb_light_03);
		sb_light_04 = (SeekBar) findViewById(R.id.sb_light_04);
		sb_light_05 = (SeekBar) findViewById(R.id.sb_light_05);
		sb_light_06 = (SeekBar) findViewById(R.id.sb_light_06);
		sb_light_07 = (SeekBar) findViewById(R.id.sb_light_07);
		sb_light_08 = (SeekBar) findViewById(R.id.sb_light_08);
		sb_light_09 = (SeekBar) findViewById(R.id.sb_light_09);
		sb_light_10 = (SeekBar) findViewById(R.id.sb_light_10);
		sb_light_11 = (SeekBar) findViewById(R.id.sb_light_11);
		sb_light_12 = (SeekBar) findViewById(R.id.sb_light_12);
		sb_light_13 = (SeekBar) findViewById(R.id.sb_light_13);
		sb_light_14 = (SeekBar) findViewById(R.id.sb_light_14);
		sb_light_15 = (SeekBar) findViewById(R.id.sb_light_15);
		sb_light_16 = (SeekBar) findViewById(R.id.sb_light_16);
		sb_light_17 = (SeekBar) findViewById(R.id.sb_light_17);
		sb_light_18 = (SeekBar) findViewById(R.id.sb_light_18);
		sb_light_19 = (SeekBar) findViewById(R.id.sb_light_19);
		sb_light_20 = (SeekBar) findViewById(R.id.sb_light_20);
		sb_light_21 = (SeekBar) findViewById(R.id.sb_light_21);
		sb_light_22 = (SeekBar) findViewById(R.id.sb_light_22);
		sb_light_23 = (SeekBar) findViewById(R.id.sb_light_23);
		sb_light_24 = (SeekBar) findViewById(R.id.sb_light_24);

		tv_light_01.setOnClickListener(this);
		tv_light_02.setOnClickListener(this);
		tv_light_03.setOnClickListener(this);
		tv_light_04.setOnClickListener(this);
		tv_light_05.setOnClickListener(this);
		tv_light_06.setOnClickListener(this);
		tv_light_07.setOnClickListener(this);
		tv_light_08.setOnClickListener(this);
		tv_light_09.setOnClickListener(this);
		tv_light_10.setOnClickListener(this);
		tv_light_11.setOnClickListener(this);
		tv_light_12.setOnClickListener(this);
		tv_light_13.setOnClickListener(this);
		tv_light_14.setOnClickListener(this);
		tv_light_15.setOnClickListener(this);
		tv_light_16.setOnClickListener(this);
		tv_light_17.setOnClickListener(this);
		tv_light_18.setOnClickListener(this);
		tv_light_19.setOnClickListener(this);
		tv_light_20.setOnClickListener(this);
		tv_light_21.setOnClickListener(this);
		tv_light_22.setOnClickListener(this);
		tv_light_23.setOnClickListener(this);
		tv_light_24.setOnClickListener(this);

		sb_light_01.setOnSeekBarChangeListener(this);
		sb_light_02.setOnSeekBarChangeListener(this);
		sb_light_03.setOnSeekBarChangeListener(this);
		sb_light_04.setOnSeekBarChangeListener(this);
		sb_light_05.setOnSeekBarChangeListener(this);
		sb_light_06.setOnSeekBarChangeListener(this);
		sb_light_07.setOnSeekBarChangeListener(this);
		sb_light_08.setOnSeekBarChangeListener(this);
		sb_light_09.setOnSeekBarChangeListener(this);
		sb_light_10.setOnSeekBarChangeListener(this);
		sb_light_11.setOnSeekBarChangeListener(this);
		sb_light_12.setOnSeekBarChangeListener(this);
		sb_light_13.setOnSeekBarChangeListener(this);
		sb_light_14.setOnSeekBarChangeListener(this);
		sb_light_15.setOnSeekBarChangeListener(this);
		sb_light_16.setOnSeekBarChangeListener(this);
		sb_light_17.setOnSeekBarChangeListener(this);
		sb_light_18.setOnSeekBarChangeListener(this);
		sb_light_19.setOnSeekBarChangeListener(this);
		sb_light_20.setOnSeekBarChangeListener(this);
		sb_light_21.setOnSeekBarChangeListener(this);
		sb_light_22.setOnSeekBarChangeListener(this);
		sb_light_23.setOnSeekBarChangeListener(this);
		sb_light_24.setOnSeekBarChangeListener(this);

		if (Activity_Operator_Mode == OPERATOR_ADD) {
			initParameter();
		} else if (Activity_Operator_Mode == OPERATOR_DEL) {
			restoreParameter();
		} else if (Activity_Operator_Mode == OPERATOR_MODIFY) {
			restoreParameter();
		} else if (Activity_Operator_Mode == OPERATOR_SELF_ADAPTER) {
			// if i_Current_Channel exist, must be resore. Or init
			ArrayList<GrowLightTimerCurvePointDefine> items = mTimerHelper
					.getAllTimer(mPlugId, i_Current_Channel);
			if (items.size() <= 0) {
				initParameter();
			} else {
				restoreParameter();
			}
		}
	}
	private void set_seekbar(SeekBar sb, TextView tv_light, int value) {
		sb.setProgress(value);
		tv_light.setText(String.valueOf(value));
	}

	private void initParameter() {
		cb_enable.setChecked(true);
		i_Current_Type = 0;
		if (irlist_channel.size() > 0) {
			spinner_channel.setSelection(i_Current_Channel);
		}
		if (irlist_type.size() > 0) {
			spinner_type.setSelection(i_Current_Type);
		}

		for (int i = 0; i < 7; i++) {
			mSelDays[i] = "1";
		}
		initPeriod(mSelDays);

		tv_light_01.setText(value_light_01_time);
		tv_light_02.setText(value_light_02_time);
		tv_light_03.setText(value_light_03_time);
		tv_light_04.setText(value_light_04_time);
		tv_light_05.setText(value_light_05_time);
		tv_light_06.setText(value_light_06_time);
		tv_light_07.setText(value_light_07_time);
		tv_light_08.setText(value_light_08_time);
		tv_light_09.setText(value_light_09_time);
		tv_light_10.setText(value_light_10_time);
		tv_light_11.setText(value_light_11_time);
		tv_light_12.setText(value_light_12_time);
		tv_light_13.setText(value_light_13_time);
		tv_light_14.setText(value_light_14_time);
		tv_light_15.setText(value_light_15_time);
		tv_light_16.setText(value_light_16_time);
		tv_light_17.setText(value_light_17_time);
		tv_light_18.setText(value_light_18_time);
		tv_light_19.setText(value_light_19_time);
		tv_light_20.setText(value_light_20_time);
		tv_light_21.setText(value_light_21_time);
		tv_light_22.setText(value_light_22_time);
		tv_light_23.setText(value_light_23_time);
		tv_light_24.setText(value_light_24_time);

		value_light_01_pos = 0;
		value_light_02_pos = 0;
		value_light_03_pos = 0;
		value_light_04_pos = 0;
		value_light_05_pos = 0;
		value_light_06_pos = 0;
		value_light_07_pos = 0;
		value_light_08_pos = 0;
		value_light_09_pos = 0;
		value_light_10_pos = 0;
		value_light_11_pos = 0;
		value_light_12_pos = 0;
		value_light_13_pos = 0;
		value_light_14_pos = 0;
		value_light_15_pos = 0;
		value_light_16_pos = 0;
		value_light_17_pos = 0;
		value_light_18_pos = 0;
		value_light_19_pos = 0;
		value_light_20_pos = 0;
		value_light_21_pos = 0;
		value_light_22_pos = 0;
		value_light_23_pos = 0;
		value_light_24_pos = 0;

		set_seekbar(sb_light_01, tv_light_right_01, value_light_01_pos);
		set_seekbar(sb_light_02, tv_light_right_02, value_light_02_pos);
		set_seekbar(sb_light_03, tv_light_right_03, value_light_03_pos);
		set_seekbar(sb_light_04, tv_light_right_04, value_light_04_pos);
		set_seekbar(sb_light_05, tv_light_right_05, value_light_05_pos);
		set_seekbar(sb_light_06, tv_light_right_06, value_light_06_pos);
		set_seekbar(sb_light_07, tv_light_right_07, value_light_07_pos);
		set_seekbar(sb_light_08, tv_light_right_08, value_light_08_pos);
		set_seekbar(sb_light_09, tv_light_right_09, value_light_09_pos);
		set_seekbar(sb_light_10, tv_light_right_10, value_light_10_pos);
		set_seekbar(sb_light_11, tv_light_right_11, value_light_11_pos);
		set_seekbar(sb_light_12, tv_light_right_12, value_light_12_pos);
		set_seekbar(sb_light_13, tv_light_right_13, value_light_13_pos);
		set_seekbar(sb_light_14, tv_light_right_14, value_light_14_pos);
		set_seekbar(sb_light_15, tv_light_right_15, value_light_15_pos);
		set_seekbar(sb_light_16, tv_light_right_16, value_light_16_pos);
		set_seekbar(sb_light_17, tv_light_right_17, value_light_17_pos);
		set_seekbar(sb_light_18, tv_light_right_18, value_light_18_pos);
		set_seekbar(sb_light_19, tv_light_right_19, value_light_19_pos);
		set_seekbar(sb_light_20, tv_light_right_20, value_light_20_pos);
		set_seekbar(sb_light_21, tv_light_right_21, value_light_21_pos);
		set_seekbar(sb_light_22, tv_light_right_22, value_light_22_pos);
		set_seekbar(sb_light_23, tv_light_right_23, value_light_23_pos);
		set_seekbar(sb_light_24, tv_light_right_24, value_light_24_pos);

	}

	private void restoreParameter() {
		mTimer.clear();
		mTimer = mTimerHelper.getAllTimer(mPlugId, i_Current_Channel);
		if (mTimer.size() > 0) {
			i_Current_Type = mTimer.get(0).mType;
			String period = mTimer.get(0).mPeriod;
			for (int i = 0; i < 7; i++) {
				mSelDays[i] = period.substring(i, i + 1);
			}

			value_light_01_time = mTimer.get(0).mPowerOnTime;
			value_light_02_time = mTimer.get(1).mPowerOnTime;
			value_light_03_time = mTimer.get(2).mPowerOnTime;
			value_light_04_time = mTimer.get(3).mPowerOnTime;
			value_light_05_time = mTimer.get(4).mPowerOnTime;
			value_light_06_time = mTimer.get(5).mPowerOnTime;
			value_light_07_time = mTimer.get(6).mPowerOnTime;
			value_light_08_time = mTimer.get(7).mPowerOnTime;
			value_light_09_time = mTimer.get(8).mPowerOnTime;
			value_light_10_time = mTimer.get(9).mPowerOnTime;
			value_light_11_time = mTimer.get(10).mPowerOnTime;
			value_light_12_time = mTimer.get(11).mPowerOnTime;
			value_light_13_time = mTimer.get(12).mPowerOnTime;
			value_light_14_time = mTimer.get(13).mPowerOnTime;
			value_light_15_time = mTimer.get(14).mPowerOnTime;
			value_light_16_time = mTimer.get(15).mPowerOnTime;
			value_light_17_time = mTimer.get(16).mPowerOnTime;
			value_light_18_time = mTimer.get(17).mPowerOnTime;
			value_light_19_time = mTimer.get(18).mPowerOnTime;
			value_light_20_time = mTimer.get(19).mPowerOnTime;
			value_light_21_time = mTimer.get(20).mPowerOnTime;
			value_light_22_time = mTimer.get(21).mPowerOnTime;
			value_light_23_time = mTimer.get(22).mPowerOnTime;
			value_light_24_time = mTimer.get(23).mPowerOnTime;

			value_light_01_pos = mTimer.get(0).light;
			value_light_02_pos = mTimer.get(1).light;
			value_light_03_pos = mTimer.get(2).light;
			value_light_04_pos = mTimer.get(3).light;
			value_light_05_pos = mTimer.get(4).light;
			value_light_06_pos = mTimer.get(5).light;
			value_light_07_pos = mTimer.get(6).light;
			value_light_08_pos = mTimer.get(7).light;
			value_light_09_pos = mTimer.get(8).light;
			value_light_10_pos = mTimer.get(9).light;
			value_light_11_pos = mTimer.get(10).light;
			value_light_12_pos = mTimer.get(11).light;
			value_light_13_pos = mTimer.get(12).light;
			value_light_14_pos = mTimer.get(13).light;
			value_light_15_pos = mTimer.get(14).light;
			value_light_16_pos = mTimer.get(15).light;
			value_light_17_pos = mTimer.get(16).light;
			value_light_18_pos = mTimer.get(17).light;
			value_light_19_pos = mTimer.get(18).light;
			value_light_20_pos = mTimer.get(19).light;
			value_light_21_pos = mTimer.get(20).light;
			value_light_22_pos = mTimer.get(21).light;
			value_light_23_pos = mTimer.get(22).light;
			value_light_24_pos = mTimer.get(23).light;
		}

		cb_enable.setChecked(true);

		// if (irlist_channel.size() > 0) {
		// spinner_channel.setSelection(i_Current_Channel);
		// }

		if (irlist_type.size() > 0) {
			spinner_type.setSelection(i_Current_Type);
		}

		// for (int i = 0; i < 7; i++) {
		// mSelDays[i] = "1";
		// }
		initPeriod(mSelDays);

		tv_light_01.setText(value_light_01_time);
		tv_light_02.setText(value_light_02_time);
		tv_light_03.setText(value_light_03_time);
		tv_light_04.setText(value_light_04_time);
		tv_light_05.setText(value_light_05_time);
		tv_light_06.setText(value_light_06_time);
		tv_light_07.setText(value_light_07_time);
		tv_light_08.setText(value_light_08_time);
		tv_light_09.setText(value_light_09_time);
		tv_light_10.setText(value_light_10_time);
		tv_light_11.setText(value_light_11_time);
		tv_light_12.setText(value_light_12_time);
		tv_light_13.setText(value_light_13_time);
		tv_light_14.setText(value_light_14_time);
		tv_light_15.setText(value_light_15_time);
		tv_light_16.setText(value_light_16_time);
		tv_light_17.setText(value_light_17_time);
		tv_light_18.setText(value_light_18_time);
		tv_light_19.setText(value_light_19_time);
		tv_light_20.setText(value_light_20_time);
		tv_light_21.setText(value_light_21_time);
		tv_light_22.setText(value_light_22_time);
		tv_light_23.setText(value_light_23_time);
		tv_light_24.setText(value_light_24_time);

		set_seekbar(sb_light_01, tv_light_right_01, value_light_01_pos);
		set_seekbar(sb_light_02, tv_light_right_02, value_light_02_pos);
		set_seekbar(sb_light_03, tv_light_right_03, value_light_03_pos);
		set_seekbar(sb_light_04, tv_light_right_04, value_light_04_pos);
		set_seekbar(sb_light_05, tv_light_right_05, value_light_05_pos);
		set_seekbar(sb_light_06, tv_light_right_06, value_light_06_pos);
		set_seekbar(sb_light_07, tv_light_right_07, value_light_07_pos);
		set_seekbar(sb_light_08, tv_light_right_08, value_light_08_pos);
		set_seekbar(sb_light_09, tv_light_right_09, value_light_09_pos);
		set_seekbar(sb_light_10, tv_light_right_10, value_light_10_pos);
		set_seekbar(sb_light_11, tv_light_right_11, value_light_11_pos);
		set_seekbar(sb_light_12, tv_light_right_12, value_light_12_pos);
		set_seekbar(sb_light_13, tv_light_right_13, value_light_13_pos);
		set_seekbar(sb_light_14, tv_light_right_14, value_light_14_pos);
		set_seekbar(sb_light_15, tv_light_right_15, value_light_15_pos);
		set_seekbar(sb_light_16, tv_light_right_16, value_light_16_pos);
		set_seekbar(sb_light_17, tv_light_right_17, value_light_17_pos);
		set_seekbar(sb_light_18, tv_light_right_18, value_light_18_pos);
		set_seekbar(sb_light_19, tv_light_right_19, value_light_19_pos);
		set_seekbar(sb_light_20, tv_light_right_20, value_light_20_pos);
		set_seekbar(sb_light_21, tv_light_right_21, value_light_21_pos);
		set_seekbar(sb_light_22, tv_light_right_22, value_light_22_pos);
		set_seekbar(sb_light_23, tv_light_right_23, value_light_23_pos);
		set_seekbar(sb_light_24, tv_light_right_24, value_light_24_pos);
	}
	public void initPeriod(String[] selDays) {
		mDays = getResources().getStringArray(R.array.current_week);
		tv_peroid.setTag(getPeriodValue());
		tv_peroid.setText(getPeriodText());
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
			case R.id.sb_light_06 :
				tv_light_right_06.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_07 :
				tv_light_right_07.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_08 :
				tv_light_right_08.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_09 :
				tv_light_right_09.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_10 :
				tv_light_right_10.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_11 :
				tv_light_right_11.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_12 :
				tv_light_right_12.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_13 :
				tv_light_right_13.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_14 :
				tv_light_right_14.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_15 :
				tv_light_right_15.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_16 :
				tv_light_right_16.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_17 :
				tv_light_right_17.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_18 :
				tv_light_right_18.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_19 :
				tv_light_right_19.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_20 :
				tv_light_right_20.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_21 :
				tv_light_right_21.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_22 :
				tv_light_right_22.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_23 :
				tv_light_right_23.setText(String.valueOf(bar.getProgress()));
				break;
			case R.id.sb_light_24 :
				tv_light_right_24.setText(String.valueOf(bar.getProgress()));
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
				break;
			case R.id.sb_light_02 :
				value_light_02_pos = bar.getProgress();
				tv_light_right_02.setText(String.valueOf(value_light_02_pos));
				break;
			case R.id.sb_light_03 :
				value_light_03_pos = bar.getProgress();
				tv_light_right_03.setText(String.valueOf(value_light_03_pos));
				break;
			case R.id.sb_light_04 :
				value_light_04_pos = bar.getProgress();
				tv_light_right_04.setText(String.valueOf(value_light_04_pos));
				break;
			case R.id.sb_light_05 :
				value_light_05_pos = bar.getProgress();
				tv_light_right_05.setText(String.valueOf(value_light_05_pos));
				break;
			case R.id.sb_light_06 :
				value_light_06_pos = bar.getProgress();
				tv_light_right_06.setText(String.valueOf(value_light_06_pos));
				break;
			case R.id.sb_light_07 :
				value_light_07_pos = bar.getProgress();
				tv_light_right_07.setText(String.valueOf(value_light_07_pos));
				break;
			case R.id.sb_light_08 :
				value_light_08_pos = bar.getProgress();
				tv_light_right_08.setText(String.valueOf(value_light_08_pos));
				break;
			case R.id.sb_light_09 :
				value_light_09_pos = bar.getProgress();
				tv_light_right_09.setText(String.valueOf(value_light_09_pos));
				break;
			case R.id.sb_light_10 :
				value_light_10_pos = bar.getProgress();
				tv_light_right_10.setText(String.valueOf(value_light_10_pos));
				break;
			case R.id.sb_light_11 :
				value_light_11_pos = bar.getProgress();
				tv_light_right_11.setText(String.valueOf(value_light_11_pos));
				break;
			case R.id.sb_light_12 :
				value_light_12_pos = bar.getProgress();
				tv_light_right_12.setText(String.valueOf(value_light_12_pos));
				break;
			case R.id.sb_light_13 :
				value_light_13_pos = bar.getProgress();
				tv_light_right_13.setText(String.valueOf(value_light_13_pos));
				break;
			case R.id.sb_light_14 :
				value_light_14_pos = bar.getProgress();
				tv_light_right_14.setText(String.valueOf(value_light_14_pos));
				break;
			case R.id.sb_light_15 :
				value_light_15_pos = bar.getProgress();
				tv_light_right_15.setText(String.valueOf(value_light_15_pos));
				break;
			case R.id.sb_light_16 :
				value_light_16_pos = bar.getProgress();
				tv_light_right_16.setText(String.valueOf(value_light_16_pos));
				break;
			case R.id.sb_light_17 :
				value_light_17_pos = bar.getProgress();
				tv_light_right_17.setText(String.valueOf(value_light_17_pos));
				break;
			case R.id.sb_light_18 :
				value_light_18_pos = bar.getProgress();
				tv_light_right_18.setText(String.valueOf(value_light_18_pos));
				break;
			case R.id.sb_light_19 :
				value_light_19_pos = bar.getProgress();
				tv_light_right_19.setText(String.valueOf(value_light_19_pos));
				break;
			case R.id.sb_light_20 :
				value_light_20_pos = bar.getProgress();
				tv_light_right_20.setText(String.valueOf(value_light_20_pos));
				break;
			case R.id.sb_light_21 :
				value_light_21_pos = bar.getProgress();
				tv_light_right_21.setText(String.valueOf(value_light_21_pos));
				break;
			case R.id.sb_light_22 :
				value_light_22_pos = bar.getProgress();
				tv_light_right_22.setText(String.valueOf(value_light_22_pos));
				break;
			case R.id.sb_light_23 :
				value_light_23_pos = bar.getProgress();
				tv_light_right_23.setText(String.valueOf(value_light_23_pos));
				break;
			case R.id.sb_light_24 :
				value_light_24_pos = bar.getProgress();
				tv_light_right_24.setText(String.valueOf(value_light_24_pos));
				break;
			default :
				break;
		}
	}

	class spinnerChannelListener
			implements
				android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			i_Current_Channel = position;
			String str_Channel = parent.getItemAtPosition(position).toString();

			updateALL();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	class spinnerTypeListener
			implements
				android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			i_Current_Type = position;
			String str_type = parent.getItemAtPosition(position).toString();

			// updateALL();
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private void updateALL() {
		if (Activity_Operator_Mode == OPERATOR_ADD) {
			initParameter();
		} else if (Activity_Operator_Mode == OPERATOR_DEL) {
			// do nothing...
		} else if (Activity_Operator_Mode == OPERATOR_MODIFY) {
			restoreParameter();
		} else if (Activity_Operator_Mode == OPERATOR_SELF_ADAPTER) {
			// if i_Current_Channel exist, must be resore. Or init
			ArrayList<GrowLightTimerCurvePointDefine> items = mTimerHelper
					.getAllTimer(mPlugId, i_Current_Channel);
			if (items.size() <= 0) {
				initParameter();
			} else {
				restoreParameter();
			}
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
	}
}
