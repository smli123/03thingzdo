package com.thingzdo.platform.ObjMgrTool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectMapManager {
	private Map< String,Object> objMap 		= new HashMap<String,Object>();
	private ReadWriteLock  lock 						= new ReentrantReadWriteLock();
	public void Register(String key,Object value)
	{
		lock.writeLock().lock();
		//如果该KEY已存在，则替换其VALUE
		objMap.put(key, value);
		lock.writeLock().unlock();
	}
	public void UnRegister(String key,Object value)
	{
		if(null == key || null == value)
			return;
		if(value == objMap.get(key))
		{
			lock.writeLock().lock();
			objMap.remove(key);
			lock.writeLock().unlock();
		}
		
	}
	public Object GetValue(String key)
	{
		return objMap.get(key);
	}
	
	public boolean Containskey(String key)
	{
		return objMap.containsKey(key);
	}
	public Collection<Object> GetValues()
	{
		return objMap.values();
	}
	public String ToString()
	{
		String strRet = String.format("KEY \t VALUE\n");
		Set<String> key_set = objMap.keySet();
        for (Iterator<String> it = key_set.iterator(); it.hasNext();) {
            String key =  it.next();
            Object value = objMap.get(key);
            strRet += String.format("%s \t %s\n", key,value.toString());
        }
        return strRet;
	}
}
