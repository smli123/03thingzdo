package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerGrowLightQrySunTime
		extends
			SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_QUERYSCENE_ACTION);

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] buffer = (String[]) msg.obj;
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);

		if (0 == ret) {
			mIntent.putExtra("RESULT", 0);

			mIntent.putExtra("SUNUP", buffer[EVENT_MESSAGE_HEADER + 1]);
			mIntent.putExtra("SUNDOWN", buffer[EVENT_MESSAGE_HEADER + 2]);

			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} else {
			mIntent.putExtra("RESULT", 1);
			int resid = AppServerReposeDefine.getServerResponse(ret);
			if (0 == resid) {
				mIntent.putExtra(
						"MESSAGE",
						SmartPlugApplication.getContext().getString(
								R.string.smartplug_growlight_querysuntime_fail));
			} else {
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(resid));
			}
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		}
	}
}
