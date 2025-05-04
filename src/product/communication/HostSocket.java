package product.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @deprecated Replaced by {@link LinkStartSocketsNIO} which uses NIO-based non-blocking I/O
 * for scalable socket management.
 */
@Deprecated
public class HostSocket {
	private final ServerSocket soc;

	public HostSocket(int port) throws IOException{
		soc = new ServerSocket(port);
	}
	
	public Socket accept() throws IOException {
		return soc.accept();
	}
}
