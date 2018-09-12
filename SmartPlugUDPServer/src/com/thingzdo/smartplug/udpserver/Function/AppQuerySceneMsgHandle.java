package com.thingzdo.smartplug.udpserver.Function;

import java.util.Vector;

import com.thingzdo.platform.PWDTool.PWDManagerTool;
import com.thingzdo.smartplug.udpserver.commdef.ICallFunction;
import com.thingzdo.smartplug.udpserver.commdef.ServerCommDefine;
import com.thingzdo.smartplug.udpserver.commdef.ServerRetCodeMgr;
import com.thingzdo.smartplug.udpserver.db.MODULE_IRSCENE;
import com.thingzdo.smartplug.udpserver.db.MODULE_SCENE;
import com.thingzdo.smartplug.udpserver.db.ServerDBMgr;
import com.thingzdo.smartplug.udpserver.db.USER_INFO;
import com.thingzdo.smartplug.udpserver.db.USER_MODULE;

public class AppQuerySceneMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name UpdatePWDHandle 修改用户密码
	 * @param 	strMsg: 命令字符串 格式：<cookie>,APPQUERYSCENE,<username>,<moduleid>#
	 * @RET 		<new_cookie>,APPQUERYSCENE,<username>,<0>,<code>
	 *                  其中return code: 0表示成功，其它：错误码
	 * @return  boolean 是否成功
	 * @author zxluan
	 * @date 2015/04/07
	 * **********************************************************************************************************/
	public int call(Runnable thread_base, String strMsg) 
	{
		String strRet[] 	= strMsg.split(ServerCommDefine.CMD_SPLIT_STRING);
		String strCookie	= strRet[0].trim();
		String strCmd		= strRet[1].trim();
		String strUserName	= strRet[2].trim();
		String strModuleID 	= strRet[3].trim();
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strCmd, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, iRet);
			return iRet;
		}
		
		ServerDBMgr dbMgr = new ServerDBMgr();
		
		try
		{
			Vector<MODULE_SCENE> infos = dbMgr.QuerySceneInfoList(strUserName);
			String strContent = String.valueOf(infos.size()) + ",";
			int i = 0;
			for(MODULE_SCENE item : infos) {
				strContent += String.format("%d,%s,%d,%s,%d,%d,%s,%d,%d,%s,%d,%d,%d,%s,%s,%s,%d", 
						item.getScene_id(), item.getScene_name(), 
						item.getPower_enable(), item.getPower_moduleid(), item.getPower_control(),
						item.getCurtain_enable(),item.getCurtain_moduleid(),item.getCurtain_control(),
						item.getAircon_enable(),item.getAircon_moduleid(),item.getAircon_temperature(),item.getAircon_control(),
						item.getPc_enable(),item.getPc_moduleid(),item.getPc_mac_moduleid(),item.getPc_mac_address(),item.getPc_control()
						);
				i++;
				if (i < infos.size())
					strContent += ",";
			}
				
			//更新成功
			ResponseToAPP(strCmd, strUserName, ServerRetCodeMgr.SUCCESS_CODE, strContent);
			
			return ServerRetCodeMgr.SUCCESS_CODE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServerRetCodeMgr.ERROR_COMMON;
		} finally {
			dbMgr.Destroy();
		}
	}

	@Override
	public int resp(Runnable thread_base, String strMsg) {
		// TODO Auto-generated method stub
		return 0;
	}
}
