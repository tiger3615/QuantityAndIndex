package sf.ibu.qni.core.local;

import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import sf.ibu.qni.common.MySocketClient;
import sf.ibu.qni.core.Conf;
import sf.ibu.qni.core.server.ServerScanner;

public class LocalHandler {
	public HashMap<String,Integer> getIndexAndServerTotal(String ip) throws Exception {
		HashMap<String,Integer> indexAndTotal = new HashMap<String, Integer>();
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
		JSONArray serversJarr = JSONArray.parseArray(allServers);
		// get Index And Server Total
		if(serversJarr.size()>0) {
			int index = getSelfIndex(serversJarr,ip);
			indexAndTotal.put("index", index);
			indexAndTotal.put("total", serversJarr.size());
		}
		mySocketClient.shutdown();
		return indexAndTotal;
	}

	public int getSelfIndex(JSONArray serversJarr,String ip) {
		for (int i = 0; i < serversJarr.size(); i++) {
			JSONObject oneServerInfo = serversJarr.getJSONObject(i);
			if (ip.equals(oneServerInfo.getString("IP"))) {
				return i;
			}
		}
		return 0;
	}

}
