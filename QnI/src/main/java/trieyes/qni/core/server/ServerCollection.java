package trieyes.qni.core.server;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import trieyes.qni.common.Util;
import trieyes.qni.core.Conf;

public class ServerCollection {
	//map makes new IP as key overwrite previous one
	private static HashMap<String, JSONObject> serverMap = new HashMap<String, JSONObject>();
	private static final Logger logger = LoggerFactory.getLogger(ServerCollection.class);
	private static ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();
	static {
		/*
		 * tpl { "IP":"127.0.0.1", "port":2000, "birthDay":22222L } }
		 */
		JSONObject self= new JSONObject();
		Conf conf;
		try {
			conf = Conf.getIns();
			JSONObject confJObj = conf.getConfJson();
			self.put("port", confJObj.getIntValue("port"));
			self.put("IP", Util.getLocalIP());
			put(self);
		} catch (Throwable e) {
			logger.error("",e);
		}
	}

	/**
	 * 将IP和时间存入一个全局的HashMap中
	 * @param serverInfoJObj
	 */
	public static void put(JSONObject serverInfoJObj) {
		String ip = serverInfoJObj.getString("IP");
		serverInfoJObj.put("birthDay", System.currentTimeMillis());
		rrw.writeLock().lock();
		try {
			serverMap.put(ip, serverInfoJObj);
		}finally {
			rrw.writeLock().unlock();
		}
		
	}

	/**
	 * 获取所有有效server
	 * @return
	 */
	public static JSONArray getAllServers() {
		JSONArray serversJarr = new JSONArray();
		rrw.readLock().lock();
		try {
			serverMap.forEach((k,oneClient)->{
				if (System.currentTimeMillis() - oneClient.getLong("birthDay") < 5000) {
					serversJarr.add(oneClient);
				}
			});
		}finally {
			rrw.readLock().unlock();
		}
		return serversJarr;
	}
}
