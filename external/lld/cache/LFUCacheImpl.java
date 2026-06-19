package internal.designPattern.external.lld.cache;

import java.util.HashMap;
import java.util.Map;

class LFUCacheImpl {
    class Node {
        int key, val;
        Node prev, next;

        Node(int k, int v) {
            key = k;
            val = v;
        }
    }

    class DLL {
        Node head, tail;

        DLL() {
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
        }

        void add(Node node) {
            Node next = head.next;

            head.next = node;
            node.prev = head;

            node.next = next;
            next.prev = node;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        Node removeLast() {
            if (head.next == tail) return null;

            Node node = tail.prev;
            remove(node);
            return node;
        }

        boolean isEmpty() {
            return head.next == tail;
        }
    }

    Map<Integer, Node> valueMap = new HashMap<>();
    Map<Integer, Integer> freqMap = new HashMap<>();
    Map<Integer, DLL> listMap = new HashMap<>();

    int capacity, minFreq = 0;

    public LFUCacheImpl(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (!valueMap.containsKey(key)) return -1;

        Node node = valueMap.get(key);
        update(key, node.val);
        return node.val;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        if (valueMap.containsKey(key)) {
            update(key, value);
            return;
        }

        if (valueMap.size() == capacity) {
            DLL minList = listMap.get(minFreq);
            Node removed = minList.removeLast();

            valueMap.remove(removed.key);
            freqMap.remove(removed.key);
        }

        Node node = new Node(key, value);
        valueMap.put(key, node);
        freqMap.put(key, 1);

        listMap.putIfAbsent(1, new DLL());
        listMap.get(1).add(node);

        minFreq = 1;
    }

    private void update(int key, int value) {
        Node node = valueMap.get(key);
        int freq = freqMap.get(key);

        DLL currList = listMap.get(freq);
        currList.remove(node);

        if (freq == minFreq && currList.isEmpty()) {
            minFreq++;
        }

        node.val = value;
        freqMap.put(key, freq + 1);

        listMap.putIfAbsent(freq + 1, new DLL());
        listMap.get(freq + 1).add(node);
    }

}

