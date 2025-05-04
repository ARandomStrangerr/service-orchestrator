package product.chain;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;

import product.communication.SocketNIO;
import product.communication.SocketNIO.SocketEvent;

public class LinkStartSocket extends Link{
	public LinkStartSocket(Chain owner){
		super(owner);
	}

	public boolean exec() {
		Object mutex = new Object();
		ChainHandleSocketRequest handleRequest = new ChainHandleSocketRequest();
		ChainVerifyClientSocket verifyClient = new ChainVerifyClientSocket();
		SocketNIO soc;
		try {
			soc =  new SocketNIO(super.owner.getProcessObject().get("port").getAsInt());
		} catch (IOException e) {
			return false;
		}
		ExecutorService threadPool = Executors.newFixedThreadPool(super.owner.getProcessObject().get("threadPool").getAsInt()); //todo: keep the thread to be dynamical
		new Thread(() -> {
			while (true) {
				Set<SelectionKey> keys;
				try {
					keys = soc.getReadySockets();
				} catch (IOException e) {
					continue;
				}
				for (SelectionKey key : keys){
					threadPool.submit(() -> {
						switch (soc.getEventType(key)){
							case SocketEvent.ACCEPT:
								try{
									soc.accept(key);
								} catch (IOException e){
									return;
								}
								try {
									JsonObject processObj = soc.read(key);
									verifyClient.exec(processObj);
								} catch (IOException e) { 
									return;
								}
								break;
							case SocketEvent.DATA:
								try{
									JsonObject processObj = soc.read(key);
									handleRequest.exec(processObj);
								} catch (IOException e){
									return;
								}
								break;
							case SocketEvent.CLOSED:
								break;
							default:
								return;
						}
						// actually process the JSON Object here
					});
				}
				keys.clear();
			}
		}).start();	
		return true;
	}
}
