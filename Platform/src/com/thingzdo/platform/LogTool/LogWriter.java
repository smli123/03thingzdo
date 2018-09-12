package com.thingzdo.platform.LogTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.thingzdo.platform.commdefine.PlatformCommDefine;

public class LogWriter {
	/*读写锁*/
	private final static ReadWriteLock  errLogLock 		= new ReentrantReadWriteLock();
	private final static ReadWriteLock  warnLogLock 	= new ReentrantReadWriteLock();
	private final static ReadWriteLock  traceLogLock 	= new ReentrantReadWriteLock();
	private final static ReadWriteLock  exceptionLogLock 	= new ReentrantReadWriteLock();
	private final static int MAX_FILE_SIZE = 100*1024*1024;//调试文件最大100
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final static Map<Integer,String> m_mapLogType = new HashMap();
	/*日志属性*/
	public final static int APP_TO_SRV		= 0;
	public final static int SRV_TO_APP		= 1;
	public final static int SRV_SELF_LOG	= 2;
	public final static int MOD_TO_SRV		= 3;
	public final static int SRV_TO_MOD		= 4;
	public final static int RECV			= 5;
	public final static int SEND			= 6;
	public final static int SELF			= 7;
	/*日志目录*/
	private static String BASE_LOG_DIRECTORY;
	private static String ERROR_LOG_DIRECTORY ;
	private static String TRACE_LOG_DIRECTORY ;
	private static String WARNING_LOG_DIRECTORY;
	private static String EXCEPTION_LOG_DIRECTORY	;
	private static String DEBUG_LOG_DIRETORY;
	private static String HEART_LOG_DIRECTORY;
	/*调试标识*/
	public final static boolean DEBUG_MODE 							= true;
	public static void Init(String strServerName)
	{
		BASE_LOG_DIRECTORY					= String.format("%s/%s/log/",PlatformCommDefine.BASE_DIRECTORY, strServerName);
		ERROR_LOG_DIRECTORY 				= BASE_LOG_DIRECTORY + "error/";
		TRACE_LOG_DIRECTORY 				= BASE_LOG_DIRECTORY + "trace/";
		WARNING_LOG_DIRECTORY			= BASE_LOG_DIRECTORY + "warning/";
		EXCEPTION_LOG_DIRECTORY		= BASE_LOG_DIRECTORY + "exception/";
		DEBUG_LOG_DIRETORY			 		= BASE_LOG_DIRECTORY + "debug/";
		HEART_LOG_DIRECTORY				= BASE_LOG_DIRECTORY + "heart/";
		/*STEP1 创建ERROR LOG目录 */
		File logDir = new File(ERROR_LOG_DIRECTORY);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
		
		/*STEP2 创建TRACE LOG目录 */
		logDir = new File(TRACE_LOG_DIRECTORY);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
		
		/*STEP3 创建WARNING LOG目录*/
		logDir = new File(WARNING_LOG_DIRECTORY);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
		
		/*STEP4 创建异常处理LOG目录*/
		logDir = new File(EXCEPTION_LOG_DIRECTORY);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
		
		/*STEP5 创建调试LOG目录*/
		if(DEBUG_MODE)
		{
			logDir = new File(DEBUG_LOG_DIRETORY);
			if(!logDir.exists())
			{
				logDir.mkdirs();
			}			
		}
		
		/*STEP6 创建心跳LOG目录*/
		logDir = new File(HEART_LOG_DIRECTORY);
		if(!logDir.exists())
		{
			logDir.mkdirs();
		}
		
		/*初始化LOG类型*/
		m_mapLogType.put(APP_TO_SRV, 	"APP_TO_SRV");
		m_mapLogType.put(SRV_TO_APP, 	"SRV_TO_APP");
		m_mapLogType.put(SRV_SELF_LOG, "SRV_SELF_LOG");
		m_mapLogType.put(MOD_TO_SRV, 	"MOD_TO_SRV");
		m_mapLogType.put(SRV_TO_MOD, 	"SRV_TO_MOD");
		m_mapLogType.put(RECV, 	"RECV");
		m_mapLogType.put(SEND, 	"SEND");
		m_mapLogType.put(SELF, 	"SELF");
		
	}
	public static String GetLineInfo()
	{
        StackTraceElement ste = new Throwable().getStackTrace()[2]; 
        return ste.getFileName() + ": Line " + ste.getLineNumber(); 
	}
	public static void WriteErrorLog(int LogType,String strLog)
	{
		Date date =  new Date();
		String strLogFileName = ERROR_LOG_DIRECTORY + String.format("%tF.txt", date);
		try {
			boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fErrWriter = new FileWriter(strLogFileName,bAppend);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [ERROR]\t %s \t %s<%s>\r\n", strTime,m_mapLogType.get(LogType),strLog,GetLineInfo());
			errLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			errLogLock.writeLock().unlock();
		}
		WriteDebugLogForOther(LogType,strLog);
	}	
	
