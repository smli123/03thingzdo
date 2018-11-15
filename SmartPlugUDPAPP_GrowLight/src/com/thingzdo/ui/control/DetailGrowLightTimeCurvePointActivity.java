package com.thingzdo.ui.control;

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
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private Spinner spinner_channel;

	private List<String> irlist_channel = new ArrayList<String>();
	private int i_Current_channel = 0;

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

	// 0，渐变 1，跳变
	private int iShowMode_01 = 0;
	private int iShowMode_02 = 0;
	private int iShowMode_03 = 0;
	private int iShowMode_04 = 0;
	private int iShowMode_05 = 0;

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
	private boolean mLineHasLabels = false; // 曲线的数据坐标是否加上备注
	private boolean mLineHasLabelsOnlyForSelected = true;// 点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
	private boolean mLineHasLines = true; // 是否用线显示。如果为false 则没有曲线只有点显示
	private boolean mLineHasPoints = true; // 是否显示圆点 如果为false
											// 则没有原点只有点显示（每个数据点都是个大的圆点）
	private int mLinePointRadius = 3; // 设置线上节点的大小
	private int mLineStrokeWidth = 2; // 设置线的粗细

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private int i_Current_lushu = 5;

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
		mTimer_01 = mTimerHelper.getAllTimer(mPlugId, 0);
		mTimer_02.clear();
		mTimer_02 = mTimerHelper.getAllTimer(mPlugId, 1);
		mTimer_03.clear();
		mTimer_03 = mTimerHelper.getAllTimer(mPlugId, 2);
		mTimer_04.clear();
		mTimer_04 = mTimerHelper.getAllTimer(mPlugId, 3);
		mTimer_05.clear();
		mTimer_05 = mTimerHelper.getAllTimer(mPlugId, 4);

		iShowMode_01 = getMode(mTimer_01);
		iShowMode_02 = getMode(mTimer_02);
		iShowMode_03 = getMode(mTimer_03);
		iShowMode_04 = getMode(mTimer_04);
		iShowMode_05 = getMode(mTimer_05);

		// 使用另外一种方法绘制曲线
		if (i_Current_lushu >= 1) {
			getData(iShowMode_01, mTimer_01, mAxisXValues_01, mPointValues_01);
		}
		if (i_Current_lushu >= 2) {
			getData(iShowMode_02, mTimer_02, mAxisXValues_02, mPointValues_02);
		}
		if (i_Current_lushu >= 3) {
			getData(iShowMode_03, mTimer_03, mAxisXValues_03, mPointValues_03);
		}
		if (i_Current_lushu >= 4) {
			getData(iShowMode_04, mTimer_04, mAxisXValues_04, mPointValues_04);
		}
		if (i_Current_lushu >= 5) {
			getData(iShowMode_05, mTimer_05, mAxisXValues_05, mPointValues_05);
		}

		initLineChart();
	}

	private int getMode(ArrayList<GrowLightTimerCurvePointDefine> mTimer) {
		int iMode = 0;
		if (mTimer != null) {
			if (mTimer.size() > 0)
				iMode = mTimer.get(0).mType; // 0，渐变 1，跳变
		}
		return iMode;
	}
	private void getData(int iShowMode,
			ArrayList<GrowLightTimerCurvePointDefine> mTimer,
			List<AxisValue> xList, List<PointValue> yList) {
		xList.clear();
		yList.clear();

		if (mTimer != null) {
			for (int i = 0; i < mTimer.size(); i++) {
				GrowLightTimerCurvePointDefine ti = mTimer.get(i);
				xList.add((new AxisValue(i).setLabel(ti.mPowerOnTime)));
				if (iShowMode == 1) { // 0: 渐变，1：跳变
					if (i > 0 && i < mTimer.size()) {
						GrowLightTimerCurvePointDefine old_ti = mTimer
								.get(i - 1);
						yList.add(new PointValue(i, old_ti.light));
					}
				}
				yList.add(new PointValue(i, ti.light));
			}
		}
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,
				R.layout.activity_plug_detail_growlight_timercurvepoint, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mSharedPreferences = getSharedPreferences("GROWLIGHT"
				+ PubStatus.g_CurUserName, Activity.MODE_PRIVATE);

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

		loadData();

		init();

		if (PubDefine.g_Connect_Mode == PubDefine.SmartPlug_Connect_Mode.WiFi) {
			mTcpSocketThread = new RevCmdFromSocketThread();
			mTcpSocketThread.start();
		}

		// refreshView();

		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		// queryAllTimeCurvePoint();
		// }
		// }, 1000);
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

	private void loadData() {
		i_Current_lushu = mSharedPreferences.getInt("SETROUTES" + mPlugId, 5);
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
		intent.putExtra("OPERATE", "SELF_ADAPTER");
		// intent.putExtra("OPERATE", "ADD");
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

		// 设置曲线的各种数据
		lineChart = (LineChartView) findViewById(R.id.line_chart);
		refreshView();

		tv_add = (TextView) findViewById(R.id.tv_add);
		tv_del = (TextView) findViewById(R.id.tv_del);
		tv_modify = (TextView) findViewById(R.id.tv_modify);
		tv_clear = (TextView) findViewById(R.id.tv_clear);

		spinner_channel = (Spinner) findViewById(R.id.spinner_channel);

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
			spinner_channel.setSelection(i_Current_channel);
		}

		tv_add.setOnClickListener(this);
		tv_del.setOnClickListener(this);
		tv_modify.setOnClickListener(this);
		tv_clear.setOnClickListener(this);
	}

	private Line getLine_01() {
		Line line = new Line(mPointValues_01).setColor(Color
				.parseColor(mLineColor_01));
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		if (mLineHasLabels == false) {
			line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		}
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);
		line.setPointRadius(mLinePointRadius);
		line.setStrokeWidth(mLineStrokeWidth);

		return line;
	}

	private Line getLine_02() {
		Line line = new Line(mPointValues_02).setColor(Color
				.parseColor(mLineColor_02)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		if (mLineHasLabels == false) {
			line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		}
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);
		line.setPointRadius(mLinePointRadius);
		line.setStrokeWidth(mLineStrokeWidth);

		return line;
	}

	private Line getLine_03() {
		Line line = new Line(mPointValues_03).setColor(Color
				.parseColor(mLineColor_03)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		if (mLineHasLabels == false) {
			line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		}
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);
		line.setPointRadius(mLinePointRadius);
		line.setStrokeWidth(mLineStrokeWidth);

		return line;
	}

	private Line getLine_04() {
		Line line = new Line(mPointValues_04).setColor(Color
				.parseColor(mLineColor_04)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		if (mLineHasLabels == false) {
			line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		}
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);
		line.setPointRadius(mLinePointRadius);
		line.setStrokeWidth(mLineStrokeWidth);

		return line;
	}

	private Line getLine_05() {
		Line line = new Line(mPointValues_05).setColor(Color
				.parseColor(mLineColor_05)); // 折线的颜色（橙色）
		line.setShape(mLineShape);
		line.setCubic(mLineCubic);
		line.setFilled(mLineFilled);
		line.setHasLabels(mLineHasLabels);
		if (mLineHasLabels == false) {
			line.setHasLabelsOnlyForSelected(mLineHasLabelsOnlyForSelected);
		}
		line.setHasLines(mLineHasLines);
		line.setHasPoints(mLineHasPoints);
		line.setPointRadius(mLinePointRadius);
		line.setStrokeWidth(mLineStrokeWidth);

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

		String xLabel = SmartPlugApplication.getInstance().getString(
				R.string.smartplug_growlight_time);
		String yLabel = SmartPlugApplication.getInstance().getString(
				R.string.smartplug_growlight_bright);

		// X坐标轴
		Axis axisX = new Axis(); // X轴
		axisX.setHasTiltedLabels(false); // X坐标轴字体是斜的显示还是直的，true是斜的显示
		axisX.setTextColor(Color.GRAY); // 设置字体颜色
		axisX.setName(xLabel); // 表格名称
		axisX.setTextSize(10);// 设置字体大小
		axisX.setMaxLabelChars(8); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
		axisX.setValues(mAxisXValues_01); // 填充X轴的坐标名称
		data.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		// Y坐标轴: 根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
		Axis axisY = new Axis(); // Y轴
		axisY.setTextColor(Color.GRAY);
		axisY.setName(yLabel);// y轴标注
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
		// 总共 24个点，所以全景就是 0~23
		Viewport v = new Viewport(lineChart.getMaximumViewport());
		v.left = 0;
		v.right = 23;
		lineChart.setCurrentViewport(v);
	}

	class spinnerChannelListener
			implements
				android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			i_Current_channel = position;
			String str_Channel = parent.getItemAtPosition(position).toString();

			refreshView();
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
