package com.thingzdo.ui.control;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.MyAlertDialog;
import com.thingzdo.ui.wheelutils.ScreenInfo;
import com.thingzdo.ui.wheelutils.WheelMain;

public class DetailGrowLightAutoActivity extends TitledActivity
		implements
			OnClickListener {

	private Context mContext;
	private WheelMain wheelMain;

	private TextView tv_light_sun_cur_value;
	private TextView tv_light_sunup_time;
	private TextView tv_light_sundown_time;
	private RelativeLayout layout_period_header;
	private TextView tv_select_days;
	private Button btn_submit;

	private String str_sunup = "06:00:00";
	private String str_sundown = "18:00:00";

	private String[] mDays = null;
	private String[] mSelDays = {"0", "0", "0", "0", "0", "0", "0"};
	private String mPeroid = "1111111";

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private String mErrorMsg = "";

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private Timer valueTimer = null;
	private TimerTask valueTimerTask = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightAutoActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_SUNTIME_ACTION)) {
				if (mProgress != null) {
					mProgress.dismiss();
				}

				str_sunup = intent.getStringExtra("SUNUP");
				str_sundown = intent.getStringExtra("SUNDOWN");
				mPeroid = intent.getStringExtra("SUNPEROID");

				saveData();
				updateUI();

				mHandler.sendEmptyMessage(0);
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (0 == msg.what) { // Succeed for modify Sun time
				PubFunc.thinzdoToast(mContext, SmartPlugApplication
						.getInstance().getString(R.string.app_modify_succeed));
				finish();
			} else if (1 == msg.what) { // Failed for modify Sun time
				PubFunc.thinzdoToast(mContext, SmartPlugApplication
						.getInstance().getString(R.string.app_modify_failed));
			} else if (2 == msg.what) { // Show Bright Value
				int value = msg.arg1;
				tv_light_sun_cur_value.setText(String.valueOf(value));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_auto, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mContext = this;

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_SET_SUNTIME_ACTION);
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

		initNewTimer();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}
	}
	private void saveData() {
		editor = mSharedPreferences.edit();
		editor.putString("SUNUPTIME" + mPlugId, str_sunup);
		editor.putString("SUNDOWNTIME" + mPlugId, str_sundown);
		editor.putString("SUNPEROID" + mPlugId, mPeroid);

		editor.commit();
	}

	private void loadData() {
		str_sunup = mSharedPreferences.getString("SUNUPTIME" + mPlugId,
				"06:00:00");
		str_sundown = mSharedPreferences.getString("SUNDOWNTIME" + mPlugId,
				"18:00:00");
		mPeroid = mSharedPreferences
				.getString("SUNPEROID" + mPlugId, "0000000");
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
					intent.setClass(DetailGrowLightAutoActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightAutoActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.tv_light_sunup_time :
				showDialog(tv_light_sunup_time, tv_light_sunup_time.getText()
						.toString());
				break;
			case R.id.tv_light_sundown_time :
				showDialog(tv_light_sundown_time, tv_light_sundown_time
						.getText().toString());
				break;
			case R.id.btn_submit :
				do_commit();
				break;
			case R.id.layout_period_header :
				Intent intent = new Intent();
				intent.setClass(this, SetPeriodActivity.class);
				intent.putExtra("selected_days", mSelDays);
				startActivityForResult(intent, 0);
				break;
			default :
				break;
		}
	}

	public void initPeriod(String[] selDays) {
		mDays = getResources().getStringArray(R.array.current_week);
		tv_select_days.setTag(getPeriodValue());
		tv_select_days.setText(getPeriodText());
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

	private void initNewTimer() {
		// for (int i = 0; i < 7; i++) {
		// mSelDays[i] = "1";
		// }
		for (int i = 0; i < 7; i++) {
			mSelDays[i] = mPeroid.substring(i, i + 1);
		}
		initPeriod(mSelDays);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (null == data) {
			return;
		}
		if (0 == resultCode) {
			mSelDays = data.getStringArrayExtra("selected_days");
		}
		tv_select_days.setTag(getPeriodValue());
		tv_select_days.setText(getPeriodText());
	}

	private void do_commit() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(
				DetailGrowLightAutoActivity.this,
				getString(R.string.smartplug_growlight_modify_sunset), false);
		mProgress.show();

		String data = tv_light_sunup_time.getText().toString() + ","
				+ tv_light_sundown_time.getText().toString() + ","
				+ tv_select_days.getTag();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_SET_SUNTIME)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(data);

		sendMsg(true, sb.toString(), true);
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
		ScreenInfo screenInfo = new ScreenInfo(DetailGrowLightAutoActivity.this);
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
				.getString(R.string.smartplug_growlight_auto));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		tv_light_sun_cur_value = (TextView) findViewById(R.id.tv_light_sun_cur_value);
		tv_light_sunup_time = (TextView) findViewById(R.id.tv_light_sunup_time);
		tv_light_sundown_time = (TextView) findViewById(R.id.tv_light_sundown_time);
		tv_select_days = (TextView) findViewById(R.id.tv_select_days);

		tv_light_sunup_time.setText(str_sunup);
		tv_light_sundown_time.setText(str_sundown);

		layout_period_header = (RelativeLayout) findViewById(R.id.layout_period_header);
		layout_period_header.setOnClickListener(this);

		btn_submit = (Button) findViewById(R.id.btn_submit);

		tv_light_sunup_time.setOnClickListener(this);
		tv_light_sundown_time.setOnClickListener(this);
		btn_submit.setOnClickListener(this);

		if (valueTimer != null) {
			valueTimer.cancel();
		}
		if (valueTimerTask != null) {
			valueTimerTask.cancel();
		}
		valueTimer = new Timer();
		valueTimerTask = new TimerTask() {

			@Override
			public void run() {
				int sunup = getMinutFromTime(str_sunup);
				int sundown = getMinutFromTime(str_sundown);
				int curTime = getCurrentMinut();
				int value = getCurValue(sunup, sundown, curTime);

				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = value;
				mHandler.sendMessage(msg);
				// tv_light_sun_cur_value.setText(String.valueOf(value));
			}

		};
		valueTimer.schedule(valueTimerTask, 500, 60 * 1000);

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

		tv_light_sunup_time.setText(str_sunup);
		tv_light_sundown_time.setText(str_sundown);

	}

	/*
	 * 日出日落函数抛物线模式： f(x) = -1 * k * (x - (sunup+sunup)/2) * (x -
	 * (sunup+sunup)/2) + 100;
	 * 
	 * 解出来 a的值为：
	 * 
	 * k = 400 / ((sunup - sundown) * (sunup - sundown))
	 */
	private int getCurValue(int sunupTime, int sundownTime, int curTime) {
		double k = 400 * 1.0 / ((sunupTime - sundownTime) * (sunupTime - sundownTime));
		double value = -1.0 * k * (curTime - ((sunupTime + sundownTime) / 2.0))
				* (curTime - ((sunupTime + sundownTime) / 2.0)) + 100;

		if (value < 0) {
			value = 0;
		}

		return (int) value;
	}

	private int getMinutFromTime(String sunTime) {
		String[] buffer = sunTime.split(":");
		if (buffer.length < 2) {
			return 0;
		}
		try {
			int hours = Integer.valueOf(buffer[0]);
			int minuts = Integer.valueOf(buffer[1]);

			int value = hours * 60 + minuts;
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}
	private int getCurrentMinut() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
		String strTemp = df.format(new Date());
		int temp = getMinutFromTime(strTemp);
		return temp;
	}
}