	public static  void WriteWarningLog(int LogType,String strLog)
	{
		Date date =  new Date();
		String strLogFileName = WARNING_LOG_DIRECTORY + String.format("%tF.txt", date);
		try {
			FileWriter fErrWriter = new FileWriter(strLogFileName,true);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [WARN]\t %s \t %s<%s>\r\n", strTime,m_mapLogType.get(LogType),strLog,GetLineInfo());
			warnLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			warnLogLock.writeLock().unlock();
		}
		WriteDebugLogForOther(LogType,strLog);
	}
	
	public static void WriteTraceLog(int LogType,String strLog)
	{
		Date date =  new Date();
		String strLogFileName = TRACE_LOG_DIRECTORY + String.format("%tF.txt", date);
		try {
			FileWriter fErrWriter = new FileWriter(strLogFileName,true);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [TRACE]\t %s \t %s<%s>\r\n", strTime,m_mapLogType.get(LogType),strLog,GetLineInfo());
			traceLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			traceLogLock.writeLock().unlock();
		}
		WriteDebugLogForOther(LogType,strLog);
	}
	
	public static void WriteExceptionLog(int LogType,Exception e,String strInfo)
	{
		Date date =  new Date();
		String strLogFileName = EXCEPTION_LOG_DIRECTORY + String.format("%tF.txt", date);
		
		try {
			boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fw = new FileWriter(strLogFileName, bAppend); //  throw IOException
			PrintWriter pw = new PrintWriter(fw);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [EXCEPT]\t %s <%s>\t", strTime,m_mapLogType.get(LogType),GetLineInfo());
			exceptionLogLock.writeLock().lock();
			pw.write(strLine);
			pw.write(String.format("<info:%s>", strInfo));
			e.printStackTrace(pw);
			exceptionLogLock.writeLock().unlock();
			pw.close();
			
			/*打印到DEBUG*/
			strLogFileName = DEBUG_LOG_DIRETORY + String.format("%tF.txt", date);
			fw = new FileWriter(strLogFileName, bAppend); //  throw IOException
			pw = new PrintWriter(fw);
			strTime = String.format("%tT", date);	
			strLine = String.format("%s [DEBUG]\t %s <%s>\t", strTime,m_mapLogType.get(LogType),GetLineInfo());
			exceptionLogLock.writeLock().lock();
			pw.write(strLine);
			pw.write(String.format("<info:%s>", strInfo));
			e.printStackTrace(pw);
			exceptionLogLock.writeLock().unlock();
			pw.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void WriteExceptionLog(int LogType,Exception e)
	{
		Date date =  new Date();
		String strLogFileName = EXCEPTION_LOG_DIRECTORY + String.format("%tF.txt", date);
		
		try {
			boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fw = new FileWriter(strLogFileName, bAppend); //  throw IOException
			PrintWriter pw = new PrintWriter(fw);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [EXCEPT]\t %s <%s>\t", strTime,m_mapLogType.get(LogType),GetLineInfo());
			exceptionLogLock.writeLock().lock();
			pw.write(strLine);
			e.printStackTrace(pw);
			exceptionLogLock.writeLock().unlock();
			pw.close();
			
			/*打印到DEBUG*/
			strLogFileName = DEBUG_LOG_DIRETORY + String.format("%tF.txt", date);
			fw = new FileWriter(strLogFileName, bAppend); //  throw IOException
			pw = new PrintWriter(fw);
			strTime = String.format("%tT", date);	
			strLine = String.format("%s [EXCEPT]\t %s <%s>\t", strTime,m_mapLogType.get(LogType),GetLineInfo());
			exceptionLogLock.writeLock().lock();
			pw.write(strLine);
			e.printStackTrace(pw);
			exceptionLogLock.writeLock().unlock();
			pw.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void WriteDebugLog(int LogType,String strLog)
	{
		if(!DEBUG_MODE)
		{
			return;
		}
		Date date =  new Date();
		String strLogFileName = DEBUG_LOG_DIRETORY + String.format("%tF.txt", date);
		try {
			//限定DEBUG文件大小，防止文件过大，导致服务器崩溃
            boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fErrWriter = new FileWriter(strLogFileName,bAppend);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [DEBUG]\t %s \t %s<%s>\r\n", strTime,m_mapLogType.get(LogType),strLog,GetLineInfo());
			traceLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
			strLine  = "\r\n" + strLine;
			System.out.print(strLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			traceLogLock.writeLock().unlock();
		}
	}
	
	public static void WriteDebugLogForOther(int LogType,String strLog)
	{
		if(!DEBUG_MODE)
		{
			return;
		}
		Date date =  new Date();
		String strLogFileName = DEBUG_LOG_DIRETORY + String.format("%tF.txt", date);
		try {
			//限定DEBUG文件大小，防止文件过大，导致服务器崩溃
            boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fErrWriter = new FileWriter(strLogFileName,bAppend);
			String strTime = String.format("%tT", date);	
			
	        StackTraceElement ste[] = new Throwable().getStackTrace(); 
	        String strInfo = ste[2].getFileName() + ": Line " + ste[2].getLineNumber(); 
			String strLine = String.format("%s [DEBUG]\t %s \t %s<%s>\r\n", strTime,m_mapLogType.get(LogType),strLog,strInfo);
			traceLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
			strLine  = "\r\n" + strLine;
			System.out.print(strLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			traceLogLock.writeLock().unlock();
		}
	}

	public static void WriteHeartLog(int LogType,String strLog)
	{
		if(!DEBUG_MODE)
		{
			return;
		}
		Date date =  new Date();
		String strLogFileName = HEART_LOG_DIRECTORY + String.format("%tF.txt", date);
		try {
			//限定DEBUG文件大小，防止文件过大，导致服务器崩溃
            boolean bAppend = GetFileAppendFlag(strLogFileName);
			FileWriter fErrWriter = new FileWriter(strLogFileName,bAppend);
			String strTime = String.format("%tT", date);	
			String strLine = String.format("%s [HEART]\t %s \t %s\r\n", strTime,m_mapLogType.get(LogType),strLog);
			traceLogLock.writeLock().lock();
			fErrWriter.write(strLine);
			fErrWriter.flush();
			fErrWriter.close();
			strLine  = "\r\n" + strLine;
			System.out.print(strLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			traceLogLock.writeLock().unlock();
		}
		
		WriteDebugLogForOther(LogType, strLog);
	}
	
	/**
	 * @name GetFileAppendFlag 
	 * @function 根据指定文件大小，判定是否以追加的方式打开该文件;
	 * @param strFileName
	 * @return
	 * @throws IOException 
	 */
	private static boolean GetFileAppendFlag(String strFileName) throws IOException
	{
		File f = new File(strFileName) ;
		if(!f.exists())
		{
			return true;
		}
        FileInputStream fis  = new FileInputStream(f);
        if(fis.available() > MAX_FILE_SIZE)
        {
        	fis.close();
        	return false;//不再追加
        }
        fis.close();
        return true;
	}
	
	public static void main(String[] args)  {
		LogWriter.WriteErrorLog(APP_TO_SRV, "test");
	}
}
