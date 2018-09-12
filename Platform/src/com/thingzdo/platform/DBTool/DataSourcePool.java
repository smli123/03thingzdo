package com.thingzdo.platform.DBTool;
import java.io.PrintWriter;  
import java.lang.reflect.InvocationHandler;  
import java.lang.reflect.Method;  
import java.lang.reflect.Proxy;  
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import javax.sql.DataSource;

import com.thingzdo.platform.LogTool.LogWriter;  

public class DataSourcePool implements DataSource{
	private static String username = null;  
    private static String password = null;  
    private static String dbname = null;
    private static int size = 10;  
    private final static String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static DataSourcePool pool = new DataSourcePool();  
    private static LinkedList<Connection> list = new LinkedList<Connection>();

    public static void Init(String strDBName, String strUserName, String strPass)
    {
    	dbname 		= strDBName;
    	username 	= strUserName;
    	password	= strPass;
    	
    	//创建对象就初始化size个数据库连接  
		String strDbUrl = String.format("jdbc:mysql://127.0.0.1:3306/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF-8", 
				dbname,username,password);
		try {
			Class.forName(DB_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}
		
		for(int i=0;i<size;i++){   
          try {  
				final Connection conn = DriverManager.getConnection(strDbUrl);
				LogWriter.WriteDebugLog(LogWriter.SELF,String.format("init: MSSQL Connection(%d): %s", i, conn.toString()));
                list.add(conn);  
              } catch (SQLException e) {  
            	  e.printStackTrace();  
                  LogWriter.WriteExceptionLog(LogWriter.SELF, e);
              }  
        }
		LogWriter.WriteDebugLog(LogWriter.SELF,String.format("init: MSSQL Connection's size in Pool: %d", list.size()));
    }
    
   private DataSourcePool(){}   

   public static DataSourcePool getInstance(){  
	   return pool;  
    }   

//   @Override  
//   public Connection getConnection() throws SQLException { 
//   	
//		synchronized (list) {
//			LogWriter.WriteDebugLog(LogWriter.SELF,String.format("MSSQL Connection's size in Pool: %d", list.size()));
//			if(list.size()>0) {  
//				try {
//					//取到连接，即从list中弹出一个Connection 连接  
//					final Connection conn = list.pop();
//					return conn;     
//				} catch (NoSuchElementException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					LogWriter.WriteExceptionLog(LogWriter.SELF, e, "list.pop is wrong. renew one to replace old.");
//				}
//				return makeConnection();
//			}
//			final Connection conn = makeConnection();
//			list.add(conn);
//			return conn;
//		}
//	}
   	
    @Override  
    public Connection getConnection() throws SQLException { 
    	
    	synchronized (pool) 
    	{
    		LogWriter.WriteDebugLog(LogWriter.SELF,String.format("MSSQL Connection's size in Pool: %d", list.size()));
    	    if(list.size()>0)
    	    {  
    	    	try {
  	    		  //取到连接，即从list中弹出一个Connection 连接  
	       		     final Connection conn = list.pop();
	       		  
	       		     if (validate(conn) == false) {
	       		    	LogWriter.WriteDebugLog(LogWriter.SELF, String.format("conn(%s) is invalid.", conn.toString()));
	       		     }
	       		    	 return conn;
    	    		
    	    		
    	    		
//    	    		  //取到连接，即从list中弹出一个Connection 连接  
//	       		     final Connection conn = list.pop();
	       		  
//	       		     if (validate(conn) == true) {
//	       		    	 return conn;
//	       		     } else {
//	       		    	LogWriter.WriteDebugLog(LogWriter.SELF, String.format("conn(%s) is invalid.", conn.toString()));
//	       		    	return makeConnection();
//	       		     }
	       		
//	       		     //动态代理，返回一个代理对象  
//	       		     return (Connection) Proxy.newProxyInstance(DataSourcePool.class.getClassLoader(), conn.getClass().getInterfaces(), 
//	       		    		new InvocationHandler()
//	       		     		{  
//	       					     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
//	       					     {
//	       					    	 // 暂时取消 连接池
//	       						         //如果Connection调用的是close方法就将连接返回给数据连接池  
//	       						         if(method.getName().equals("close")){
//	       						        	 LogWriter.WriteDebugLog(LogWriter.SELF,String.format("MSSQL Connection is closing, push it into pool. conn:%s", conn.toString()));
//	       						        	 list.add(conn);
//	       						             return null;
//	       						         }  
//	       						
//	       						         return method.invoke(conn, args);  
//	       					     }  
//	       		     		}//end InvocationHandler
//	       		    );
    	    	} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				LogWriter.WriteExceptionLog(LogWriter.SELF, e, "connection of list.pop may be wrong. please check it. renew one.");
    				// 出现这种情况，代表pool已经无效了，必须重新初始化；
    				LogWriter.WriteExceptionLog(LogWriter.SELF, e, "connection of list is invalid, we'll ReInit it.");
    				list = null;
    				list = new LinkedList<Connection>();
    				Init(dbname, username, password);
    			}
    	    } else {
    	    	return makeConnection();
    	    }
    	    
    	    //连接用完  
    	    throw new RuntimeException("Sorry, DB server is busy");  
    	}
    }
    /*
     * 连接归池
     */
    public static void freeConnection(Connection conn) {
    	if (list != null) {
    		try {
    			list.add(conn);
    		} catch (Exception e) {
				e.printStackTrace();
				LogWriter.WriteExceptionLog(LogWriter.SELF, e);
				
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				LogWriter.WriteDebugLog(LogWriter.SELF, String.format("conn(%s) is closed. 02", conn.toString()));
			}
    	}
   	}
    
