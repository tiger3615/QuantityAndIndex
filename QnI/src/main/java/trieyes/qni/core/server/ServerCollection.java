package sf.ibu.qni.core.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import sf.ibu.qni.common.Util;
import sf.ibu.qni.core.Conf;

public class ServerCollection {
	public static HashMap<String, JSONObject> serverMap = new HashMap<String, JSONObject>();
	private static final Logger logger = LoggerFactory.getLogger(ServerCollection.class);
	static {
		/*
		 * tpl { "IP":"127.0.0.1", "port":2000, "filePaths":["path1","path2"] } }
		 */
		JSONObject self= new JSONObject();
		Conf conf;
		try {
			conf = Conf.getIns();
			JSONObject confJObj = conf.getConfJson();
			self.put("port", confJObj.getIntValue("port"));
			self.put("IP", Util.getLocalIP());
			put(self);
		} catch (Exception e) {
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
		serverMap.put(ip, serverInfoJObj);
	}

	/**
	 * 获取所有有效server
	 * @return
	 */
	public static JSONArray getAllServers() {
		JSONArray serversJarr = new JSONArray();
		Set<String> ipKeys = serverMap.keySet();
		Iterator<String> itor = ipKeys.iterator();
		while (itor.hasNext()) {
			String ipKey = itor.next();
			JSONObject oneClient = serverMap.get(ipKey);
			if (System.currentTimeMillis() - oneClient.getLong("birthDay") < 5 * 1000) {
				serversJarr.add(oneClient);
			}

		}
		return serversJarr;
	}
}
