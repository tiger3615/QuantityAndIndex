package trieyes.qni.common;

import java.net.Socket;

public interface ServerHandlerI {
	public void handle(String message, Socket socket) throws Throwable;
}
