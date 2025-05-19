package product.communication;

import java.nio.channels.SelectionKey;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;

class Node {
	int activeRequests;
	SelectionKey connection;

  Node(int activeRequests, SelectionKey connection) {
		this.activeRequests = activeRequests;
		this.connection = connection;
	}
}

public class RegisteredSocketStorage {
	private static RegisteredSocketStorage instance = null;

	public static RegisteredSocketStorage getInstance () {
		if (instance == null) instance = new RegisteredSocketStorage();
		return instance;
	}

	private final HashMap<String, LinkedList<Node>> storage;

	public RegisteredSocketStorage(){
		storage = new HashMap<>();
	}

	public void add(String serviceName, SelectionKey connection) {
		LinkedList<Node> serviceList = null;
		Node node = new Node(0, connection);
		if (storage.containsKey(serviceName)) { // case when there is no service under name yet
			serviceList = new LinkedList<>();
			storage.put(serviceName, serviceList);
		} else {
		 	serviceList = storage.get(serviceName);
		}
		serviceList.push(node);
	}
	
	public SelectionKey get(String serviceName) throws EmptyStackException {
		if (!storage.containsKey(serviceName)) throw new EmptyStackException();
		LinkedList<Node> serviceList = storage.get(serviceName);
		Node node = serviceList.pop();
		node.activeRequests++;
		if (serviceList.size() == 0) serviceList.push(node);
		else {
			int pos = 0;
			for (Node n : serviceList) {
				if (n.activeRequests >= node.activeRequests) break;
				pos++;
			}
			serviceList.add(pos, node);
		}
		return node.connection;
	}

	public void giveBack(String serviceName, SelectionKey connection){	
		if (!storage.containsKey(serviceName)) throw new EmptyStackException();
		LinkedList<Node> serviceList = storage.get(serviceName);
		Node node = serviceList.remove(pos(serviceList, connection));
		node.activeRequests--;
		int pos = 0;
		for (Node n : serviceList) {
			if (n.activeRequests >= node.activeRequests) break;
			pos++;
		}
		serviceList.add(pos, node);
	}

	public void remove(String serviceName, SelectionKey connection) {
		if (!storage.containsKey(serviceName)) throw new EmptyStackException();
		LinkedList<Node> serviceList = storage.get(serviceName);
		serviceList.remove(pos(serviceList, connection));
	}

	private int pos(LinkedList<Node> serviceList, SelectionKey connection) {
		int pos = 0;
		for (Node node : serviceList){
			if (node.connection.equals(connection)) break;
			pos++;
		}
		return pos;
	}
}

