package internal.designPattern.external.lld.cache;

import java.util.HashMap;
import java.util.Map;

class LRUCacheImpl {

    class Node {
        int key;
        int value;
        Node next;
        Node prev;

        Node(int key, int value){
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    Map<Integer, Node> map;
    Node head;
    Node tail;
    int capacity;

    public LRUCacheImpl(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if(map.containsKey(key)){
            Node node = map.get(key);
            deleteNode(node);
            addNode(node);
            return node.value;
        }
        else{
            return -1;
        }
    }

    public void put(int key, int value) {
        Node newNode = new Node(key, value);

        if(map.containsKey(key)){
            deleteNode(map.get(key));
            addNode(newNode);
            map.put(key, newNode);
        }
        else{
            if(map.size() == capacity){
                map.remove(tail.prev.key);
                deleteNode(tail.prev);
            }

            map.put(key, newNode);
            addNode(newNode);
        }
    }

    public void addNode(Node newNode){
        Node nextNode = head.next;
        head.next = newNode;
        newNode.prev = head;
        newNode.next = nextNode;
        nextNode.prev = newNode;
    }

    public void deleteNode(Node oldNode){
        Node prevNode = oldNode.prev;
        Node nextNode = oldNode.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    public static void main(String[] args) {

    }
}

