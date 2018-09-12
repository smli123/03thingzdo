package com.thingzdo.platform.DBTool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.thingzdo.platform.LogTool.LogWriter;

public class DBTool {
	private Connection conn 	= null;
	private Statement stmt		= null;
	/**********************************************************************************************************
	 * @name DBTool  数据库工具类
	 * @param 	strDBName: 数据库名称
	 * 					strUserName:访问数据库的用户名
	 * 					strPass:访问数据库的密码
	 * @author zxluan
	 * @throws SQLException 
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public DBTool()
	{
		try {
			conn = DataSourcePool.getInstance().getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}
	}
	
	/**********************************************************************************************************
	 * @name Destroy 销毁
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public void Destroy()
	{
		try {
			if(null != stmt)
			{
				stmt.close();
			}
			if(null != conn)
			{
				//conn.close();
				
				// 主动close改为freeConnection
				DataSourcePool.getInstance().freeConnection(conn);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SRV_SELF_LOG,e);
		}
	}
	/**********************************************************************************************************
	 * @name setAutoCommit
	 * @param value: 是否自动提交数据库的修改;
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public void setAutoCommit(boolean value) throws SQLException
	{
		conn.setAutoCommit(value);
	}
	/**********************************************************************************************************
	 * @name commit 提交已进行的数据库修改；在AutoCommit设置为false时有效
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public void commit() throws SQLException
	{
		conn.commit();
	}
	/**********************************************************************************************************
	 * @name rollback 对已进行的数据库修改回滚；在AutoCommit设置为false时有效
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public void rollback() throws SQLException
	{
		conn.rollback();
	}
	/**********************************************************************************************************
	 * @name executeQuery 执行数据库查询操作
	 * @param strSQL: 待执行的SQL语句
	 * @return ResultSet 数据查询结果集
	 * @exception SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public ResultSet executeQuery(String strSQL) throws ClassNotFoundException, SQLException
	{
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		return stmt.executeQuery(strSQL);
	}
	/**********************************************************************************************************
	 * @name executeQuery 执行数据库查询操作
	 * @param strSQL: 待执行的SQL语句
	 * @return ResultSet 数据查询结果集
	 * @exception SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public boolean execute(String strSQL) throws ClassNotFoundException, SQLException
	{
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		return stmt.execute(strSQL);
	}
	/**********************************************************************************************************
	 * @name executeUpdate 执行数据库查询操作
	 * @param strSQL: 待执行的SQL语句
	 * @return  int 已UPDATE的记录数 
	 * @exception SQLException ClassNotFoundException
	 * @author zxluan
	 * @date 2015/03/23
	 * **********************************************************************************************************/
	public int executeUpdate(String strSQL) throws SQLException
	{
			stmt = conn.createStatement();
			return stmt.executeUpdate(strSQL);
	}
	/**********************************************************************************************************
	 * @name insert 数据库插入操作
	 * @param 	strTableName: 待插入的数据库表名称
	 * 					content:待插入的数据，<属性，值>
	 * @return  boolean 插入是否成功
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public boolean insert(String strTableName,Map<String,String> content) throws SQLException
	{
		//"insert into %s(%s,%s,%s) values('%s','%s',%d)"
		String strNames = "(";
		String strValues= "values(";
		for (String key : content.keySet()) {
			strNames += key;
			strNames += ",";
			strValues += "'" + (String) content.get(key) + "'";
			strValues += ",";
		}
		strNames += ")";
		strValues += ")";
		strNames =strNames.substring(0,strNames.length() - 2) + ")";
		strValues = strValues.substring(0,strValues.length() - 2) + ")";
		String strSQL = "insert into " + strTableName + strNames + " " + strValues;
		return (1 == executeUpdate(strSQL));	
	}
	/**********************************************************************************************************
	 * @name delete 删除指定数据库表的全部数据
	 * @param 	strTableName: 待删除的数据库表名称
	 * @return  boolean 删除是否成功
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public boolean delete(String strTableName) throws SQLException
	{
		String strSQL = String.format("delete from %s", strTableName);
		return 0 <= executeUpdate(strSQL);
	}
	/**********************************************************************************************************
	 * @name delete 删除指定数据库表符合条件的数据
	 * @param 	strTableName: 待删除的数据库表名称
	 * 					selection:删除条件<属性，值>对
	 * @return  boolean 删除是否成功
	 * @exception SQLException
	 * @author zxluan
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public boolean delete(String strTableName,Map<String,String> selection) throws SQLException
	{
		String strSelection = GenerateAndString(selection);
		String strSQL = String.format("delete from %s where %s", strTableName,strSelection);
		return 0 <= executeUpdate(strSQL);
	}
	/**********************************************************************************************************
	 * @name query 查询指定数据库表的全部数据
	 * @param 	strTableName: 待查询的数据库表名称
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public ResultSet query(String strTableName) throws ClassNotFoundException, SQLException
	{
		String strSQL = String.format("select * from %s", strTableName);
		return executeQuery(strSQL);
	}
	/**********************************************************************************************************
	 * @name query 查询
	 * @param 	strTableName: 查询的数据库表名称
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public ResultSet query(String strTableName,Map<String,String> selection) throws ClassNotFoundException, SQLException
	{
		String strSelection = GenerateAndString(selection);
		String strSQL = String.format("select * from %s where %s", strTableName,strSelection);
		return executeQuery(strSQL);
	}
	/**********************************************************************************************************
	 * @name query 查询
	 * @param 	strTableName: 查询的数据库表名称
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public ResultSet query(String strTableName, String selection) throws ClassNotFoundException, SQLException
	{
		String strSQL = String.format("select * from %s where %s", strTableName, selection);
		return executeQuery(strSQL);
	}	
	/**********************************************************************************************************
	 * @name query 查询排序
	 * @param 	strTableName: 查询的数据库表名称
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public ResultSet query(String strTableName, String selection, String orders) throws ClassNotFoundException, SQLException
	{
		String strSQL = String.format("select * from %s where %s order by %s", strTableName, selection, orders);
		return executeQuery(strSQL);
	}	
	/**********************************************************************************************************
	 * @name query 联合查询
	 * @param 	vecTableName: 联合查询的数据库表列表
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public ResultSet query(Vector<String> vecTableName,Map<String,String> selection) throws ClassNotFoundException, SQLException
	{
		//数据库列表
		String strTableList = "";
		for(String value:vecTableName)
		{
			strTableList = strTableList + value + ",";
		}
		strTableList = strTableList.substring(0, strTableList.length() - 1);
		
		//条件列表
		String strSelection = GenerateAndString(selection);
		String strSQL = String.format("select * from %s where %s", strTableList,strSelection);
		return executeQuery(strSQL);
	}
	
	/**********************************************************************************************************
	 * @name query 联合查询
	 * @param 	vecTableName: 联合查询的数据库表列表
	 * @return  ResultSet 查询结果集
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public boolean update(String strTableName,Map<String,String> content,Map<String,String> selection) throws SQLException
	{
		String strSelection = GenerateAndString(selection);
		String strSet			= GenerateSetString(content);
		//update user_info set %s='%s',%s='%s',%s='%s' where %s='%s'", 
		String strSQL = String.format("update %s set %s where %s", strTableName,strSet,strSelection);
		return executeUpdate(strSQL) > 0;
	}
	/**********************************************************************************************************
	 * @name insert_or_update 插入更新
	 * @param 	vecTableName: 
	 * @return  ResultSet 
	 * @author zxluan
	 * @throws SQLException 
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	public boolean insert_or_update(String strTableName,Map<String,String> content,Map<String,String> selection) throws SQLException
	{
		String strSelection = GenerateAndString(selection);
		String strValue	   = GenerateSetString(content);
		String strSQL = String.format("insert into %s set %s ON DUPLICATE KEY UPDATE %s", strTableName,strValue,strSelection);
		return executeUpdate(strSQL) > 0;
	}
	/**********************************************************************************************************
	 * @name GenerateAndString 生成与条件字符串
	 * @param 	selection: 条件
	 * @return  String 与字符串，例如 user_name='zxluan' and module_id='100'
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	private String GenerateAndString(Map<String,String> selection)
	{
		String strSelection = "";
		for (String key : selection.keySet()) {
			strSelection = strSelection + String.format("%s='%s'", key, selection.get(key))  + " and ";
		}
		strSelection = strSelection.substring(0,strSelection.length() - 5);
		return strSelection;
	}
	/**********************************************************************************************************
	 * @name GenerateAndString 生成SET字符串
	 * @param 	content: 设置的内容对
	 * @return  String set字符串，例如 （set） %s='%s',%s='%s',%s='%s'
	 * @author zxluan
	 * @throws ClassNotFoundException SQLException
	 * @date 2015/03/24
	 * **********************************************************************************************************/
	private String GenerateSetString(Map<String,String> content)
	{
		String strSelection = "";
		for (String key : content.keySet()) {
			strSelection += String.format("%s='%s'", key, content.get(key));
			strSelection += ",";
		}
		strSelection = strSelection.substring(0,strSelection.length() - 1);
		return strSelection;
	}

	public static void main(String[] str)
	{
		DataSourcePool.Init("smartplug", "root", "2681b009");
		DBTool db_tool = new DBTool();
		try {
			/*
			Map<String,String> content = new HashMap();
			content.put("name", "zxluan");
			content.put("password", "19821218");
			content.put("sim_no", "13066831952");
			content.put("email", "csulzx@qq.com");
			boolean bret = db_tool.insert("user_info", content);
			Map<String,String> selection = new HashMap();
			selection.put("name", "zxluan");
			bret = db_tool.delete("user_info", selection);
			bret = db_tool.delete("user_info");
			*/
			Vector<String> vecTable = new Vector<String>();
			vecTable.add("user_info");
			Map<String,String> sel = new HashMap();
			sel.put("user_name", "zxluan"); 
			ResultSet value = db_tool.query(vecTable, sel);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
