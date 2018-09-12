package com.thingzdo.ui.manage;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SceneDefine;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.NotifyProcessor;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;
import com.thingzdo.ui.wheelutils.ActionSheetDialog;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.OnSheetItemClickListener;
import com.thingzdo.ui.wheelutils.ActionSheetDialog.SheetItemColor;

public class Scene_Activity extends TitledActivity implements OnClickListener {
	private String mErrorMsg = "";

	private EditText et_scene_name;
	private ImageView iv_power_switch;
	private ImageView iv_curtain_switch;
	private ImageView iv_aircon_switch;
	private ImageView iv_pc_switch;

	private TextView tv_power_moduleids;
	private TextView tv_curtain_moduleids;
	private TextView tv_aircon_moduleids;
	private TextView tv_pc_moduleids;

	private ImageView iv_power_control;
	private ImageView iv_curtain_control;
	private ImageView iv_aircon_control;
	private ImageView iv_pc_control;

	private Button btn_deletescene;

	private TextView tv_pc_mac_moduleids;

	private int n_max_scene_id = 0;
	private int n_scene_id = -1;
	private String str_scene_name = "";
	private int n_power_switch = 0;
	private int n_curtain_switch = 0;
	private int n_aircon_switch = 0;
	private int n_pc_switch = 0;

	private int n_power_control = 0;
	private int n_curtain_control = 0;
	private int n_aircon_control = 0;
	private int n_pc_control = 0;

	private int n_aircon_temperature = 17;

	private String str_power_moduleids = "";
	private String str_curtain_moduleids = "";
	private String str_aircon_moduleids = "";
	private String str_pc_moduleids = "";
	private String str_pc_mac_moduleids = "";
	private String str_pc_mac_address = "";

	private SmartPlugHelper mPlugHelper = null;
	private ArrayList<SmartPlugDefine> AllSmartPlug = new ArrayList<SmartPlugDefine>();
	private boolean create_mode = false;

	// 用于模块List
	private List<String> list_PowerIDs = new ArrayList<String>();
	private List<String> list_PowerIDs_show = new ArrayList<String>();

	private List<String> list_CurtainIDs = new ArrayList<String>();
	private List<String> list_CurtainIDs_show = new ArrayList<String>();

	private List<String> list_AirconIDs = new ArrayList<String>();
	private List<String> list_AirconIDs_show = new ArrayList<String>();

	private List<String> list_PCIDs = new ArrayList<String>();
	private List<String> list_PCIDs_show = new ArrayList<String>();

