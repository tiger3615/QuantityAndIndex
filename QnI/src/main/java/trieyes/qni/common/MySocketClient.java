package trieyes.qni.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import trieyes.qni.common.Util;

/**
 * 设置soket客服端，为心跳作准备
 * @author cat555666@126.com
 *
 */
public class MySocketClient {

	private String targetIP;
	private int targetPort;

	private PrintWriter pw;
	private BufferedReader br;
	private Socket socket;

	public MySocketClient(String targetIP, int targetPort) {
		this.targetIP = targetIP;
		this.targetPort = targetPort;
	}

	//初始化一个soket客服端
	public void init() throws Exception {
		socket = new Socket(targetIP, targetPort);
		OutputStream os = socket.getOutputStream();
		pw = new PrintWriter(os);
		InputStream is = socket.getInputStream();
		br = new BufferedReader(new InputStreamReader(is));
	}
	//assure only one thread can call write(String v)
	private Object writeLock=new Object();
	public void write(String v) {
		synchronized (writeLock) {
			Util.write(pw, v);
		}
	}
	//assure only one thread can call read()
	private Object readLock=new Object();
	public String read() throws Exception {
		synchronized (readLock) {
			return Util.read(br);
		}
	}

	public void shutdown() throws Exception {
		socket.shutdownOutput();
		br.close();
		pw.close();
		socket.close();
	}
	
}
