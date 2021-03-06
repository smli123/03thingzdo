package com.thingzdo.processhandler;

import android.content.Intent;
import android.os.Message;

import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.smartplug.PubStatus;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

public class SmartPlugEventHandlerNotifyOnline extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_NOTIFY_ONLINE);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
	
		int online = Integer.parseInt(buffer[EVENT_MESSAGE_HEADER+1]);
		mIntent.putExtra("PLUGID", PubStatus.g_moduleId);
		mIntent.putExtra("ONLINE", online);
		SmartPlugApplication.getContext().sendBroadcast(mIntent);
	}
}
