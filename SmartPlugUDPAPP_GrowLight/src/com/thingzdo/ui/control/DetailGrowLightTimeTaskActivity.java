package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import com.thingzdo.SwipeMenuListView.SwipeMenu;
import com.thingzdo.SwipeMenuListView.SwipeMenuCreator;
import com.thingzdo.SwipeMenuListView.SwipeMenuItem;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnMenuItemClickListener;
import com.thingzdo.SwipeMenuListView.SwipeMenuListView.OnSwipeListener;
import com.thingzdo.dataprovider.SmartPlugGrowLightTimerHelper;
import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.GrowLightTimerDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.TimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailGrowLightTimeTaskActivity extends TitledActivity
		implements
			OnClickListener {

	private RelativeLayout mLayoutTimer = null;
	private RelativeLayout mLayoutStatic_power = null;
	private TextView mTxtStatic_power = null;
	private ImageView mImgAddTimer = null;
	private SwipeMenuListView mListView;

	private Context mContext;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugGrowLightTimerHelper mTimerHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";
	private GrowLightTimerlistAdapter mAdapter;
	private ArrayList<GrowLightTimerDefine> timers = new ArrayList<GrowLightTimerDefine>();
	private GrowLightTimerDefine mTimer = null;

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightTimeTaskActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_ADD_TIMETASK_ACTION)) {

			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_DEL_TIMETASK_ACTION)) {

			}
			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_MOD_TIMETASK_ACTION)) {

			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_DEL_TIMETASK_ACTION)) {

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_timetask, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mContext = this;

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_ADD_TIMETASK_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_DEL_TIMETASK_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_MOD_TIMETASK_ACTION);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_QRY_TIMETASK_ACTION);

		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);
		mTimerHelper = new SmartPlugGrowLightTimerHelper(this);

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

		querydTimers();
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
								DetailGrowLightTimeTaskActivity.this,
								DetailGrowLightTimeTaskActivity.this
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
			intent.setClass(DetailGrowLightTimeTaskActivity.this,
					DetailGrowLightTimerActivity.class);
			startActivity(intent);
		}
	};

	OnClickListener editCLick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (null != mAdapter) {
				// mAdapter.setEditable(mIsEditable);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private void querydTimers() {
		mProgress = PubFunc.createProgressDialog(
				DetailGrowLightTimeTaskActivity.this,
				DetailGrowLightTimeTaskActivity.this
						.getString(R.string.tabhost_query), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_TIMETASK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId);

		sendMsg(true, sb.toString(), true);
	}

	private void enabledTimer(boolean enabled) {

		mProgress = PubFunc.createProgressDialog(
				DetailGrowLightTimeTaskActivity.this,
				(enabled
						? DetailGrowLightTimeTaskActivity.this
								.getString(R.string.active)
						: DetailGrowLightTimeTaskActivity.this
								.getString(R.string.deactive)), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_MOD_TIMETASK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light01)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light02)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light03)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light04)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light05)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPowerOnTime)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPeriod)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(enabled ? "1" : "0");

		sendMsg(true, sb.toString(), true);
	}

	private void deleteTimer() {
		mProgress = PubFunc.createProgressDialog(
				DetailGrowLightTimeTaskActivity.this,
				DetailGrowLightTimeTaskActivity.this
						.getString(R.string.deleting), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_DEL_TIMETASK)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mTimerId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light01)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light02)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light03)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light04)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.light05)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPowerOnTime)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mPeriod)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(mTimer.mEnable);

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
					intent.setClass(DetailGrowLightTimeTaskActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightTimeTaskActivity.this,
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

	private void initStatics(final ArrayList<GrowLightTimerDefine> timers) {
		int count = getTaskCount(0, timers);
		mTxtStatic_power.setText(String.valueOf(count));
	}

	private int getTaskCount(final int type,
			final ArrayList<GrowLightTimerDefine> timers) {
		int count = 0;
		for (GrowLightTimerDefine timer : timers) {
			if (type == timer.mType) {
				count++;
			}
		}
		return count;
	}

	private void init() {
		mPlug = mPlugHelper.getSmartPlug(mPlugId);
		if (null == mPlug) {
			return;
		}

		setTitle(SmartPlugApplication.getInstance().getResources()
				.getString(R.string.smartplug_growlight_timetask));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		mLayoutTimer = (RelativeLayout) findViewById(R.id.layout_timer_all);
		mLayoutStatic_power = (RelativeLayout) findViewById(R.id.layout_power_static);
		mTxtStatic_power = (TextView) findViewById(R.id.txt_power_static);
		mImgAddTimer = (ImageView) findViewById(R.id.img_timeradd);

		mAdapter = new GrowLightTimerlistAdapter(this, mPlugId, mPlugIp,
				timers, mTimerHandler, mPlug.mIsOnline);
		// mAdapter.setEditable(mIsEditable);
		initStatics(timers);

		if (PubDefine.SmartPlug_Connect_Mode.WiFi == PubDefine.g_Connect_Mode) {
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(null);
			} else {
				mLayoutTimer.setOnClickListener(addTimerClick);
			}
		} else if (PubDefine.SmartPlug_Connect_Mode.Internet == PubDefine.g_Connect_Mode) {
			// internet
			if (false == mPlug.mIsOnline) {
				mLayoutTimer.setOnClickListener(null);
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
				Intent intent = new Intent(
						DetailGrowLightTimeTaskActivity.this,
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
				DetailGrowLightTimeTaskActivity.this.startActivity(intent);

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
				WindowManager wm = (WindowManager) DetailGrowLightTimeTaskActivity.this
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
