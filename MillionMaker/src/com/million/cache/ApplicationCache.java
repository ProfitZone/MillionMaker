package com.million.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationCache {
	
	private Map<String , Set<Object>> cache = new HashMap<>();
	
	private static ApplicationCache myself = new ApplicationCache();
	
	private ApplicationCache() {};
	
	public static ApplicationCache getInstance() {
		return ApplicationCache.myself;
	}
	
	public void put(String groupId , Object cacheObject)	{
		
		Set<Object> cacheSet = cache.get(groupId);
		
		if(cacheSet == null )	{
			cacheSet = new HashSet<>();
			cache.put(groupId, cacheSet);
		}
		
		cacheSet.add(cacheObject);
	}
	
	public boolean contains(String groupId , Object cacheObject)	{
		
		Set<Object> cacheSet = cache.get(groupId);
		
		if(cacheSet == null)	{
			return false;
		}
		
		return cacheSet.contains(cacheObject);
		
	}

}
