package com.thingzdo.ui.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.dataprovider.SmartPlugSceneHelper;
import com.thingzdo.processhandler.SmartPlugMessage;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SceneDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.StringUtils;
import com.thingzdo.ui.common.TitledActivity;
import com.thingzdo.ui.control.NotifyProcessor;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SceneManagement_Activity extends TitledActivity
		implements
			OnClickListener {

	private SmartPlugHelper mPlugHelper = null;
	private Context mContext;

	private ListView lv_scene;

	private List<SceneDefine> m_tjSceneData = new ArrayList<SceneDefine>();
	private SceneAdapter sceneAdapter;
	private SmartPlugSceneHelper mSceneHelper = null;
	private int n_max_scene_id = -1;

	private BroadcastReceiver mDetailRev = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_ONLINE)) {
				if (true == NotifyProcessor.onlineNotify(
						SceneManagement_Activity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_NOTIFY_POWER)) {
				if (true == NotifyProcessor.powerNotify(
						SceneManagement_Activity.this, intent)) {
					updateUI();
				}
			}

			if (intent.getAction().equals(PubDefine.PLUG_POWER_ACTION)) {
				// nothing to do;
			}

			if (intent.getAction().equals(PubDefine.PLUG_ADDSCENE_ACTION)) {
				getServerSceneData();
			}
			if (intent.getAction().equals(PubDefine.PLUG_DELSCENE_ACTION)) {
				getServerSceneData();
			}

			if (intent.getAction().equals(PubDefine.PLUG_QUERYSCENE_ACTION)) {
				int result = intent.getIntExtra("RESULT", 0);
				if (result == 0) {
					Bundle bu = intent.getExtras();
					String[] infors = bu.getStringArray("DATA");

					refresh_Scene_Data(infors);
				}
			}
		}
	};

	private void refresh_Scene_Data(String[] cmds) {
		int baseIdx = 5;
		int count = Integer.parseInt(cmds[baseIdx]);
		baseIdx++;

		m_tjSceneData.clear();
		n_max_scene_id = -1;
		for (int i = baseIdx; i < cmds.length;) {
			SceneDefine item = new SceneDefine();
			item.mSceneId = Integer.valueOf(cmds[i++]);
			item.mSceneName = cmds[i++];

			item.mPowerEnable = Integer.valueOf(cmds[i++]);
			item.mPowerModuleID = cmds[i++];
			item.mPowerStatus = Integer.valueOf(cmds[i++]);

			item.mCurtainEnable = Integer.valueOf(cmds[i++]);
			item.mCurtainModuleID = cmds[i++];
			item.mCurtainStatus = Integer.valueOf(cmds[i++]);

			item.mAirConEnable = Integer.valueOf(cmds[i++]);
			item.mAirConModuleID = cmds[i++];
			item.mAirConTemperature = Integer.valueOf(cmds[i++]);
			item.mAirConStatus = Integer.valueOf(cmds[i++]);

			item.mPCEnable = Integer.valueOf(cmds[i++]);
			item.mPCModuleID = cmds[i++];
			item.mPCMacModuleID = cmds[i++];
			item.mPCMacAddress = cmds[i++];
			item.mPCStatus = Integer.valueOf(cmds[i++]);

			if (n_max_scene_id < item.mSceneId) {
				n_max_scene_id = item.mSceneId;
			}

			m_tjSceneData.add(item);
		}
		sceneAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_scenemanagement,
				false);
		SmartPlugApplication.resetTask();
		SmartPlugApplication.getInstance().addActivity(this);

		mContext = this;

		setTitleRightButton(R.string.add, R.drawable.title_btn_selector, this);
		setTitleLeftButton(R.string.smartplug_goback,
				R.drawable.title_btn_selector, this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PubDefine.PLUG_POWER_ACTION);
		filter.addAction(PubDefine.PLUG_NOTIFY_ONLINE);
		filter.addAction(PubDefine.PLUG_NOTIFY_POWER);
		filter.addAction(PubDefine.PLUG_QUERYSCENE_ACTION);
		filter.addAction(PubDefine.PLUG_ADDSCENE_ACTION);
		filter.addAction(PubDefine.PLUG_DELSCENE_ACTION);
		registerReceiver(mDetailRev, filter);

		mPlugHelper = new SmartPlugHelper(this);

		init();

		// 使用定时器来 更新 时间
		new Timer("GetSceneName").schedule(new TimerTask() {
			@Override
			public void run() {
				getServerSceneData();
			}
		}, 1000, 600000);
	}

	private void getServerSceneData() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_QUERYSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.g_CurUserName)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0");

		sendMsg(true, sb.toString(), true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SmartPlugApplication.resetTask();
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mDetailRev);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titlebar_leftbutton :
				finish();
				break;
			case R.id.titlebar_rightbutton :
				Button btn_TitleRight = (Button) findViewById(R.id.titlebar_rightbutton);
				// Internet模式：“增加”界面
				if (btn_TitleRight.getText().equals(getString(R.string.add))) {
					Intent intent = new Intent();
					intent.putExtra("ACTION", "ADD");
					intent.putExtra("MAXSCENEID", n_max_scene_id + 1);
					intent.setClass(SceneManagement_Activity.this,
							Scene_Activity.class);
					startActivity(intent);
				} else {
					// do nothing...
				}
				break;
			default :
				break;
		}
	}
	private void init() {
		lv_scene = (ListView) findViewById(R.id.lv_scene);

		sceneAdapter = new SceneAdapter(this);
		lv_scene.setAdapter(sceneAdapter);

		lv_scene.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				Intent intent = new Intent();
				intent.setClass(SceneManagement_Activity.this,
						Scene_Activity.class);

				intent.putExtra("SCENEDEFINE", m_tjSceneData.get(pos));
				intent.putExtra("ACTION", "MODIFY");

				SceneManagement_Activity.this.startActivity(intent);
			}
		});
	}
	private void updateUI() {

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) { // Apply Scene
				int pos = msg.arg1;
				applyScene(pos);
			}
		}
	};

	private void applyScene(int pos) {
		SceneDefine item = m_tjSceneData.get(pos);

		StringBuffer sb = new StringBuffer();
		sb.append(SmartPlugMessage.CMD_SP_APPLYSCENE)
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(PubStatus.getUserName())
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL).append("0")
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf("0"))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(item.mSceneId))
				.append(StringUtils.PACKAGE_RET_SPLIT_SYMBOL)
				.append(String.valueOf(item.mSceneName));

		sendMsg(true, sb.toString(), true);

	}
	public class SceneAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SceneAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return m_tjSceneData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder_listitem holder = null;
			if (convertView == null) {
				holder = new ViewHolder_listitem();

				convertView = mInflater.inflate(R.layout.item_scene_list, null);

				holder.tv_item_no = (TextView) convertView
						.findViewById(R.id.tv_item_no);
				holder.tv_scene_name = (TextView) convertView
						.findViewById(R.id.tv_scene_name);

				holder.btn_apply_scene = (Button) convertView
						.findViewById(R.id.btn_apply_scene);
				holder.btn_apply_scene
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Message msg = new Message();
								msg.what = 1;
								msg.arg1 = Integer.valueOf(v
										.getContentDescription().toString());
								mHandler.sendMessage(msg);
							}
						});

				holder.tv_scene_power = (TextView) convertView
						.findViewById(R.id.tv_scene_power);
				holder.tv_scene_curtain = (TextView) convertView
						.findViewById(R.id.tv_scene_curtain);
				holder.tv_scene_aircon = (TextView) convertView
						.findViewById(R.id.tv_scene_aircon);
				holder.tv_scene_pc = (TextView) convertView
						.findViewById(R.id.tv_scene_pc);

				holder.img_scene_power = (ImageView) convertView
						.findViewById(R.id.img_scene_power);
				holder.img_scene_curtain = (ImageView) convertView
						.findViewById(R.id.img_scene_curtain);
				holder.img_scene_aircon = (ImageView) convertView
						.findViewById(R.id.img_scene_aircon);
				holder.img_scene_pc = (ImageView) convertView
						.findViewById(R.id.img_scene_pc);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder_listitem) convertView.getTag();
			}

			if (holder != null && m_tjSceneData != null
					&& position < m_tjSceneData.size()) {
				Map<String, Object> data = null;
				convertView.setBackgroundColor(Color.TRANSPARENT);
				holder.ViewData(data, position);
			}

			return convertView;
		}
	}

	// ------------------------------------------------------
	public final class ViewHolder_listitem {
		public TextView tv_item_no;
		public TextView tv_scene_name;
		public TextView tv_scene_power;
		public TextView tv_scene_curtain;
		public TextView tv_scene_aircon;
		public TextView tv_scene_pc;

		public Button btn_apply_scene;

		public ImageView img_scene_power;
		public ImageView img_scene_curtain;
		public ImageView img_scene_aircon;
		public ImageView img_scene_pc;

		public void ViewData(Map<String, Object> data, final int position) {
			SceneDefine item = m_tjSceneData.get(position);

			tv_item_no.setText(String.valueOf(position + 1));
			tv_scene_name.setText(item.mSceneName);

			btn_apply_scene.setContentDescription(String.valueOf(position));

			// tv_scene_power.setTextColor(item.mPowerEnable == 0
			// ? R.color.red
			// : R.color.blue);
			// tv_scene_curtain.setTextColor(item.mCurtainEnable == 0
			// ? R.color.red
			// : R.color.blue);
			// tv_scene_aircon.setTextColor(item.mAirConEnable == 0
			// ? R.color.red
			// : R.color.blue);
			// tv_scene_pc.setTextColor(item.mPCEnable == 0
			// ? R.color.red
			// : R.color.blue);

			tv_scene_power.setTextColor(item.mPowerEnable == 0 ? Color
					.parseColor("#929292") : Color.parseColor("#000000"));
			tv_scene_curtain.setTextColor(item.mCurtainEnable == 0 ? Color
					.parseColor("#929292") : Color.parseColor("#000000"));
			tv_scene_aircon.setTextColor(item.mAirConEnable == 0 ? Color
					.parseColor("#929292") : Color.parseColor("#000000"));
			tv_scene_pc.setTextColor(item.mPCEnable == 0 ? Color
					.parseColor("#929292") : Color.parseColor("#000000"));

			img_scene_power.setBackgroundResource(item.mPowerStatus == 0
					? R.drawable.smp_switcher_close
					: R.drawable.smp_switcher_open);

			img_scene_curtain.setBackgroundResource(item.mCurtainStatus == 0
					? R.drawable.smp_switcher_close
					: R.drawable.smp_switcher_open);

			img_scene_aircon.setBackgroundResource(item.mAirConStatus == 0
					? R.drawable.smp_switcher_close
					: R.drawable.smp_switcher_open);

			img_scene_pc.setBackgroundResource(item.mPCStatus == 0
					? R.drawable.smp_switcher_close
					: R.drawable.smp_switcher_open);
		}
	}
}
