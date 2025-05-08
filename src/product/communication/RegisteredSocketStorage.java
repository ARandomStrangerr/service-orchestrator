package product.communication;

import java.nio.channels.SelectionKey;
import java.util.EmptyStackException;
import java.util.LinkedList;

class Node {
    int key;
    LinkedList<SelectionKey> value;
    Node left, right, parent;

    Node(int key, Node parent) {
        this.key = key;
        this.value = new LinkedList<>();
        this.parent = parent;
    }
}

public class RegisteredSocketStorage {
    private Node head = null;

    public synchronized void insert(SelectionKey key, int activeRequest) {
        head = insertRecursive(head, key, activeRequest, null);
    }

    private Node insertRecursive(Node node, SelectionKey key, int activeRequest, Node parent) {
        if (node == null) {
            Node newNode = new Node(activeRequest, parent);
            newNode.value.add(key);
            return newNode;
        }
        if (activeRequest < node.key) {
            node.left = insertRecursive(node.left, key, activeRequest, node);
        } else if (activeRequest > node.key) {
            node.right = insertRecursive(node.right, key, activeRequest, node);
        } else {
            node.value.add(key);
        }
        return node;
    }

    public synchronized SelectionKey getMin() {
        if (head == null) throw new EmptyStackException();
        Node node = head;
        while (node.left != null) node = node.left;

        while (node != null && node.value.isEmpty()) {
            node = successor(node);
        }
        if (node == null) throw new EmptyStackException();

        return node.value.poll();
    }

    public synchronized void remove(SelectionKey key) {
        removeRecursive(head, key);
    }

    private void removeRecursive(Node node, SelectionKey key) {
        if (node == null) return;

        node.value.remove(key);
        removeRecursive(node.left, key);
        removeRecursive(node.right, key);
    }

    private Node successor(Node node) {
        if (node.right != null) {
            node = node.right;
            while (node.left != null) node = node.left;
            return node;
        }
        Node parent = node.parent;
        while (parent != null && node == parent.right) {
            node = parent;
            parent = parent.parent;
        }
        return parent;
    }
}