	private List<String> list_MacIDs = new ArrayList<String>();
	private List<String> list_MacIDs_show = new ArrayList<String>();

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(Scene_Activity.this,
						intent)) {
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(Scene_Activity.this,
						intent)) {
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_scene, false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);
		setTitleRightButton(R.string.smartplug_confirm,
				R.drawable.title_btn_selector, this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);

		Intent intent = getIntent();
		String action = intent.getStringExtra("ACTION");
		if (action.equals("MODIFY") == true) {
			create_mode = false;
			SceneDefine item = (SceneDefine) intent
					.getSerializableExtra("SCENEDEFINE");

			n_scene_id = item.mSceneId;
			str_scene_name = item.mSceneName;

			n_power_switch = item.mPowerEnable;
			n_curtain_switch = item.mCurtainEnable;
			n_aircon_switch = item.mAirConEnable;
			n_pc_switch = item.mPCEnable;

			str_power_moduleids = item.mPowerModuleID;
			str_curtain_moduleids = item.mCurtainModuleID;
			str_aircon_moduleids = item.mAirConModuleID;
			str_pc_moduleids = item.mPCModuleID;
			str_pc_mac_moduleids = item.mPCMacModuleID;
			str_pc_mac_address = item.mPCMacAddress;

			n_power_control = item.mPowerStatus;
			n_curtain_control = item.mCurtainStatus;
			n_aircon_control = item.mAirConStatus;
			n_pc_control = item.mPCStatus;

		} else if (action.equals("ADD") == true) {
			// Use Default Value
			create_mode = true;
			n_max_scene_id = intent.getIntExtra("MAXSCENEID", 0);
		}

		AllSmartPlug.clear();
		AllSmartPlug = mPlugHelper.getAllSmartPlugEx(PubStatus.g_CurUserName);
		for (int i = 0; i < AllSmartPlug.size(); i++) {
			SmartPlugDefine mPlug = AllSmartPlug.get(i);
			if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
					&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) { // 1_2
																						// SmartPlug
				list_PowerIDs.add(mPlug.mPlugId);
				list_PowerIDs_show.add(mPlug.mPlugName + " " + mPlug.mPlugId);

				list_AirconIDs.add(mPlug.mPlugId);
				list_AirconIDs_show.add(mPlug.mPlugName + " " + mPlug.mPlugId);

				list_PCIDs.add(mPlug.mPlugId);
				list_PCIDs_show.add(mPlug.mPlugName + " " + mPlug.mPlugId);

			} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_CURTAIN) { // 3_1
																					// Curtain
				list_CurtainIDs.add(mPlug.mPlugId);
				list_CurtainIDs_show.add(mPlug.mPlugName + " " + mPlug.mPlugId);

			} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SWITCH
					&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 5_1
																			// SmartLight

			} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SWITCH
					&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_AIRCON) { // 5_2
																						// SmartLightEx

			} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_PLUG
					&& mPlug.mSubProductType == PubDefine.PRODUCT_SMART_PLUG_ENERGE) { // 1_3
																						// Energe

			} else if (mPlug.mSubDeviceType == PubDefine.DEVICE_SMART_SIMULATION_PC
					&& mPlug.mSubProductType == PubDefine.PRODUCT_PLUG) { // 7_1
																			// SimulatorPC
				list_MacIDs.add(mPlug.mPlugId);
				list_MacIDs_show.add(mPlug.mPlugName + " " + mPlug.mPlugId);

			}

			init();
			refresh_UI(true);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
		// refresh_UI();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mDetailRev);
	}

	private void init() {
		et_scene_name = (EditText) findViewById(R.id.et_scene_name);
		iv_power_switch = (ImageView) findViewById(R.id.iv_power_switch);
		iv_curtain_switch = (ImageView) findViewById(R.id.iv_curtain_switch);
		iv_aircon_switch = (ImageView) findViewById(R.id.iv_aircon_switch);
		iv_pc_switch = (ImageView) findViewById(R.id.iv_pc_switch);

		tv_power_moduleids = (TextView) findViewById(R.id.tv_power_moduleids);
		tv_curtain_moduleids = (TextView) findViewById(R.id.tv_curtain_moduleids);
		tv_aircon_moduleids = (TextView) findViewById(R.id.tv_aircon_moduleids);
		tv_pc_moduleids = (TextView) findViewById(R.id.tv_pc_moduleids);

		iv_power_control = (ImageView) findViewById(R.id.iv_power_control);
		iv_curtain_control = (ImageView) findViewById(R.id.iv_curtain_control);
		iv_aircon_control = (ImageView) findViewById(R.id.iv_aircon_control);
		iv_pc_control = (ImageView) findViewById(R.id.iv_pc_control);

		tv_pc_mac_moduleids = (TextView) findViewById(R.id.tv_pc_mac_moduleids);

		btn_deletescene = (Button) findViewById(R.id.btn_deletescene);
		if (create_mode == true) {
			btn_deletescene.setVisibility(View.GONE);
		} else {
			btn_deletescene.setVisibility(View.VISIBLE);
		}

		iv_power_switch.setOnClickListener(this);
		iv_curtain_switch.setOnClickListener(this);
		iv_aircon_switch.setOnClickListener(this);
		iv_pc_switch.setOnClickListener(this);

		tv_power_moduleids.setOnClickListener(this);
		tv_curtain_moduleids.setOnClickListener(this);
		tv_aircon_moduleids.setOnClickListener(this);
		tv_pc_moduleids.setOnClickListener(this);

		iv_power_control.setOnClickListener(this);
		iv_curtain_control.setOnClickListener(this);
		iv_aircon_control.setOnClickListener(this);
		iv_pc_control.setOnClickListener(this);

		tv_pc_mac_moduleids.setOnClickListener(this);
		btn_deletescene.setOnClickListener(this);
	}
	private void refresh_UI(boolean first) {
		if (first == true) {
			et_scene_name.setText(str_scene_name);
		}

		iv_power_switch.setImageResource(n_power_switch == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_curtain_switch.setImageResource(n_curtain_switch == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_aircon_switch.setImageResource(n_aircon_switch == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_pc_switch.setImageResource(n_pc_switch == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);

		iv_power_control.setImageResource(n_power_control == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_curtain_control.setImageResource(n_curtain_control == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_aircon_control.setImageResource(n_aircon_control == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);
		iv_pc_control.setImageResource(n_pc_control == 0
				? R.drawable.smp_switcher_close
				: R.drawable.smp_switcher_open);

		int loc = list_PowerIDs.indexOf(str_power_moduleids);
		if (loc == -1) {
			tv_power_moduleids.setText("未设置");
		} else {
			tv_power_moduleids.setText(list_PowerIDs_show.get(loc));
		}

		loc = list_CurtainIDs.indexOf(str_curtain_moduleids);
		if (loc == -1) {
			tv_curtain_moduleids.setText("未设置");
		} else {
			tv_curtain_moduleids.setText(list_CurtainIDs_show.get(loc));
		}

		loc = list_AirconIDs.indexOf(str_aircon_moduleids);
		if (loc == -1) {
			tv_aircon_moduleids.setText("未设置");
		} else {
			tv_aircon_moduleids.setText(list_AirconIDs_show.get(loc));
		}

		loc = list_PCIDs.indexOf(str_pc_moduleids);
		if (loc == -1) {
			tv_pc_moduleids.setText("未设置");
		} else {
			tv_pc_moduleids.setText(list_PCIDs_show.get(loc));
		}

		loc = list_MacIDs.indexOf(str_pc_mac_moduleids);
		if (loc == -1) {
			tv_pc_mac_moduleids.setText("未设置");
		} else {
			tv_pc_mac_moduleids.setText(list_MacIDs_show.get(loc));
		}

	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.titlebar_leftbutton : // WiFi模式 退出时，需要close掉 TCP连接
				finish();
				break;
			case R.id.titlebar_rightbutton :
				// Add new Scene to Server
				addScene();
				finish();
				break;
			case R.id.iv_power_switch :
				n_power_switch = (n_power_switch == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_curtain_switch :
				n_curtain_switch = (n_curtain_switch == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_aircon_switch :
				n_aircon_switch = (n_aircon_switch == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_pc_switch :
				n_pc_switch = (n_pc_switch == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_power_control :
				n_power_control = (n_power_control == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_curtain_control :
				n_curtain_control = (n_curtain_control == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_aircon_control :
				n_aircon_control = (n_aircon_control == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			case R.id.iv_pc_control :
				n_pc_control = (n_pc_control == 0) ? 1 : 0;
				refresh_UI(false);
				break;
			// 模块选择器
			case R.id.tv_power_moduleids :
				showPowerModuleIDs();
				break;
			case R.id.tv_curtain_moduleids :
				showCurtainModuleIDs();
				break;
			case R.id.tv_aircon_moduleids :
				showAirconModuleIDs();
				break;
			case R.id.tv_pc_moduleids :
				showPCModuleIDs();
				break;
			case R.id.tv_pc_mac_moduleids :
				showPCMacModuleIDs();
				break;
			case R.id.btn_deletescene :
				delScene();
				finish();
				break;
			default :
				break;
		}
	}

	private void addScene() {
		str_scene_name = et_scene_name.getText().toString();

		if (create_mode == true) { // Add
			mErrorMsg = getString(R.string.smartplug_oper_create_scene_fail);
			// 设置 n_scene_id 为最大值
			n_scene_id = n_max_scene_id;
		} else { // Modify
			mErrorMsg = getString(R.string.smartplug_oper_modify_scene_fail);
		}

		mProgress = PubFunc.createProgressDialog(Scene_Activity.this,
				this.getString(R.string.str_reset_module), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_ADDSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0")

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_scene_id))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(str_scene_name))

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_power_switch))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_power_moduleids)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_power_control))

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_curtain_switch))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_curtain_moduleids)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_curtain_control))

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_aircon_switch))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_aircon_moduleids)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_aircon_temperature))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_aircon_control))

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_pc_switch))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_pc_moduleids)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(str_pc_mac_moduleids)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_pc_control));

		sendMsg(true, sb.toString(), true);

	}

	private void delScene() {
		mErrorMsg = getString(R.string.smartplug_oper_modify_scene_fail);

		mProgress = PubFunc.createProgressDialog(Scene_Activity.this,
				this.getString(R.string.str_reset_module), false);
		mProgress.show();

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_DELSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0")

				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(n_scene_id));

		sendMsg(true, sb.toString(), true);

	}
	/*
	 * 显示电源模块
	 */
	private void showPowerModuleIDs() {
		ActionSheetDialog dlg = new ActionSheetDialog(Scene_Activity.this)
				.builder()
				.setTitle(
						Scene_Activity.this
								.getString(R.string.smartplug_oper_selectmodule_title))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		for (int i = 0; i < list_PowerIDs_show.size(); i++) {
			dlg.addSheetItem(list_PowerIDs_show.get(i).toString(),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 0;
							msg.arg1 = which - 1;
							updateHandler.sendMessage(msg);
						}
					});
		}
		dlg.show();
	}
	/*
	 * 显示窗帘模块
	 */
	private void showCurtainModuleIDs() {
		ActionSheetDialog dlg = new ActionSheetDialog(Scene_Activity.this)
				.builder()
				.setTitle(
						Scene_Activity.this
								.getString(R.string.smartplug_oper_selectmodule_title))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		for (int i = 0; i < list_CurtainIDs_show.size(); i++) {
			dlg.addSheetItem(list_CurtainIDs_show.get(i).toString(),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 1;
							msg.arg1 = which - 1;
							updateHandler.sendMessage(msg);
						}
					});
		}
		dlg.show();
	}
	/*
	 * 显示空调模块
	 */
	private void showAirconModuleIDs() {
		ActionSheetDialog dlg = new ActionSheetDialog(Scene_Activity.this)
				.builder()
				.setTitle(
						Scene_Activity.this
								.getString(R.string.smartplug_oper_selectmodule_title))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		for (int i = 0; i < list_AirconIDs_show.size(); i++) {
			dlg.addSheetItem(list_AirconIDs_show.get(i).toString(),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 2;
							msg.arg1 = which - 1;
							updateHandler.sendMessage(msg);
						}
					});
		}
		dlg.show();
	}

	/*
	 * 显示电脑模块
	 */
	private void showPCModuleIDs() {
		ActionSheetDialog dlg = new ActionSheetDialog(Scene_Activity.this)
				.builder()
				.setTitle(
						Scene_Activity.this
								.getString(R.string.smartplug_oper_selectmodule_title))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		for (int i = 0; i < list_PCIDs_show.size(); i++) {
			dlg.addSheetItem(list_PCIDs_show.get(i).toString(),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 3;
							msg.arg1 = which - 1;
							updateHandler.sendMessage(msg);
						}
					});
		}
		dlg.show();
	}
	/*
	 * 显示电脑模块
	 */
	private void showPCMacModuleIDs() {
		if (str_pc_moduleids.isEmpty() == true) {
			Toast.makeText(Scene_Activity.this, "首先设置电脑模块", Toast.LENGTH_SHORT)
					.show();

			return;
		}

		ActionSheetDialog dlg = new ActionSheetDialog(Scene_Activity.this)
				.builder()
				.setTitle(
						Scene_Activity.this
								.getString(R.string.smartplug_oper_selectmodule_title))
				.setCancelable(true).setCanceledOnTouchOutside(true);

		list_MacIDs.clear();
		list_MacIDs_show.clear();
		for (int i = 0; i < AllSmartPlug.size(); i++) {
			SmartPlugDefine item = AllSmartPlug.get(i);
			if (item.mPlugId.contains(str_pc_moduleids)
					&& !item.mPlugId.equals(str_pc_moduleids)) {
				list_MacIDs.add(item.mPlugId);
				list_MacIDs_show.add(item.mPlugName + " " + item.mPlugId);
			}
		}

		for (int i = 0; i < list_MacIDs_show.size(); i++) {
			dlg.addSheetItem(list_MacIDs_show.get(i).toString(),
					SheetItemColor.Blue, new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Message msg = new Message();
							msg.what = 4;
							msg.arg1 = which - 1;
							updateHandler.sendMessage(msg);
						}
					});
		}

		dlg.show();
	}
	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			int index = msg.arg1;

			if (0 == msg.what) { // Power
				str_power_moduleids = list_PowerIDs.get(index);
				tv_power_moduleids.setText(list_PowerIDs_show.get(index));

			} else if (1 == msg.what) { // Cutain
				str_curtain_moduleids = list_CurtainIDs.get(index);
				tv_curtain_moduleids.setText(list_CurtainIDs_show.get(index));

			} else if (2 == msg.what) { // Aircon
				str_aircon_moduleids = list_AirconIDs.get(index);
				tv_aircon_moduleids.setText(list_AirconIDs_show.get(index));

			} else if (3 == msg.what) { // PC
				str_pc_moduleids = list_PCIDs.get(index);
				tv_pc_moduleids.setText(list_PCIDs_show.get(index));

			} else if (4 == msg.what) { // Mac
				str_pc_mac_moduleids = list_MacIDs.get(index);
				tv_pc_mac_moduleids.setText(list_MacIDs_show.get(index));

			}
		};
	};
}
