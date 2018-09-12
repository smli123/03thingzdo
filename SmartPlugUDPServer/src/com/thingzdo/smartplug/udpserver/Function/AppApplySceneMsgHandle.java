package com.thingzdo.smartplug.udpserver.Function;

import com.thingzdo.platform.LogTool.LogWriter;
import com.thingzdo.platform.PWDTool.PWDManagerTool;
import com.thingzdo.smartplug.udpserver.ServerWorkThread;
import com.thingzdo.smartplug.udpserver.commdef.ICallFunction;
import com.thingzdo.smartplug.udpserver.commdef.ServerCommDefine;
import com.thingzdo.smartplug.udpserver.commdef.ServerRetCodeMgr;
import com.thingzdo.smartplug.udpserver.db.MODULE_INFO;
import com.thingzdo.smartplug.udpserver.db.MODULE_IRSCENE;
import com.thingzdo.smartplug.udpserver.db.MODULE_SCENE;
import com.thingzdo.smartplug.udpserver.db.ServerDBMgr;
import com.thingzdo.smartplug.udpserver.db.USER_INFO;
import com.thingzdo.smartplug.udpserver.db.USER_MODULE;

public class AppApplySceneMsgHandle implements ICallFunction{
	/**********************************************************************************************************
	 * @name UpdatePWDHandle 修改用户密码
	 * @param 	strMsg: 命令字符串 格式：<cookie>,MODPWD,<username>,<oldpwd>,<newpwd>
	 * @RET 		<new_cookie>,MODPWD,<username>,<0>,<code>
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
		int type			= Integer.valueOf(strRet[4].trim());
		int scene_id		= Integer.valueOf(strRet[5].trim());
		String scene_name 	= strRet[6].trim();
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strCmd, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, iRet);
			return iRet;
		}
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		ServerDBMgr dbMgr = new ServerDBMgr();
		try  {
			MODULE_SCENE info = null;
			if (type == 0) {
				info = dbMgr.QuerySceneInfo(strUserName, scene_id);
			} else if (type ==1) {
				info = dbMgr.QuerySceneInfo(strUserName, scene_name);
			}
			
			if (info == null) {
				LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) SceneId == null(%s) Apply Fail. ", 
						thread.getSrcIP(),thread.getSrcPort(),strUserName,scene_id));
				return ServerRetCodeMgr.ERROR_COMMON;
			} else {
				String module_command = "";
				String module_id = "";
				try {
				// 电源
				if (info.getPower_enable() == 1 && !info.getPower_moduleid().isEmpty()) {
					module_id = info.getPower_moduleid();
					module_command = "0,APPPOWER," + strUserName + "," + module_id + "," + String.valueOf(info.getPower_control());
					iRet = NotifyToModule(module_command);
					if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
					{
						LogWriter.WriteTraceLog(LogWriter.SELF, String.format("Apply SceneId(%s): Power(%s) fail. ", 
								scene_id, module_id));
					}
				}
				
				// 窗帘
				if (info.getCurtain_enable() == 1 && !info.getCurtain_moduleid().isEmpty()) {
					module_id = info.getCurtain_moduleid();
					module_command = "0,APPCURTAIN_ACTION," + strUserName + "," + module_id + "," + (info.getCurtain_control() == 0 ? "2" : "1");
					iRet = NotifyToModule(module_command);
					if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
					{
						LogWriter.WriteTraceLog(LogWriter.SELF, String.format("Apply SceneId(%s): Curtain(%s) fail. ", 
								scene_id, module_id));
					}
				}
				// 空调
				if (info.getAircon_enable() == 1 && !info.getAircon_moduleid().isEmpty()) {
					module_id = info.getAircon_moduleid();
					String str_current_type = dbMgr.getIRName(module_id);
					module_command = "0,APPAIRCONSERVER," + strUserName + "," + module_id + "," + str_current_type + "," + (info.getCurtain_control() == 0 ? "064" : "063");
					iRet = NotifyToModule(module_command);
					if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
					{
						LogWriter.WriteTraceLog(LogWriter.SELF, String.format("Apply SceneId(%s): AirCon(%s) fail. ", 
								scene_id, module_id));
					}
				}
				// PC
				if (info.getPc_enable() == 1 && !info.getPc_moduleid().isEmpty() && !info.getPc_mac_moduleid().isEmpty()) {
					String mac_moduleid = info.getPc_mac_moduleid();
					
					if (info.getPc_control() == 0) {				// CLOSE PC
						module_command = "0,APPPOWER," + strUserName + "," + mac_moduleid + "," + "0";
					} else if  (info.getPc_control() == 1) {		// OPEN PC
						module_id = info.getPc_moduleid();
						MODULE_INFO info_mac = dbMgr.QueryModuleInfo(mac_moduleid);
						String mac_address = info_mac.getMac();
						module_command = "0,MAGICPACKET," + strUserName + "," + module_id + "," + mac_address + ",1";
					}
					
					iRet = NotifyToModule(module_command);
					if (ServerRetCodeMgr.SUCCESS_CODE != iRet)
					{
						LogWriter.WriteTraceLog(LogWriter.SELF, String.format("Apply SceneId(%s): PC (%s) fail. ", 
								scene_id, info.getPower_moduleid()));
					}
				}
				
				} catch (Exception e) {
					e.printStackTrace();
					LogWriter.WriteExceptionLog(LogWriter.SELF, e);
				}
				
				LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) Succeed to Apply SceneId(%s). ", 
						thread.getSrcIP(),thread.getSrcPort(),strUserName,scene_id));
			}
			
			//通知 APP
			ResponseToAPP(strCmd, strUserName, strModuleID, ServerRetCodeMgr.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
			return ServerRetCodeMgr.ERROR_COMMON;
		} finally {
			dbMgr.Destroy();
		}
		
		return ServerRetCodeMgr.ERROR_COMMON;
	}

	@Override
	public int resp(Runnable thread_base, String strMsg) {
		return 0;
	}
}
