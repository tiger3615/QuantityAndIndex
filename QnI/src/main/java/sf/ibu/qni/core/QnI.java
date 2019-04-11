package sf.ibu.qni.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import sf.ibu.qni.common.MySocketClient;
import sf.ibu.qni.common.MySocketServer;
import sf.ibu.qni.common.Util;
import sf.ibu.qni.core.local.LocalHandler;
import sf.ibu.qni.core.server.ServerHandler;
import sf.ibu.qni.core.server.ServerScanner;

/**
 * 
 * @author 89003360
 *
 */
public class QnI {
	private static LocalHandler localHandler = new LocalHandler();
	private static boolean inited = false;
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
			connectMonitorThread.setName("connectMonitorThread");
			connectMonitorThread.start();
		}
	}
	
	/**
	 * 心跳，告诉最小ip所在的服务器，自己的状态
	 * @throws Exception
	 */
	public static void register() throws Exception {
		while (true) {
			try {
				MySocketClient mySocketClient = new MySocketClient(ServerScanner.getAvailableIP(),Conf.getIns().getPort());
				/*
				 * message tpl { "type":"register", "info":{ "IP":"127.0.0.1", "port":2000} }
				 */
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "register");
				JSONObject infoJObj=Conf.getIns().getConfJson();
				
				//clone one to avoid update orignal configure
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
			Util.sleep(3 * 1000);//3秒
		}
	}

	/**
	 * 查询服务器总数和自己所在位置
	 * @param ip 
	 * @return
	 */
	public static HashMap<String,Integer> getServerSizeAndIndex(String ip) {
		try {
			return localHandler.getIndexAndServerTotal(ip);
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return new HashMap<String,Integer>();
		}
	}
	
	/**
	 * 启动一个soket
	 * @throws Exception
	 */
	public static void startInnerServer() throws Exception {
		if (Conf.getIns().contains(Util.getLocalIP())) {
			MySocketServer mySocketServer = new MySocketServer(Conf.getIns().getPort(), "QnI server");
			mySocketServer.setOneOffConnectionHandler(new ServerHandler());
			mySocketServer.startListener();
		}
		

	}
}
