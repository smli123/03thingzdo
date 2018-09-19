package com.thingzdo.ui.control;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailGrowLightTimeCurvePointActivity extends TitledActivity
		implements
			OnClickListener {

	private TextView tv_add;
	private TextView tv_del;
	private TextView tv_modify;
	private TextView tv_clear;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugGrowLightTimerCurvePointHelper mTimerHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_01 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_02 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_03 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_04 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_05 = new ArrayList<GrowLightTimerCurvePointDefine>();

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightTimeCurvePointActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailGrowLightTimeCurvePointActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}

			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_QRY_TIMECURVEPOINT_ACTION)) {
				String moduleID = intent.getStringExtra("MODULEID");
				int channel = intent.getIntExtra("CHANNEL", 0);

				refreshView();

			}
			if (intent.getAction().equals(
					PubDefine.PLUG_GROWLIGHT_SET_TIMECURVEPOINT_ACTION)) {
				String moduleID = intent.getStringExtra("MODULEID");
				int channel = intent.getIntExtra("CHANNEL", 0);

				queryTimeCurvePoint(channel);
			}
		}
	};

	// 重新获取数据&刷新界面信息
	private void refreshView() {
		// 重新获取数据
		mTimer_01.clear();
		mTimer_01 = mTimerHelper.getAllTimer(mPlugId, 1);
		mTimer_02.clear();
		mTimer_02 = mTimerHelper.getAllTimer(mPlugId, 2);
		mTimer_03.clear();
		mTimer_03 = mTimerHelper.getAllTimer(mPlugId, 3);
		mTimer_04.clear();
		mTimer_04 = mTimerHelper.getAllTimer(mPlugId, 4);
		mTimer_05.clear();
		mTimer_05 = mTimerHelper.getAllTimer(mPlugId, 5);

		// 根据数据重新画图
		DrawView();
	}

	private void DrawView() {
		Toast.makeText(this, "Now is drawing...", Toast.LENGTH_SHORT).show();
	}

	private void queryAllTimeCurvePoint() {
		for (int i = 1; i <= 5; i++) {
			queryTimeCurvePoint(i);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void queryTimeCurvePoint(int channel) {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_TIMECURVEPOINT)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(channel);

		sendMsg(true, sb.toString(), true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_timercurvepoint, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_GROWLIGHT_QRY_TIMECURVEPOINT_ACTION);
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

		UDPClient.getInstance().setIPAddress(mPlugIp);

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		new Handler().postDelayed(new Runnable() {
			public void run() {
				queryAllTimeCurvePoint();
			}
		}, 1000);
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
					intent.setClass(DetailGrowLightTimeCurvePointActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightTimeCurvePointActivity.this,
							AddSocketActivity2.class);
					startActivity(intent);
					if (PubDefine.SmartPlug_Connect_Mode.WiFi != PubDefine.g_Connect_Mode) {
						finish();
					}
				}
				break;
			case R.id.tv_add :
				timecurve_add();
				break;
			case R.id.tv_del :
				timecurve_del();
				break;
			case R.id.tv_modify :
				timecurve_modify();
				break;
			case R.id.tv_clear :
				timecurve_clear();
				break;
			default :
				break;
		}
	}

	private void timecurve_add() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("OPERATE", "ADD");
		intent.setClass(DetailGrowLightTimeCurvePointActivity.this,
				DetailGrowLightPointActivity.class);
		startActivity(intent);
	}

	private void timecurve_del() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("OPERATE", "DEL");
		intent.setClass(DetailGrowLightTimeCurvePointActivity.this,
				DetailGrowLightPointActivity.class);
		startActivity(intent);
	}

	private void timecurve_modify() {
		Intent intent = new Intent();
		intent.putExtra("PLUGID", mPlugId);
		intent.putExtra("OPERATE", "MODIFY");
		intent.setClass(DetailGrowLightTimeCurvePointActivity.this,
				DetailGrowLightPointActivity.class);
		startActivity(intent);
	}

	private void timecurve_clear() {
		// 提示是否要删除
		mTimerHelper.clearTimer(mPlugId);

		refreshView();
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

		tv_add = (TextView) findViewById(R.id.tv_add);
		tv_del = (TextView) findViewById(R.id.tv_del);
		tv_modify = (TextView) findViewById(R.id.tv_modify);
		tv_clear = (TextView) findViewById(R.id.tv_clear);

		tv_add.setOnClickListener(this);
		tv_del.setOnClickListener(this);
		tv_modify.setOnClickListener(this);
		tv_clear.setOnClickListener(this);
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
