package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugGrowLightTimerHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.GrowLightTimerDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerGrowLightQryTimeTask
		extends
			SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_GROWLIGHT_QRY_TIMETASK_ACTION);

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] buffer = (String[]) msg.obj;
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);

		if (0 == ret) {
			mIntent.putExtra("RESULT", 0);
			String moduleID = buffer[3];

			String value = "";
			for (int i = 5; i < buffer.length; i++) {
				value += buffer[i];
				if (i < buffer.length - 1) {
					value += ",";
				}
			}
			mIntent.putExtra("TIMETASK", value);

			parseQryResult(moduleID, value);

			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} else {
			mIntent.putExtra("RESULT", 1);
			int resid = AppServerReposeDefine.getServerResponse(ret);
			if (0 == resid) {
				mIntent.putExtra(
						"MESSAGE",
						SmartPlugApplication
								.getContext()
								.getString(
										R.string.smartplug_growlight_querytimetask_fail));
			} else {
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(resid));
			}
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		}
	}

	private void parseQryResult(String moduleID, String command) {
		// Count, NO, Type, light01,
		// light02,light03,light04,light05,time,peroid,eanble
		SmartPlugGrowLightTimerHelper mTimerHelper = new SmartPlugGrowLightTimerHelper(
				SmartPlugApplication.getContext());

		mTimerHelper.clearTimer(moduleID);

		String[] infors = command.split(",#");
		if (infors.length > 0) {
			int count = Integer.parseInt(infors[0]);

			int baseIdx = 1;
			int BLOCK_SIZE = 10;
			for (int j = 0; j < count; j++) {
				GrowLightTimerDefine ti = new GrowLightTimerDefine();

				ti.mTimerId = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 0]);
				ti.mPlugId = moduleID;
				ti.mType = Integer
						.parseInt(infors[baseIdx + j * BLOCK_SIZE + 1]);
				ti.light01 = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 2]);
				ti.light02 = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 3]);
				ti.light03 = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 4]);
				ti.light04 = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 5]);
				ti.light05 = Integer.parseInt(infors[baseIdx + j * BLOCK_SIZE
						+ 6]);
				ti.mPowerOnTime = infors[baseIdx + j * BLOCK_SIZE + 7];
				ti.mPeriod = infors[baseIdx + j * BLOCK_SIZE + 8];
				ti.mPowerOffTime = infors[baseIdx + j * BLOCK_SIZE + 7];

				ti.mEnable = infors[baseIdx + j * BLOCK_SIZE + 9].equals("1")
						? true
						: false;

				mTimerHelper.addTimer(ti);
			}
		}
	}
}
