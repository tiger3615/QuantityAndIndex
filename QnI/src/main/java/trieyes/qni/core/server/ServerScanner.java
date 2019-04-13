package trieyes.qni.core.server;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import trieyes.qni.common.Util;
import trieyes.qni.core.Conf;

public class ServerScanner {
	private static final Logger logger = LoggerFactory.getLogger(ServerScanner.class);
	private static volatile String availableIP;

	public static String getAvailableIP() {
		return availableIP;
	}
	static long scanServerIntervalMs;
	public static Thread scannerThread = new Thread() {
		public void run() {
			while (true) {
				try {
					availableIP = findAvailableIP();
				} catch (Throwable e) {
					logger.error("", e);
				}
				Util.sleep(scanServerIntervalMs);
			}
		}
	};
	static {
		try {
			availableIP = findAvailableIP();
			scannerThread.setName("Thread_server_scanner");
			//sleep for a while, then run thread. avoid call findAvailableIP twice at initial stage.
			Conf conf = Conf.getIns();
			scanServerIntervalMs = conf.getScanServerIntervalMs();
			Util.sleep(scanServerIntervalMs);
			scannerThread.start();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	public static String findAvailableIP() throws Exception {
		Conf conf = Conf.getIns();
		int port = conf.getPort();
		JSONArray ipArr = conf.getIPs();
		for (int i = 0; i < ipArr.size(); i++) {
			String ip = ipArr.getString(i);
			if (ping(ip, port)) {
				return ip;
			}
		}
		//if there is no available IP. call local IP
		return Util.getLocalIP();
	}
	public static boolean ping(String ip, int port) {
		try {
			Socket socket = new Socket(ip, port);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
