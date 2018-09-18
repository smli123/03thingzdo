package com.thingzdo.ui.control;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
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

	private int Activity_Operator_Mode = OPERATOR_ADD;
	private Context mContext;
	private String[] mSelDays = {"0", "0", "0", "0", "0", "0", "0"};
	private String[] mDays = null;

	private WheelMain wheelMain;

	private Spinner spinner_channel;
	private Spinner spinner_type;
	private ThingzdoCheckBox cb_enable;
	private TextView tv_peroid;

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

	private List<String> irlist_channel = new ArrayList<String>();
	private List<String> irlist_type = new ArrayList<String>();

	private int i_Current_Channel = 0;
	private int i_Current_Type = 0;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

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
				// do something... ReQuery;
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

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_TIMECURVEPOINT_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");

		String operator_mode = intent.getStringExtra("OPERATE");
		if (operator_mode.equals("ADD") == true) {
			Activity_Operator_Mode = OPERATOR_ADD;
		} else if (operator_mode.equals("DEL") == true) {
			Activity_Operator_Mode = OPERATOR_DEL;
		} else if (operator_mode.equals("MODIFY") == true) {
			Activity_Operator_Mode = OPERATOR_MODIFY;
		}

		UDPClient.getInstance().setIPAddress(mPlugIp);

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
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
					intent.setClass(DetailGrowLightPointActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightPointActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.tv_peroid :
				set_peroid();
				break;
			default :
				break;
		}
	}

	private void set_peroid() {
		Intent intent = new Intent();
		intent.setClass(this, SetPeriodActivity.class);
		intent.putExtra("selected_days", mSelDays);
		startActivityForResult(intent, 0);

		// showDialog(tv_peroid, tv_peroid.getText().toString());
	}

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
		setTitleRightButton(R.string.smartplug_title_plug_detail,
				R.drawable.title_btn_selector, this);

		spinner_channel = (Spinner) findViewById(R.id.spinner_channel);
		spinner_type = (Spinner) findViewById(R.id.spinner_type);
		cb_enable = (ThingzdoCheckBox) findViewById(R.id.cb_enable);
		tv_peroid = (TextView) findViewById(R.id.tv_peroid);

		tv_peroid.setOnClickListener(this);

		// init spinner Channel data
		irlist_channel.clear();
		Resources res = SmartPlugApplication.getInstance().getResources();
		irlist_channel.add(res
				.getString(R.string.smartplug_growlight_lushu_control_1));
		irlist_channel.add(res
				.getString(R.string.smartplug_growlight_lushu_control_2));
		irlist_channel.add(res
				.getString(R.string.smartplug_growlight_lushu_control_3));
		irlist_channel.add(res
				.getString(R.string.smartplug_growlight_lushu_control_4));
		irlist_channel.add(res
				.getString(R.string.smartplug_growlight_lushu_control_5));

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
				R.layout.activity_detail_aircon2item, R.id.tv_item,
				irlist_channel);
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
		}
	}

	private void initParameter() {
		cb_enable.setChecked(true);
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

	}

	public void initPeriod(String[] selDays) {
		mDays = getResources().getStringArray(R.array.current_week);
		tv_peroid.setTag(getPeriodValue());
		tv_peroid.setText(getPeriodText());
	}

	private void restoreParameter() {
		cb_enable.setChecked(true);
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
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

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
