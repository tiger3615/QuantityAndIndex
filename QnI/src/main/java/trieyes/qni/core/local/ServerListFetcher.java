package trieyes.qni.core.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import trieyes.qni.common.MySocketClient;
import trieyes.qni.common.Util;
import trieyes.qni.core.Conf;
import trieyes.qni.core.server.ServerScanner;

public class ServerListFetcher {
	private static final Logger logger = LoggerFactory.getLogger(ServerListFetcher.class);
	private static long scanServerIntervalMs;
	public static volatile JSONArray serversJSONArr;
	public static Thread scannerThread = new Thread() {
		public void run() {
			while (true) {
				try {
					serversJSONArr = getAllAvailableServers();
				} catch (Throwable e) {
					logger.error("", e);
				}
				Util.sleep(scanServerIntervalMs);
			}
		}
	};
	static {
		try {
			serversJSONArr = getAllAvailableServers();
			scannerThread.setName("Availale server fetcher");
			// sleep for a while, then run thread. avoid call findAvailableIP twice at
			// initial stage.
			Conf conf = Conf.getIns();
			scanServerIntervalMs = conf.getScanServerIntervalMs();
			Util.sleep(scanServerIntervalMs);
			scannerThread.start();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static JSONArray getAllAvailableServers() throws Exception {
		// get all server info
		MySocketClient mySocketClient = new MySocketClient(ServerScanner.getAvailableIP(), Conf.getIns().getPort());
		mySocketClient.init();
		/*
		 * message tpl { "type":"getAllServers" }
		 */
		JSONObject cmdJObj = new JSONObject();
		cmdJObj.put("type", "getAllServers");
		mySocketClient.write(cmdJObj.toString());
		String allServers = mySocketClient.read();
		return JSONArray.parseArray(allServers);
	}
}
