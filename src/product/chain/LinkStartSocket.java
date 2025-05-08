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
								break;
							case SocketEvent.DATA:
								JsonObject processObj;
								try{
									processObj = soc.read(key);
								} catch (IOException e){
									return;
								}
								if (SocketNIO.isUnregisteredSocket(key)) {
									SocketNIO.removeUnregisteredSocket(key);
								} else {
									new ChainHandleSocketRequest().exec(processObj);
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
