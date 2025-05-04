package product.chain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.JsonObject;

public abstract class Link {
	public static final ConcurrentHashMap<Long, Link> pausedLink = new ConcurrentHashMap<>();
	private static final AtomicLong counter = new AtomicLong();

	protected final Chain owner;
	private CountDownLatch latch;

	public Link(Chain owner) {
		this.owner = owner;
	}

	public abstract boolean exec();

	public void pause() throws InterruptedException {
		latch = new CountDownLatch(1);
		pausedLink.put(Link.counter.incrementAndGet(), this);
		latch.await();
	}

	public static void resume(Long id, JsonObject additionalInfo) {
		Link pausedInstance = pausedLink.remove(id);
		pausedInstance.owner.getProcessObject().add("additionalInfo", additionalInfo);
		pausedInstance.latch.countDown();
	}
}
