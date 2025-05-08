package product.chain;

import com.google.gson.JsonObject;

public class ChainStartModule extends Chain{
	public ChainStartModule(JsonObject obj) {
		super.links.add(new LinkStartSocket(this));
	}
}
