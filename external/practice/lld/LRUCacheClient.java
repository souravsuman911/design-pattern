package internal.designPattern.external.practice.lld;

import java.util.HashMap;
import java.util.Map;

interface ILRUCache {
    int get(int key) throws InterruptedException;
    void put(int key, int value);
}

class Node {
    int key;
    int value;
    Node next;
    Node prev;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    @Override
    public String toString(){
        return " { " + key + ", " + value + " }";
    }
}

class LRUCache implements ILRUCache {

    private int cap; // capacity
    private Map<Integer, Node> map;
    private Node head;
    private Node tail;
    private static volatile LRUCache lruCache;

    private LRUCache(int cap) {
        this.cap = cap;
        this.map = new HashMap<>();
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    public static LRUCache getInstance(int cap){
        if(lruCache != null && lruCache.cap != cap){
            throw new IllegalStateException("LRU cache already initialized");
        }
        if(lruCache == null){
            synchronized (LRUCache.class){
                if(lruCache == null){
                    lruCache = new LRUCache(cap);
                }
            }
        }

        return lruCache;
    }

    @Override
    public synchronized int get(int key) throws InterruptedException {
        if(!map.containsKey(key)){
           return -1;
        }

        Node newNode = map.get(key);
        removeNode(newNode);
        addNode(newNode);

        System.out.println("Current Thread : " + Thread.currentThread().getName());
        System.out.println("Current LRU Cache status : " + map);

        return newNode.value;
    }

    @Override
    public synchronized void put(int key, int value) {
        Node newNode = null;
        if(map.containsKey(key)){
            Node node = map.get(key);
            node.value = value;
            newNode = node;
            removeNode(map.get(key));
        }

        newNode  = newNode == null ? new Node(key, value) : newNode;
        addNode(newNode);
        map.put(key, newNode);

        if(map.size() > cap){
            Node toBeDeleted = tail.prev;
            removeNode(toBeDeleted);
            map.remove(toBeDeleted.key);
        }

        System.out.println("Current Thread : " + Thread.currentThread().getName());
        System.out.println("Current LRU Cache status : " + map);
    }

    private void addNode(Node newNode) {
        Node nextNode = head.next;
        head.next = newNode;
        newNode.prev = head;
        newNode.next = nextNode;
        nextNode.prev = newNode;
    }

    private void removeNode(Node delNode) {
        if(delNode == head || delNode == null || delNode.prev == null || delNode.next == null){
            return;
        }
        Node prevNode = delNode.prev;
        Node nextNode = delNode.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }
}

class GetWorker implements Runnable {

    private final LRUCache lruCache;
    private final int key;

    public GetWorker(LRUCache lruCache, int key) {
        this.lruCache = lruCache;
        this.key = key;
    }

    @Override
    public void run() {
        try {
            lruCache.get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class PutWorker implements Runnable {

    private final LRUCache lruCache;
    private final int key;
    private final int value;

    public PutWorker(LRUCache lruCache, int key, int value) {
        this.lruCache = lruCache;
        this.key = key;
        this.value = value;
    }

    @Override
    public void run() {
        lruCache.put(key, value);
    }
}
public class LRUCacheClient {
    public static void main(String[] args) {
        LRUCache lruCache = LRUCache.getInstance(5);
        lruCache.put(1, 11);
        lruCache.put(2, 12);
        lruCache.put(3, 13);
        lruCache.put(4, 14);

        Thread t1 = new Thread(new GetWorker(lruCache, 2), "Thread1 GET");
        Thread t2 = new Thread(new PutWorker(lruCache, 5, 15), "Thread2 PUT");
        t1.start();
        t2.start();


    }

}
