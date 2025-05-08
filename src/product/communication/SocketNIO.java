package product.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SocketNIO {
	public static final LinkedList<SelectionKey> unregistered = new LinkedList<>();
	public static final Object mutex = new Object();
	public static void addUnregisteredSocket(SelectionKey key) {
		synchronized(mutex) {
			unregistered.add(key);
		}
	}
	public static void removeUnregisteredSocket(SelectionKey key) {
		synchronized(mutex) {
			unregistered.remove(key);
		}
	}
	public static boolean isUnregisteredSocket(SelectionKey key) {
		synchronized(mutex) {
			return unregistered.contains(key);
		}
	}

	public enum SocketEvent {ACCEPT, DATA, CLOSED}
	private final Selector selector;
	private final ServerSocketChannel serverChan;
	private final ByteBuffer buffer = ByteBuffer.allocate((int) 2e10);

	public SocketNIO(int port) throws IOException {
		selector = Selector.open();
		serverChan = ServerSocketChannel.open();
		serverChan.bind(new InetSocketAddress(port));
		serverChan.configureBlocking(false);
		serverChan.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public Set<SelectionKey> getReadySockets() throws IOException{
		selector.select();
		return selector.selectedKeys();
	}

	public SocketEvent getEventType(SelectionKey key){
		if (key.isAcceptable()) return SocketEvent.ACCEPT;
		else if (key.isReadable()) return SocketEvent.DATA;
		else return null;
	}

	public void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel clientChannel = serverChannel.accept();
		clientChannel.configureBlocking(false);
		clientChannel.register(selector, SelectionKey.OP_READ);
	}

	public JsonObject read(SelectionKey key) throws IOException {
		StringBuilder sb = new StringBuilder();
		SocketChannel clientChannel = (SocketChannel) key.channel();
		while (true) {
			int readByte = clientChannel.read(buffer);
			if (readByte == -1) {
				clientChannel.close();
				break;
			}
			buffer.flip();
			sb.append(StandardCharsets.UTF_8.decode(buffer));
			buffer.clear();
			if (sb.length() > 0 && sb.charAt(sb.length() - 2) == '\r' && sb.charAt(sb.length() - 1) == '\n') break;
		}
		JsonObject jsonObject = JsonParser.parseString(sb.toString().trim()).getAsJsonObject();
		return jsonObject;
	}
}
