package trieyes.qni.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import trieyes.qni.common.MySocketClient;
import trieyes.qni.common.MySocketServer;
import trieyes.qni.common.Util;
import trieyes.qni.core.local.ServerListFetcher;
import trieyes.qni.core.server.ServerHandlerImpl;
import trieyes.qni.core.server.ServerScanner;

/**
 * 
 * @author 89003360
 *
 */
public class QnI {
	private static volatile boolean inited = false;
	private static final Logger logger = LoggerFactory.getLogger(QnI.class);
	
	public static void init() {
		if (!inited) {
			inited = true;
			Thread connectMonitorThread = new Thread() {
				public void run() {
					try {
						//start receive server just once
						startInnerServer();
						register();//心跳
					} catch (Throwable e1) {
						logger.error("",e1);
					}
				}
			};
			connectMonitorThread.setName("kick off");
			connectMonitorThread.start();
		}
	}
	
	/**
	 * 心跳，告诉最小ip所在的服务器，自己的状态
	 * @throws Exception
	 */
	public static void register() throws Exception {
		long registerIntervalMs=Conf.getIns().getRegisterIntervalMsMs();
		while (true) {
			try {
				MySocketClient mySocketClient = new MySocketClient(ServerScanner.getAvailableIP(),Conf.getIns().getPort());
				/*
				 * message tpl { "type":"register", "info":{ "IP":"127.0.0.1", "port":2000} }
				 */
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "register");
				JSONObject infoJObj=Conf.getIns().getConfJson();
				
				//clone one to avoid update original configure
				JSONObject infoJObjSending=new JSONObject();
				infoJObjSending.put("IP", Util.getLocalIP());
				infoJObjSending.put("port", infoJObj.getIntValue("port"));
				jsonObject.put("info", infoJObjSending);
				//三大步
				mySocketClient.init();//启动
				mySocketClient.write(jsonObject.toString());//发消息
				mySocketClient.shutdown();//停止
			} catch (Throwable e) {
				logger.error("",e);
			}
			Util.sleep(registerIntervalMs);//3秒
		}
	}

	public static int getServerQuantity() throws Exception {
		JSONArray servers = ServerListFetcher.serversJSONArr;
		if (servers == null) {
			// anyway current server is running, so return 1.
			return 1;
		} else {
			int serverQuantity = servers.size();
			return serverQuantity > 0 ? serverQuantity : 1;
		}
	}

	public static int getSelfIndex() {
		String ip=Util.getLocalIP();
		JSONArray servers = ServerListFetcher.serversJSONArr;
		if ( servers == null) {
			// anyway current server is running, so return 1.
			return 0;
		} else {
			for (int i = 0; i < servers.size(); i++) {
				JSONObject oneServerInfo = servers.getJSONObject(i);
				if (ip.equals(oneServerInfo.getString("IP"))) {
					return i;
				}
			}
			return 0;
		}
		
	}
	
	/**
	 * 启动一个soket
	 * @throws Exception
	 */
	public static void startInnerServer() throws Exception {
		MySocketServer mySocketServer = new MySocketServer(Conf.getIns().getPort(), "QnI server");
		mySocketServer.setOneOffConnectionHandler(new ServerHandlerImpl());
		mySocketServer.startListener();
	}
}
