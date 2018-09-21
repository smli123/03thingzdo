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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.internet.RevCmdFromSocketThread;
import com.thingzdo.internet.UDPClient;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class DetailGrowLightTimeCurveActivity extends TitledActivity
		implements
			OnClickListener {

	private LinearLayout ll_chart;
	private LineChart mLineChart;

	private SmartPlugHelper mPlugHelper = null;
	private SmartPlugDefine mPlug = null;
	private String mPlugId = "0";
	private String mPlugIp = "0.0.0.0";

	private RevCmdFromSocketThread mTcpSocketThread = null;

	private List<Integer> lists = new ArrayList<Integer>();

	private void setLists() {
		lists.clear();
		for (int i = 1; i < 20; i++) {
			int value = ((int) (Math.random() * 100));
			lists.add(value);
		}
	}

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						DetailGrowLightTimeCurveActivity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						DetailGrowLightTimeCurveActivity.this, intent)) {
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
				R.layout.activity_plug_detail_growlight_timercurve, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

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

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		drawTheChart();
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
		renderer.setChartTitle("访问量统计");
		renderer.setChartTitleTextSize(50);

		// 图表与四周的边距
		renderer.setMargins(new int[]{80, 80, 50, 50});

		// 设置X,Y轴title的内容和大小
		renderer.setXTitle("日期");
		renderer.setYTitle("访问数");
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
		renderer.setYAxisMax(100);

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
