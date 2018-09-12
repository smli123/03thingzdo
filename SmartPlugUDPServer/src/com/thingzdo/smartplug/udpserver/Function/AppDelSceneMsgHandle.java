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

public class AppDelSceneMsgHandle implements ICallFunction{
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
		
		int scene_id		= Integer.valueOf(strRet[4].trim());
		
		/* 校验参数合法性 */
		int iRet = CheckAppCmdValid(strUserName, strCookie);
		if( ServerRetCodeMgr.SUCCESS_CODE != iRet)
		{
			ResponseToAPP(strCmd, strUserName, ServerCommDefine.DEFAULT_MODULE_ID, iRet);
			return iRet;
		}
		
		ServerWorkThread thread = (ServerWorkThread)thread_base;
		ServerDBMgr dbMgr = new ServerDBMgr();
		try {
			//开启事务机制
			dbMgr.BeginTansacion();
			
			if (!dbMgr.DeleteSceneInfo(scene_id, strUserName))
			{
				dbMgr.Rollback();
				dbMgr.EndTansacion();
				return ServerRetCodeMgr.ERROR_CODE_FAILED_DB_OPERATION;
			}
			
			//提交事务
			dbMgr.Commit();
			dbMgr.EndTansacion();
			
			LogWriter.WriteTraceLog(LogWriter.SELF, String.format("(%s:%d)\t App(%s) [Database] Succeed to Del SceneId(%s). ", 
					thread.getSrcIP(),thread.getSrcPort(),strUserName,scene_id));
			
			//通知 APP，删车成功
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
