package com.thingzdo.processhandler;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.common.PubDefine;
import com.thingzdo.ui.common.PubFunc;
import com.thingzdo.ui.smartplug.AppServerReposeDefine;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.content.Intent;
import android.os.Message;

public class SmartPlugEventHandlerAddPlug extends SmartPlugEventHandler {
	Intent mIntent = new Intent(PubDefine.PLUG_ADD_TASK);
	@Override
	public void handleMessage(Message msg) {
		String[] buffer = (String[]) msg.obj;
		try{
			int ret = PubFunc.hexStringToAlgorism(buffer[EVENT_MESSAGE_HEADER+0]);		
			if (0 == ret) {
		    	mIntent.putExtra("RESULT", 0);
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);
			} else {
				PubFunc.log("add plug fail,ret=" + String.valueOf(ret));
		    	mIntent.putExtra("RESULT", 1);
		    	int resid = AppServerReposeDefine.getServerResponse(ret);
		    	if (0  != resid) {
		    		mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext().getString(resid));
		    	} else {
		    		mIntent.putExtra("MESSAGE", SmartPlugApplication.getContext().getString(R.string.smartplug_add_fail));
		    	}
		    	SmartPlugApplication.getContext().sendBroadcast(mIntent);	    	
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
