package com.thingzdo.ui.control;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class DetailGrowLightTimeCurveActivity extends TitledActivity
		implements
			OnClickListener {

	private LinearLayout ll_chart;
	private LineChart mLineChart;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugGrowLightTimerCurvePointHelper mTimerHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_01 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_02 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_03 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_04 = new ArrayList<GrowLightTimerCurvePointDefine>();
	private ArrayList<GrowLightTimerCurvePointDefine> mTimer_05 = new ArrayList<GrowLightTimerCurvePointDefine>();

	private ArrayList<Integer> yList_01 = new ArrayList<Integer>();
	private ArrayList<Integer> yList_02 = new ArrayList<Integer>();
	private ArrayList<Integer> yList_03 = new ArrayList<Integer>();
	private ArrayList<Integer> yList_04 = new ArrayList<Integer>();
	private ArrayList<Integer> yList_05 = new ArrayList<Integer>();

	private ArrayList<String> xList_01 = new ArrayList<String>();
	private ArrayList<String> xList_02 = new ArrayList<String>();
	private ArrayList<String> xList_03 = new ArrayList<String>();
	private ArrayList<String> xList_04 = new ArrayList<String>();
	private ArrayList<String> xList_05 = new ArrayList<String>();

	private List<Integer> lists = new ArrayList<Integer>();

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightTimeCurveActivity.this, intent)) {
					updateUI();
				}
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_timercurve, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
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

		// drawTheChart();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				queryAllTimeCurvePoint();
			}
		}, 1000);
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

		copyData(mTimer_01, xList_01, yList_01);
		copyData(mTimer_02, xList_02, yList_02);
		copyData(mTimer_03, xList_03, yList_03);
		copyData(mTimer_04, xList_04, yList_04);
		copyData(mTimer_05, xList_05, yList_05);

		// 根据数据重新画图
		drawTheChart();
	}

	private void copyData(ArrayList<GrowLightTimerCurvePointDefine> mTimer,
			ArrayList<String> xList, ArrayList<Integer> yList) {
		xList.clear();
		yList.clear();

		for (int i = 0; i < mTimer.size(); i++) {
			GrowLightTimerCurvePointDefine ti = mTimer.get(i);
			xList.add(ti.mPowerOnTime);
			yList.add((Integer) ti.light);
		}
	}

	private void setLists() {
		lists.clear();
		for (int i = 0; i < yList_01.size(); i++) {
			lists.add(yList_01.get(i));
		}

		// for (int i = 1; i < 3; i++) {
		// int value = ((int) (Math.random() * 100));
		// lists.add(value);
		// }
	}

	public void drawTheChart() {
		XYMultipleSeriesRenderer mRenderer = getXYMulSeriesRenderer();

		XYSeriesRenderer renderer = getXYSeriesRenderer();

		mRenderer.addSeriesRenderer(renderer);

		setLists();

		XYMultipleSeriesDataset dataset = getDataSet();

		GraphicalView chartView = ChartFactory.getLineChartView(this, dataset,
				mRenderer);

		ll_chart.addView(chartView, 0);
	}

	public XYSeriesRenderer getXYSeriesRenderer() {
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		// 设置折线宽度
		renderer.setLineWidth(2);
		// 设置折线颜色
		renderer.setColor(Color.GRAY);
		renderer.setDisplayBoundingPoints(true);
		// 点的样式
		renderer.setPointStyle(PointStyle.CIRCLE);
		// 设置点的大小
		renderer.setPointStrokeWidth(3);
		// 设置数值显示的字体大小
		renderer.setChartValuesTextSize(30);
		// 显示数值
		renderer.setDisplayChartValues(true);
		return renderer;
	}

	public XYMultipleSeriesDataset getDataSet() {
		XYMultipleSeriesDataset barDataset = new XYMultipleSeriesDataset();
		CategorySeries barSeries = new CategorySeries("2016年");

		for (int i = 0; i < lists.size(); i++) {
			barSeries.add(lists.get(i));
		}

		barDataset.addSeries(barSeries.toXYSeries());
		return barDataset;
	}

	public XYMultipleSeriesRenderer getXYMulSeriesRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setMarginsColor(Color.argb(0x00, 0xF3, 0xF3, 0xF3));

		// 设置背景颜色
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);

		// 设置Title的内容和大小
		renderer.setChartTitle("亮度时间曲线");
		renderer.setChartTitleTextSize(50);

		// 图表与四周的边距
		renderer.setMargins(new int[]{80, 80, 50, 50});

		// 设置X,Y轴title的内容和大小
		renderer.setXTitle("时间");
		renderer.setYTitle("亮度");
		renderer.setAxisTitleTextSize(30);
		// renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.BLACK);

		// 图例文字的大小
		renderer.setLegendTextSize(20);

		// x、y轴上刻度颜色和大小
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setLabelsTextSize(20);
		renderer.setYLabelsPadding(30);

		// 设置X轴的最小数字和最大数字，由于我们的数据是从1开始，所以设置为0.5就可以在1之前让出一部分
		// 有兴趣的童鞋可以删除下面两行代码看一下效果
		renderer.setPanEnabled(false, false);

		// 显示网格
		renderer.setShowGrid(true);

		// X,Y轴上的数字数量
		renderer.setXLabels(10);
		renderer.setYLabels(10);

		// 设置X轴的最小数字和最大数字
		renderer.setXAxisMin(1);
		renderer.setXAxisMax(20);
		// 设置Y轴的最小数字和最大数字
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(255);

		// 设置渲染器显示缩放按钮
		renderer.setZoomButtonsVisible(true);
		// 设置渲染器允许放大缩小
		renderer.setZoomEnabled(true);
		// 消除锯齿
		renderer.setAntialiasing(true);

		// 刻度线与X轴坐标文字左侧对齐
		renderer.setXLabelsAlign(Paint.Align.LEFT);
		// Y轴与Y轴坐标文字左对齐
		renderer.setYLabelsAlign(Paint.Align.LEFT);

		// 允许左右拖动,但不允许上下拖动.
		renderer.setPanEnabled(true, false);

		return renderer;
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
					intent.setClass(DetailGrowLightTimeCurveActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightTimeCurveActivity.this,
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
				.getString(R.string.smartplug_growlight_timecurve));
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		ll_chart = (LinearLayout) findViewById(R.id.ll_chart);
		// mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
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
