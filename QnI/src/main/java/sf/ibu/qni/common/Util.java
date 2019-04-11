package sf.ibu.qni.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Util {

	public static String file2String(String filePath) throws Exception {
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null) {
			sb.append(temp+"\r\n");
		}
		return sb.toString();
	}
	
	public static String file2Line(String filePath) throws Exception {
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		return sb.toString();
	}

	public static void stream2file(String filePath, InputStream iStream) throws Exception {
		File f = new File(filePath);
		OutputStream os = new FileOutputStream(f);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		BufferedInputStream bis = new BufferedInputStream(iStream);
		byte[] buff = new byte[1024 * 1024];
		int len = 0;
		while ((len = bis.read(buff)) != -1) {
			bos.write(buff, 0, len);
			bos.flush();
		}
		bos.close();
	}

	public static Object runJS(String jsbody, LinkedHashMap<String, List<LinkedHashMap<String, Object>>> resultMap)
			throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		putResultMap(engine, resultMap);
		String script = "function valid() { " + jsbody + " }";
		engine.eval(script);
		Invocable inv = (Invocable) engine;
		return inv.invokeFunction("valid");
	}

	public static String runJS4String(String jsbody,
			LinkedHashMap<String, List<LinkedHashMap<String, Object>>> resultMap) throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		putResultMap(engine, resultMap);
		String script = "function run4String() { return " + jsbody + "; }";
		engine.eval(script);
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("run4String");
	}

	public static void putResultMap(ScriptEngine engine,
			LinkedHashMap<String, List<LinkedHashMap<String, Object>>> resultMap) {
		Set<String> keys = resultMap.keySet();
		for (String key : keys) {
			engine.put(key, resultMap.get(key));
		}
	}

	public static String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	public static String read(InputStream is) throws Exception {
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null) {
			sb.append(temp+"\n");
		}
		return sb.toString();
	}
	public static String read(BufferedReader br) throws Exception {
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null && !temp.equals("<<eos>>")) {
			sb.append(temp);
		}
		return sb.toString();
	}

	public static String read(Socket socket) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = br.readLine()) != null && !temp.equals("<<eos>>")) {
			sb.append(temp);
		}
		return sb.toString();
	}

	public static void transferFile(Socket socket, InputStream inputStream) throws Exception {
		OutputStream oStream = socket.getOutputStream();
		//write(socket, String.format("{\"fileName\":\"%s\"}", fileName));
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		BufferedOutputStream bos = new BufferedOutputStream(oStream);
		byte[] buffer = new byte[1024];
		int readLength = 0;
		while ((readLength = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, readLength);
			bos.flush();
		}
		bos.close();
	}

	public static void write(Socket socket, String content) throws Exception {
		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os);
		pw.write(content + "\n");
		// <<eos>> means this transfer is done
		pw.write("<<eos>>\n");
		pw.flush();
		pw.close();
	}

	public static void write(PrintWriter pw, String v) {
		pw.write(v + "\n");
		pw.write("<<eos>>\n");
		pw.flush();
	}

	public static String UTC(String format) {
		SimpleDateFormat df2 = new SimpleDateFormat(format);
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df2.format(Calendar.getInstance().getTime());
	}

	public static String formatTime(Date date, String format) {
		SimpleDateFormat df2 = new SimpleDateFormat(format);
		return df2.format(date);
	}

	public static Date str2DateTime(String dateTime, String formatter) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		return format.parse(dateTime);
	}

	public static Date UTCDateTime() throws Exception {
		String formatter = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		return format.parse(UTC(formatter));
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}

	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws InvalidAlgorithmParameterException
	 * @throws Exception
	 */
	public static String encode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] bytes = cipher.doFinal(data.getBytes());
			return byte2hex(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static String decode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return new String(cipher.doFinal(hex2byte(data.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * 二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

	private static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	public static String getLocalIP() {
		String ip = "";
		try {
			InetAddress address = InetAddress.getLocalHost();// 获取的是本地的IP地址
			ip = address.getHostAddress();
		} catch (UnknownHostException e) {
			ip="";
		}
		return ip;
	}
}
