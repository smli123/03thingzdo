package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerGrowLightSetBright
		extends
			SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_GROWLIGHT_SET_BRIGHT_ACTION);

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String[] buffer = (String[]) msg.obj;
		int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);

		if (0 == ret) {
			mIntent.putExtra("RESULT", 0);

			mIntent.putExtra("LIGHT01",
					Integer.parseInt((buffer[EVENT_MESSAGE_HEADER + 2])));
			mIntent.putExtra("LIGHT02",
					Integer.parseInt((buffer[EVENT_MESSAGE_HEADER + 3])));
			mIntent.putExtra("LIGHT03",
					Integer.parseInt((buffer[EVENT_MESSAGE_HEADER + 4])));
			mIntent.putExtra("LIGHT04",
					Integer.parseInt((buffer[EVENT_MESSAGE_HEADER + 5])));
			mIntent.putExtra("LIGHT05",
					Integer.parseInt((buffer[EVENT_MESSAGE_HEADER + 6])));

			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} else {
			mIntent.putExtra("RESULT", 1);
			int resid = AppServerReposeDefine.getServerResponse(ret);
			if (0 == resid) {
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(R.string.smartplug_growlight_setbright_fail));
			} else {
				mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext()
						.getString(resid));
			}
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		}
	}
}
