package trieyes.qni.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import trieyes.qni.common.Util;

public class Conf {
	private JSONObject confJson;
	private static Conf ins;

	public static Conf getIns() throws Exception {
		if (ins == null) {
			ins = new Conf();
		}
		return ins;
	}

	private Conf() throws JSONException, Exception {
		refresh();
	}

	private void refresh() throws JSONException, Exception {
		String configureFile = Conf.class.getResource("/QnI.json").toURI().getPath();
		confJson = JSONObject.parseObject(Util.file2String(configureFile));
	}

	public JSONArray getIPs() {
		return confJson.getJSONArray("IPs");
	}

	public int getPort() {
		return confJson.getIntValue("port");
	}

	public long getScanServerIntervalMs() {
		return confJson.getLongValue("scanServerIntervalMs");
	}
	
	public long getRegisterIntervalMsMs() {
		return confJson.getLongValue("registerIntervalMs");
	}
	public JSONObject getConfJson() {
		return confJson;
	}

	public boolean contains(String ip) {
		JSONArray ipArray = confJson.getJSONArray("IPs");
		for (int i = 0; i < ipArray.size(); i++) {
			String oneIP = ipArray.getString(i);
			if (ip.equals(oneIP)) {
				return true;
			}
		}
		return false;
	}

}
