package sf.ibu.qni.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import sf.ibu.qni.common.Util;

/**
 * 设置soket客服端，为心跳作准备
 * @author 89003360
 *
 */
public class MySocketClient {
	private String monitorCenter;

	private int monitorPort;

	private PrintWriter pw;
	private BufferedReader br;
	private Socket socket;
	private InputStream iStream;

	public MySocketClient(String monitorCenter, int monitorPort) {
		this.monitorCenter = monitorCenter;
		this.monitorPort = monitorPort;
	}

	//初始化一个soket客服端
	public void init() throws Exception {
		socket = new Socket(monitorCenter, monitorPort);
		OutputStream os = socket.getOutputStream();
		pw = new PrintWriter(os);
		iStream = socket.getInputStream();
		br = new BufferedReader(new InputStreamReader(iStream));
	}

	public void write(String v) {
		synchronized ("write" + this.hashCode()) {
			Util.write(pw, v);
		}
	}

	public String read() throws Exception {
		synchronized ("read" + this.hashCode()) {
			try {
				return Util.read(br);
			} catch (SocketException e) {
				Thread.sleep(1000 * 10);
				throw e;
			}
		}

	}

	public void shutdown() throws Exception {
		socket.shutdownOutput();
		br.close();
		pw.close();
		socket.close();
	}
	
}
