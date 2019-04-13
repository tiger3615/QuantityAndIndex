package trieyes.qni.core.server;

import java.net.Socket;

import com.alibaba.fastjson.JSONObject;

import trieyes.qni.common.ServerHandlerI;
import trieyes.qni.common.Util;

public class ServerHandlerImpl implements ServerHandlerI{

	public void handle(String message, Socket socket) throws Throwable {
		/*
		 * message tpl { "type":"register", "info":{ "IP":"127.0.0.1", "port":2000 } }
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
