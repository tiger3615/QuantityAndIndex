package sf.ibu.qni.core.server;

import java.net.Socket;

import com.alibaba.fastjson.JSONObject;

import sf.ibu.qni.common.Util;

public class ServerHandler {

	public void handle(String message, Socket socket) throws Exception {
		/*
		 * message tpl { "type":"register", "info":{ "IP":"127.0.0.1", "port":2000,
		 * "filePaths":["path1","path2"] } }
		 */
		JSONObject json = JSONObject.parseObject(message);
		String type = json.getString("type");
		if ("register".equalsIgnoreCase(type)) {
			register(json.getJSONObject("info"));
		}  else if ("getAllServers".equalsIgnoreCase(type)) {
			Util.write(socket, ServerCollection.getAllServers().toString());
		}
	}

	public void register(JSONObject json) {
		ServerCollection.put(json);
	}


}
