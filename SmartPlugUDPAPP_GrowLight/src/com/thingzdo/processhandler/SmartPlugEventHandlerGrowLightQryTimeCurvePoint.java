package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugGrowLightTimerCurvePointHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.GrowLightTimerCurvePointDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerGrowLightQryTimeCurvePoint
		extends
			SmartPlugEventHandler {
	Intent mIntent = new Intent(
			PubDefine.PLUG_GROWLIGHT_QRY_TIMECURVEPOINT_ACTION);

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] buffer = (String[]) msg.obj;
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);

		if (0 == ret) {
			String moduleID = buffer[3];

			mIntent.putExtra("RESULT", 0);
			mIntent.putExtra("MODULEID", moduleID);

			String value = "";
			for (int i = 5; i < buffer.length; i++) {
				value += buffer[i];
				if (i < buffer.length - 1) {
					value += ",";
				}
			}
			mIntent.putExtra("TIMECURVE", value);

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
										R.string.smartplug_growlight_querytimecurve_fail));
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
		SmartPlugGrowLightTimerCurvePointHelper mTimerHelper = new SmartPlugGrowLightTimerCurvePointHelper(
				SmartPlugApplication.getContext());

		String[] infors = command.split(",");
		if (infors.length > 0) {
			int channel = Integer.parseInt((infors[0]));
			mTimerHelper.clearTimer(moduleID, channel);

			int count = Integer.parseInt((infors[1]));

			int baseIdx = 2;
			int BLOCK_SIZE = 5;
			for (int j = 0; j < count; j++) {
				GrowLightTimerCurvePointDefine ti = new GrowLightTimerCurvePointDefine();

				// ti.mTimerId = Integer.parseInt(infors[baseIdx + j *
				// BLOCK_SIZE
				// + 0]);
				ti.mPlugId = moduleID;
				ti.mType = Integer
						.parseInt(infors[baseIdx + j * BLOCK_SIZE + 0]);
				ti.mPowerOnTime = infors[baseIdx + j * BLOCK_SIZE + 1];
				ti.mPeriod = infors[baseIdx + j * BLOCK_SIZE + 2];
				ti.mEnable = infors[baseIdx + j * BLOCK_SIZE + 3].equals("1")
						? true
						: false;
				ti.light = Integer
						.parseInt(infors[baseIdx + j * BLOCK_SIZE + 4]);
				ti.light_channel = channel;

				mTimerHelper.addTimer(ti);
			}
		}

		mTimerHelper = null;
	}
}