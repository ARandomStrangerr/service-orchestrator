package	product.chain;

import java.util.LinkedList;

import com.google.gson.JsonObject;

public abstract class Chain {
	private JsonObject processObject;
	private boolean endEarly = false;
	protected final LinkedList<Link> links = new LinkedList<>();

	public boolean exec(JsonObject processOjbeObject) {
		this.processObject = processOjbeObject;
		for (Link link : links){
			if (!link.exec()) return false;
			if (this.endEarly) break;
		}
		return true;
	}

	protected JsonObject getProcessObject(){
		return this.processObject;
	}

	public void endEarly() {
		this.endEarly = true;
	}
}
