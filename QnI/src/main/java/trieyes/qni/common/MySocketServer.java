package trieyes.qni.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trieyes.qni.core.server.ServerHandlerImpl;

/**
 * 设置socke服务端，让其永不停止
 * 
 * @author cat555666@126.com
 *
 */
public class MySocketServer {
	private ServerSocket server;
	private int port;
	private String name;
	private static final Logger logger = LoggerFactory.getLogger(MySocketServer.class);
	private ServerHandlerI oneOffConnectionHandler;

	public MySocketServer(int port, String name) throws Exception {
		this.name = name;
		this.port = port;
		init();
	}

	private void init() throws Exception {
		server = new ServerSocket(this.port);
	}

	public void startListener() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					Socket socket = null;
					try {
						socket = server.accept();// 启动服务端
						initClient(socket);// 初始化监听参数
					} catch (Throwable e) {
						logger.error("", e);
					}
				}
			}
		};
		thread.setName(port + " at " + name);
		thread.start();
	}

	private void initClient(final Socket socket) throws Exception {
		Thread thread = new Thread() {
			public void run() {
				try {
					String msgStr = Util.read(socket);
					if ("".equals(msgStr)) {
						return;
					}
					if (oneOffConnectionHandler != null) {
						oneOffConnectionHandler.handle(msgStr, socket);
					}
					logger.debug("{}:{} receive:{}",name , port, msgStr);
				} catch (Throwable e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String exceptionStack = sw.toString();
					try {
						Util.write(socket, exceptionStack);
					} catch (Throwable e1) {
						logger.error("", e1);
					}
					logger.error("", e);
				}
			}
		};
		thread.setName("initClient");
		thread.start();
	}

	public void setOneOffConnectionHandler(ServerHandlerI oneOffConnectionHandler) {
		this.oneOffConnectionHandler = oneOffConnectionHandler;
	}

}
