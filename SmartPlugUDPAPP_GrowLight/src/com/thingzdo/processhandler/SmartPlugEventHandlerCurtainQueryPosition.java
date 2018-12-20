package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.dataprovider.SmartPlugHelper;
import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.SmartPlugDefine;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerCurtainQueryPosition
		extends
			SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_CURTAIN_QUERYPOSITION_ACTION);
	@Override
	public void handleMessage(Message msg) {

		mIntent.putExtra("PLUGID", PubStatus.g_moduleId);

		String[] buffer = (String[]) msg.obj;
		try {
			String plugID = buffer[3];
			mIntent.putExtra("PLUGID", plugID);

			int code = PubFunc
					.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER + 0]);
			if (2 > buffer.length) {
				mIntent.putExtra("RESULT", code);
				mIntent.putExtra(
						"MESSAGE",
						SmartPlugApplication
								.getContext()
								.getString(
										R.string.smartplug_oper_curtain_queryposition_fail));
				SmartPlugApplication.getContext().sendBroadcast(mIntent);
				return;
			}

			int pos = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER + 1]);

			if (0 == code) {
				// success
				mIntent.putExtra("RESULT", 0);
				mIntent.putExtra("POSITION", pos);

				SmartPlugHelper mPlugHelper = new SmartPlugHelper(
						SmartPlugApplication.getContext());
				SmartPlugDefine plug = mPlugHelper.getSmartPlug(plugID);
				if (plug != null) {
					plug.mPosition = pos;
				}
				mPlugHelper.modifySmartPlug(plug);

			} else {
				// fail
				mIntent.putExtra("RESULT", code);
				mIntent.putExtra("POSITION", pos);
				mIntent.putExtra(
						"MESSAGE",
						SmartPlugApplication
								.getContext()
								.getString(
										R.string.smartplug_oper_curtain_queryposition_fail));
			}
			SmartPlugApplication.getContext().sendBroadcast(mIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
