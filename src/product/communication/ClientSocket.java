package product.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.JsonObject;

/**
 * @deprecated Replaced by {@link LinkStartSocketsNIO} which uses NIO-based non-blocking I/O
 * for scalable socket management.
 */
@Deprecated
public class ClientSocket {
	private final Socket soc;
	private final BufferedReader br;
	private final BufferedWriter bw;

	public ClientSocket (Socket soc) throws IOException{
		this.soc = soc;
		this.br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
	}

	public String read() throws IOException {
		return br.readLine();
	}

	public void write(String line) throws IOException {
		bw.write(line);
		bw.flush();
	}

	public void write(StringBuilder line) throws IOException {
		this.write(line.toString());
	}

	public void write(JsonObject obj) throws IOException {
		this.write(obj.toString());
	}
}
