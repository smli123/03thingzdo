package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thingzdo.SwipeMenuListView.SwipeMenu;
import com.thingzdo.SwipeMenuListView.SwipeMenuCreator;
import com.thingzdo.SwipeMenuListView.SwipeMenuItem;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnMenuItemClickListener;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnSwipeListener;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugTimerHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailCurtainActivity extends TitledActivity
		implements
			OnClickListener,
			SeekBar.OnSeekBarChangeListener {

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private boolean mOnline = false;

	private String mErrorMsg = "";

	private SharedPreferences mCurtainInfo = null;
	private int value_curtain_pos = 100;

	// Layout widget Define
	private Button btn_windowopen;
	private Button btn_windowclose;
	private Button btn_windowpause;
	private Button btn_windowinit;

	private SeekBar sb_curtain_max_length;
	private TextView tv_curtain_max_length;
	private TextView tv_wifiinfo = null;

	// TimerTask
	private RelativeLayout mLayoutTimer = null;
	private RelativeLayout mLayoutStatic_power = null;
	private TextView mTxtStatic_power = null;
	private ImageView mImgAddTimer = null;
	private SwipeMenuListView mListView;

	private Context mContext;
	private SmartPlugTimerHelper mTimerHelper = null;
	private PlugTimerlistAdapter mAdapter;
	private ArrayList<TimerDefine> timers = new ArrayList<TimerDefine>();
	private TimerDefine mTimer = null;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(PubDefine.PLUG_SETTIMER_ENABLED)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
						if (null != mTimer) {
							mTimer.mEnable = mFocusTimerEnabled;
							if (0 < mTimerHelper.modifyTimer(mTimer)) {
								init();
							}
						}

						break;
					default :
						PubFunc.thinzdoToast(DetailCurtainActivity.this,
								message);
						break;
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_DEL_TIMERTASK)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}
				int ret = intent.getIntExtra("RESULT", 0);
				String message = intent.getStringExtra("MESSAGE");
				switch (ret) {
					case 0 :
						timeoutHandler.removeCallbacks(timeoutProcess);

						if (null != mTimer) {
							if (true == mTimerHelper.deleteTimer(mTimer.mId)) {
								init();
							}
						}

						break;
					default :
						PubFunc.thinzdoToast(DetailCurtainActivity.this,
								message);
						break;
				}
			}
			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailCurtainActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_CURTAIN_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}

				String plugid = intent.getStringExtra("PLUGID");
				int result = intent.getIntExtra("RESULT", 0);
				int status = intent.getIntExtra("STATUS", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (result == 0) {
					// PubFunc.thinzdoToast(DetailCurtainActivity.this,
					// getString(R.string.oper_success));
				} else {
					PubFunc.thinzdoToast(DetailCurtainActivity.this, message);
				}

			}

			if (intent.getAction().equals(
					PubDefine.PLUG_CURTAIN_QUERYPOSITION_ACTION)) {
				timeoutHandler.removeCallbacks(timeoutProcess);
				if (null != mProgress) {
					mProgress.dismiss();
				}

				String plugid = intent.getStringExtra("PLUGID");
				int result = intent.getIntExtra("RESULT", 0);
				int pos = intent.getIntExtra("POSITION", 0);
				String message = intent.getStringExtra("MESSAGE");
				if (result == 0) {
					updateUI();
				} else {
					PubFunc.thinzdoToast(DetailCurtainActivity.this, message);
				}

			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_CURTAIN)) {
				if (true == NotifyProcessor.curtainNotify(
						DetailCurtainActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				String routeName = "";
				WifiManager Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (Wifi != null && Wifi.isWifiEnabled()) {
					WifiInfo wifiInfo = Wifi.getConnectionInfo();
					if (wifiInfo != null && wifiInfo.getSSID() != null) {
						routeName = wifiInfo.getSSID().replace("\"", "");
					}
				}
				tv_wifiinfo.setText(routeName);
			}

			if (intent.getAction().equals(PubDefine.PLUG_BACK2AP_ACTION)) {
				int code = intent.getIntExtra("RESULT", 0);
				switch (code) {
					case 0 :
						Button left_button = (Button) findViewById(R.id.titlebar_leftbutton);
						left_button.performClick();
						break;
					default :
						break;
				}
			}
		}
	};

	private void saveInfo() {
		SharedPreferences.Editor editor = mCurtainInfo.edit();
		editor.putInt(mPlugId + "_value_curtain_pos", value_curtain_pos);
		editor.commit();
	}

	private void loadInfo() {
		value_curtain_pos = mCurtainInfo.getInt(mPlugId + "_value_curtain_pos",
				100);
	}

	private void RefreshTimers() {
		timers.clear();
		timers = mTimerHelper.getAllTimer(mPlugId);

		mAdapter = new PlugTimerlistAdapter(this, mPlugId, mPlugIp, timers,
				mTimerHandler, mPlug.mIsOnline);
		mListView.setAdapter(mAdapter);

		mAdapter.notifyDataSetChanged();

		initStatics(timers);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_detail_curtain,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleRightButton(R.string.smartplug_title_plug_detail,
				R.drawable.title_btn_selector, this);

		mContext = this;

		mPlugHelper = new SmartPlugHelper(this);
		mTimerHelper = new SmartPlugTimerHelper(this);
		Intent intent = getIntent();
		mPlugId = intent.getStringExtra("PLUGID");
		if (TextUtils.isEmpty(mPlugId)) {
			mPlugId = "0";
		}
		mPlugIp = intent.getStringExtra("PLUGIP");
		mOnline = intent.getBooleanExtra("ONLINE", false);

		mCurtainInfo = getSharedPreferences("CurtainInfo"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);
		loadInfo();

		init();
		if (mPlug != null) {
			value_curtain_pos = mPlug.mPosition;
		}

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
		filter.addAction(PubDefine.PLUG_CURTAIN_ACTION);
		filter.addAction(PubDefine.PLUG_CURTAIN_QUERYPOSITION_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_CURTAIN);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(PubDefine.PLUG_BACK2AP_ACTION);

		filter.addAction(PubDefine.PLUG_SETTIMER_ENABLED);
		filter.addAction(PubDefine.PLUG_DEL_TIMERTASK);
		registerReceiver(mDetailRev, filter);

		UDPClient.getInstance().setIPAddress(mPlugIp);

		btn_windowopen = (Button) findViewById(R.id.btn_windowopen);
		btn_windowclose = (Button) findViewById(R.id.btn_windowclose);
		btn_windowpause = (Button) findViewById(R.id.btn_windowpause);
		btn_windowinit = (Button) findViewById(R.id.btn_windowinit);

		if (mOnline == true) {
			btn_windowopen.setEnabled(true);
			btn_windowclose.setEnabled(true);
			btn_windowpause.setEnabled(true);
		} else {
			btn_windowopen.setEnabled(false);
			btn_windowclose.setEnabled(false);
			btn_windowpause.setEnabled(false);
		}

		btn_windowopen.setOnClickListener(DetailCurtainActivity.this);
		btn_windowclose.setOnClickListener(DetailCurtainActivity.this);
		btn_windowpause.setOnClickListener(DetailCurtainActivity.this);
		btn_windowinit.setOnClickListener(DetailCurtainActivity.this);

		tv_curtain_max_length = (TextView) findViewById(R.id.tv_curtain_max_length);
		sb_curtain_max_length = (SeekBar) findViewById(R.id.sb_curtain_max_length);
		if (null != sb_curtain_max_length) {
			sb_curtain_max_length.setProgress(value_curtain_pos);
			sb_curtain_max_length.setOnSeekBarChangeListener(this);
			tv_curtain_max_length.setText(String.valueOf(value_curtain_pos));
		}

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		// 无线路由器
		tv_wifiinfo = (TextView) findViewById(R.id.tv_wifiinfo);
		setTitle(mPlug.mPlugName);

		RefreshTimers();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				queryPosition();
			}
		}, 100);
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
	public void onProgressChanged(SeekBar bar, int pos, boolean arg2) {
		switch (bar.getId()) {
			case R.id.sb_curtain_max_length :
				tv_curtain_max_length
						.setText(String.valueOf(bar.getProgress()));
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
			case R.id.sb_curtain_max_length :
				// adjust Bar's progress position. step = 5;
				adjust_progressbar(bar);

				value_curtain_pos = bar.getProgress();
				tv_curtain_max_length
						.setText(String.valueOf(value_curtain_pos));
				saveInfo();

				windowPos(tv_curtain_max_length.getText().toString());
				break;
			default :
				break;
		}
	}

	private void adjust_progressbar(SeekBar bar) {
		int cur_pos = bar.getProgress();
		cur_pos = Math.round(cur_pos / 5) * 5;
		bar.setProgress(cur_pos);
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
					intent.putExtra("ONLINE", mOnline);
					intent.setClass(DetailCurtainActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailCurtainActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			// Window Controller
			case R.id.btn_windowopen :
				windowopen();
				break;
			case R.id.btn_windowclose :
				windowclose();
				break;
			case R.id.btn_windowpause :
				windowpause();
				break;
			case R.id.btn_windowinit :
				windowInit();
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

		mLayoutTimer = (RelativeLayout) findViewById(R.id.layout_timer_all);
		mLayoutStatic_power = (RelativeLayout) findViewById(R.id.layout_power_static);
		mTxtStatic_power = (TextView) findViewById(R.id.txt_power_static);
		mImgAddTimer = (ImageView) findViewById(R.id.img_timeradd);

		mAdapter = new PlugTimerlistAdapter(this, mPlugId, mPlugIp, timers,
				mTimerHandler, mPlug.mIsOnline);
		// mAdapter.setEditable(mIsEditable);

		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(null);
			} else {
				mLayoutTimer.setOnClickListener(addTimerClick);
			}
		} else if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
			// internet
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(addTimerClick);
			} else {
				mLayoutTimer.setOnClickListener(addTimerClick);
			}
		} else {
			mLayoutTimer.setOnClickListener(addTimerClick);
		}

		mListView = (SwipeMenuListView) findViewById(R.id.timer_list);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				Intent intent = new Intent(DetailCurtainActivity.this,
						PlugTimerActivity.class);
				// intent.putExtra("PLUGID", mPlugId);
				intent.putExtra("PLUGIP", mPlugIp);
				ImageView imgType = (ImageView) v
						.findViewById(R.id.plug_img_type);
				ImageView imgEnable = (ImageView) v
						.findViewById(R.id.plug_img_enabled);
				RelativeLayout layout_Whole = (RelativeLayout) v
						.findViewById(R.id.lay_plug_whole);
				TextView txtDay = (TextView) v
						.findViewById(R.id.txtTimerPeriod);
				intent.putExtra("PLUGID", txtDay.getContentDescription()
						.toString());
				intent.putExtra("TIMER_TYPE", Integer.parseInt(imgType
						.getContentDescription().toString()));
				intent.putExtra("TIMERID", layout_Whole.getContentDescription()
						.toString());
				boolean isActive = true;

				intent.putExtra("ACTIVE", isActive);
				DetailCurtainActivity.this.startActivity(intent);
			}
		});

		mListView.setAdapter(mAdapter);

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				WindowManager wm = (WindowManager) DetailCurtainActivity.this
						.getSystemService(Context.WINDOW_SERVICE);

				int width = wm.getDefaultDisplay().getWidth();
				deleteItem.setWidth(width / 5/* (180) */);
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);

			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0 :
						// delete timer
						TimerDefine timer = (TimerDefine) mListView
								.getItemAtPosition(position);
						Message msg = new Message();
						msg.what = 2;
						msg.obj = timer.mPlugId + " "
								+ String.valueOf(timer.mTimerId);
						mTimerHandler.sendMessage(msg);
						// deleteTimer();
						// mAdapter.notifyDataSetChanged();
						break;
					default :
						break;
				}
			}
		});

		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
	}

	private int mFocusTimerId = 0;
	private boolean mFocusTimerEnabled = true;
	private Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			// mFocusTimerId = msg.arg1;
			String temp = (String) msg.obj;
			int location = temp.indexOf(" ");
			if (location != -1) {
				String plugID = temp.substring(0, location);
				mFocusTimerId = Integer.parseInt(temp.substring(location + 1));
				mTimer = mTimerHelper.getTimer(plugID, mFocusTimerId);
			}

			// mTimer = mTimerHelper.getTimer(mPlugId, mFocusTimerId);
			if (null == mTimer) {
				return;
			}

			if (1 == what) {
				// enabled
				mFocusTimerEnabled = !mTimer.mEnable;
				enabledTimer(mFocusTimerEnabled);

			} else {
				// delete
				deleteTimer();
			}
		};
	};

	OnClickListener addTimerClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (null != mTimerHelper) {
				if (null != mTimerHelper.getAllTimer(mPlug.mPlugId)) {
					int count = mTimerHelper.getAllTimer(mPlug.mPlugId).size();
					if (10 <= count) {
						PubFunc.thinzdoToast(
								DetailCurtainActivity.this,
								DetailCurtainActivity.this
										.getString(R.string.smartplug_oper_timer_full));
						return;
					}
				}

			}

			// 启动 定时任务界面
			Intent intent = new Intent();
			intent.putExtra("PLUGID", mPlugId);
			intent.putExtra("PLUGIP", mPlugIp);
			intent.putExtra("TIMER_TYPE", PubDefine.TIMER_TYPE_POWER);
			intent.putExtra("MAXID", getMaxTimerID());
			intent.setClass(DetailCurtainActivity.this, PlugTimerActivity.class);
			startActivity(intent);
		}
	};

	private int getMaxTimerID() {
		int maxID = 1;
		boolean b_change = false;

		for (int i = 0; i < timers.size(); i++) {
			if (maxID <= timers.get(i).mTimerId) {
				maxID = timers.get(i).mTimerId;
				b_change = true;
			}
		}
		if (b_change == true) {
			maxID += 1;
		}

		return maxID;
	}

	OnClickListener editCLick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (null != mAdapter) {
				// mAdapter.setEditable(mIsEditable);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private void enabledTimer(boolean enabled) {

		mProgress = PubFunc.createProgressDialog(
				DetailCurtainActivity.this,
				(enabled
						? DetailCurtainActivity.this.getString(R.string.active)
						: DetailCurtainActivity.this
								.getString(R.string.deactive)), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_TIMERENABLED)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(enabled ? "1" : "0");

		sendMsg(true, sb.toString(), true);
	}

	private void deleteTimer() {
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
				DetailCurtainActivity.this.getString(R.string.deleting), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_DELTIMER)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId);

		sendMsg(true, sb.toString(), true);
	}

	private void initStatics(final ArrayList<TimerDefine> timers) {
		int count = getTaskCount(0, timers);
		mTxtStatic_power.setText(String.valueOf(count));
	}

	private int getTaskCount(final int type, final ArrayList<TimerDefine> timers) {
		int count = 0;
		for (TimerDefine timer : timers) {
			if (type == timer.mType) {
				count++;
			}
		}
		return count;
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
		mOnline = mPlug.mIsOnline;

		if (mOnline == true) {
			btn_windowopen.setEnabled(true);
			btn_windowclose.setEnabled(true);
			btn_windowpause.setEnabled(true);

			// 根据NotifyCurtain信息更新进度条的位置
			value_curtain_pos = mPlug.mPosition;
			sb_curtain_max_length.setProgress(value_curtain_pos);
			tv_curtain_max_length.setText(String.valueOf(value_curtain_pos));
			saveInfo();
		} else {
			btn_windowopen.setEnabled(false);
			btn_windowclose.setEnabled(false);
			btn_windowpause.setEnabled(false);
		}
	}

	private void windowopen() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
				getString(R.string.str_btn_curtainopen), false);
		mProgress.show();

		sendcommandtoWindowController("1");
	}
	private void windowclose() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
				getString(R.string.str_btn_curtainclose), false);
		mProgress.show();

		sendcommandtoWindowController("2");
	}
	private void windowpause() {
		mErrorMsg = getString(R.string.oper_error);
		mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
				getString(R.string.str_btn_curtainpause), false);
		mProgress.show();

		sendcommandtoWindowController("0");
	}

	// For Window Controller
	private void sendcommandtoWindowController(String part_command) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_CURTAIN)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(part_command);

		// RevCmdFromSocketThread.getInstance().setRunning(true);
		sendMsg(true, sb.toString(), true);

	}

	private void queryPosition() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_CURTAIN_QUERYPOSITION)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0");

		sendMsg(true, sb.toString(), true);
	}

	/* --------------------------APPPASSTHROUGH模式------------------------------- */
	private void windowInit() {
		// mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
		// getString(R.string.str_btn_curtaininit), false);
		// mProgress.show();

		// 0,HUOER_INIT,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712#
		String command_base = "CURTAIN_INIT," + PubStatus.g_CurUserName + ","
				+ mPlugId;
		String command = "0," + command_base;
		int command_length = command.getBytes().length + 1;

		StringBuffer sb = new StringBuffer();
		sb.append("APPPASSTHROUGH")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(command_length))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(command);

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else {
			sendMsg(false, command_base, true);
		}
	}

	private void windowPos(String pos_value) {
		// mProgress = PubFunc.createProgressDialog(DetailCurtainActivity.this,
		// getString(R.string.str_introduct_curtain_max_length), false);
		// mProgress.show();

		// 0,HUOER_POS,smli123hz,6044712,15,0,0,BACK2AP,smli123hz,6044712,100#
		String command_base = "CURTAIN_POS," + PubStatus.g_CurUserName + ","
				+ mPlugId + "," + pos_value;
		String command = "0," + command_base;
		int command_length = command.getBytes().length + 1;

		StringBuffer sb = new StringBuffer();
		sb.append("APPPASSTHROUGH")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(command_length))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(command);

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.Internet) {
			sendMsg(true, sb.toString(), true);
		} else {
			sendMsg(false, command_base, true);
		}
	}
}
