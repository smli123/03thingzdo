package com.thingzdo.ui.growlightble;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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
import com.thingzdo.ui.control.PlugDetailInfoActivity;
import com.thingzdo.ui.manage.AddSocketActivity2;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
public class DetailGrowLightBleTimeCurveActivity extends DetailGrowLightBleBase
		implements
			OnClickListener {

	private LinearLayout ll_chart;

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

	// 另外一种曲线显示方法
	private LineChartView lineChart;

	private List<AxisValue> mAxisXValues_01 = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisXValues_02 = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisXValues_03 = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisXValues_04 = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisXValues_05 = new ArrayList<AxisValue>();

	private List<PointValue> mPointValues_01 = new ArrayList<PointValue>();
	private List<PointValue> mPointValues_02 = new ArrayList<PointValue>();
	private List<PointValue> mPointValues_03 = new ArrayList<PointValue>();
	private List<PointValue> mPointValues_04 = new ArrayList<PointValue>();
	private List<PointValue> mPointValues_05 = new ArrayList<PointValue>();

	private String mLineColor_01 = "#bf360c";
	private String mLineColor_02 = "#37474f";
	private String mLineColor_03 = "#4a148c";
	private String mLineColor_04 = "#2196f3";
	private String mLineColor_05 = "#64dd17";

	// Line的样式
	private ValueShape mLineShape = ValueShape.CIRCLE; // 折线图数据点的形状;圆形
														// （三种：ValueShape.SQUARE
														// ValueShape.CIRCLE
														// ValueShape.DIAMOND）
	private boolean mLineCubic = false; // 曲线是否平滑，即是曲线还是折线
	private boolean mLineFilled = false; // 是否填充曲线的面积
	private boolean mLineHasLabels = true; // 曲线的数据坐标是否加上备注
	private boolean mLineHasLabelsOnlyForSelected = true;// 点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
	private boolean mLineHasLines = true; // 是否用线显示。如果为false 则没有曲线只有点显示
	private boolean mLineHasPoints = true; // 是否显示圆点 如果为false
											// 则没有原点只有点显示（每个数据点都是个大的圆点）

	private List<Integer> lists = new ArrayList<Integer>();

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
			// if (true == NotifyProcessor.onlineNotify(
			// DetailGrowLightBleTimeCurveActivity.this, intent)) {
			// updateUI();
			// }
			// }

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

		refreshView();

		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		// queryAllTimeCurvePoint();
		// }
		// }, 1000);
	}

	// 重新获取数据&刷新界面信息
	private void refreshView() {
		// 重新获取数据
		mTimer_01.clear();
		mTimer_01 = mTimerHelper.getAllTimer(mPlugId, 0);
		mTimer_02.clear();
		mTimer_02 = mTimerHelper.getAllTimer(mPlugId, 1);
		mTimer_03.clear();
		mTimer_03 = mTimerHelper.getAllTimer(mPlugId, 2);
		mTimer_04.clear();
		mTimer_04 = mTimerHelper.getAllTimer(mPlugId, 3);
		mTimer_05.clear();
		mTimer_05 = mTimerHelper.getAllTimer(mPlugId, 4);

		// 使用另外一种方法绘制曲线
		getData(mTimer_01, mAxisXValues_01, mPointValues_01);
		getData(mTimer_02, mAxisXValues_02, mPointValues_02);
		getData(mTimer_03, mAxisXValues_03, mPointValues_03);
		getData(mTimer_04, mAxisXValues_04, mPointValues_04);
		getData(mTimer_05, mAxisXValues_05, mPointValues_05);

		initLineChart();
	}

	private void getData(ArrayList<GrowLightTimerCurvePointDefine> mTimer,
			List<AxisValue> xList, List<PointValue> yList) {
		xList.clear();
		yList.clear();

		for (int i = 0; i < mTimer.size(); i++) {
			GrowLightTimerCurvePointDefine ti = mTimer.get(i);
			xList.add((new AxisValue(i).setLabel(ti.mPowerOnTime)));
			yList.add(new PointValue(i, ti.light));
		}
	}

	private Line getLine_01() {
		Line line = new Line(mPointValues_01).setColor(Color
				.parseColor(mLineColor_01));
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);

		return line;
	}

	private Line getLine_02() {
		Line line = new Line(mPointValues_02).setColor(Color
				.parseColor(mLineColor_02)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);

		return line;
	}

	private Line getLine_03() {
		Line line = new Line(mPointValues_03).setColor(Color
				.parseColor(mLineColor_03)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);

		return line;
	}

	private Line getLine_04() {
		Line line = new Line(mPointValues_04).setColor(Color
				.parseColor(mLineColor_04)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);

		return line;
	}

	private Line getLine_05() {
		Line line = new Line(mPointValues_05).setColor(Color
				.parseColor(mLineColor_05)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);

		return line;
	}
	private void initLineChart() {
		Line line_01 = getLine_01();
		Line line_02 = getLine_02();
		Line line_03 = getLine_03();
		Line line_04 = getLine_04();
		Line line_05 = getLine_05();

		List<Line> lines = new ArrayList<Line>();
		lines.add(line_01);
		lines.add(line_02);
		lines.add(line_03);
		lines.add(line_04);
		lines.add(line_05);
		LineChartData data = new LineChartData();
		data.setLines(lines);

		// X坐标轴
		Axis axisX = new Axis(); // X轴
		axisX.setHasTiltedLabels(false); // X坐标轴字体是斜的显示还是直的，true是斜的显示
		axisX.setTextColor(Color.GRAY); // 设置字体颜色
		axisX.setName("模拟日出曲线"); // 表格名称
		axisX.setTextSize(10);// 设置字体大小
		axisX.setMaxLabelChars(8); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
		axisX.setValues(mAxisXValues_01); // 填充X轴的坐标名称
		data.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		// Y坐标轴: 根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
		Axis axisY = new Axis(); // Y轴
		axisY.setName("亮度值");// y轴标注
		axisY.setTextSize(10);// 设置字体大小
		data.setAxisYLeft(axisY); // Y轴设置在左边
		// data.setAxisYRight(axisY); //y轴设置在右边

		// 设置行为属性，支持缩放、滑动以及平移
		lineChart.setInteractive(true);
		lineChart.setZoomType(ZoomType.HORIZONTAL);
		lineChart.setMaxZoom((float) 2);// 最大方法比例
		lineChart.setContainerScrollEnabled(true,
				ContainerScrollType.HORIZONTAL);
		lineChart.setLineChartData(data);
		lineChart.setVisibility(View.VISIBLE);
		/**
		 * 注：下面的7，10只是代表一个数字去类比而已
		 * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com
		 * /tools/programming/library
		 * -hellocharts-charting-library-t2904456/page2）;
		 */
		Viewport v = new Viewport(lineChart.getMaximumViewport());
		v.left = 0;
		v.right = 7;
		lineChart.setCurrentViewport(v);
	}

	private void queryAllTimeCurvePoint() {
		for (int i = 0; i < 5; i++) {
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
		sb.append(SmartPlugMessage.CMD_SP_GROWLIGHT_QRY_TIMECURVE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(mPlugId)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append(channel);

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
					intent.setClass(DetailGrowLightBleTimeCurveActivity.this,
							PlugDetailInfoActivity.class);
					startActivity(intent);
				} else {
					// WiFi直连：“重选”界面
					// PubDefine.disconnect();
					disconnectSocket();
					Intent intent = new Intent();
					intent.setClass(DetailGrowLightBleTimeCurveActivity.this,
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

		// setTitle(SmartPlugApplication.getInstance().getResources()
		// .getString(R.string.smartplug_growlight_timecurve));
		// setTitleLeftButton(R.string.smartplug_goback,
		// R.drawable.title_btn_selector, this);

		// 设置曲线的各种数据
		lineChart = (LineChartView) findViewById(R.id.line_chart);
		refreshView();
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
