package sf.ibu.qni.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import sf.ibu.qni.common.Util;

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
		String configureFile = Conf.class.getResource("/NetFileSearch.json").toURI().getPath();
		confJson = JSONObject.parseObject(Util.file2String(configureFile));
	}

	public JSONArray getIPs() {
		return confJson.getJSONArray("IPs");
	}

	public int getPort() {
		return confJson.getIntValue("port");
	}

	public long getScanServerIntervalms() {
		return confJson.getLongValue("scanServerIntervalms");
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