    private final Connection makeConnection() throws SQLException {
		//创建对象就初始化size个数据库连接  
		String strDbUrl = String.format("jdbc:mysql://127.0.0.1:3306/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF-8", 
				dbname,username,password);
		try {
			Class.forName(DB_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}
	
		Connection conn = null;
		try {  
			conn = DriverManager.getConnection(strDbUrl);
			LogWriter.WriteDebugLog(LogWriter.SELF,String.format("New: MSSQL Connection: %s", conn.toString()));  
		} catch (SQLException e) {  
		  e.printStackTrace();
		  LogWriter.WriteExceptionLog(LogWriter.SELF, e);
		}
		return conn;  
    }

    /**
     * To detect if the connection is closed by the server as connection is timeout.
     * @author Martin
     * @param conn
     * @return true if the connection is normal, otherwise, return false.
     */
    private static boolean validate(Connection conn)
    {
        boolean isValidated = true;
        try {
            com.mysql.jdbc.Connection c = (com.mysql.jdbc.Connection)conn;
            c.ping();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            isValidated = false;
        }

        return isValidated;
    }
    
    @Override  
    public Connection getConnection(String username, String password) throws SQLException {  
		//TODO Auto-generated method stub  
		return null;  
    }  

	@Override  
	public PrintWriter getLogWriter() throws SQLException {  
		//TODO Auto-generated method stub  
		return null;  
	}  

	@Override  
	public int getLoginTimeout() throws SQLException {  
		//TODO Auto-generated method stub  
		return 0;  
	}  

	@Override  
	public void setLogWriter(PrintWriter out) throws SQLException {  
		//TODO Auto-generated method stub  
	}  

	@Override  
	public void setLoginTimeout(int seconds) throws SQLException {  
		//TODO Auto-generated method stub  
	}  

	@Override  
	public boolean isWrapperFor(Class<?> iface) throws SQLException {  
		//TODO Auto-generated method stub  
		return false;  
	}  

	@Override  
	public <T> T unwrap(Class<T> iface) throws SQLException {  
		//TODO Auto-generated method stub  
		return null;  
	}

//	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}  

}
