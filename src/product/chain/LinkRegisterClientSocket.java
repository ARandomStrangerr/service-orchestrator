package product.chain;

public class LinkRegisterClientSocket extends Link {
	public LinkRegisterClientSocket(Chain owner){
		super(owner);
	}

	public boolean exec() {
		return true;
	}
}
