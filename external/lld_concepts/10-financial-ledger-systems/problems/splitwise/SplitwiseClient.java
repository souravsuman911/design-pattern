import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class Account {
    String id;
    long balance;
    ReentrantLock lock = new ReentrantLock();

    Account(String id, long balance) {
        this.id = id;
        this.balance = balance;
    }
}

class LedgerService {
    Map<String, Account> accounts = new ConcurrentHashMap<>();
    Set<String> idempotency = ConcurrentHashMap.newKeySet();

    void add(Account a) {
        accounts.put(a.id, a);
    }

    boolean transfer(String fromId, String toId, long amount, String key) {
        if (!idempotency.add(key))
            return true;
        Account from = accounts.get(fromId), to = accounts.get(toId);
        Account first = from.id.compareTo(to.id) < 0 ? from : to, second = first == from ? to : from;
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (from.balance < amount)
                    return false;
                from.balance -= amount;
                to.balance += amount;
                return true;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
}

public class SplitwiseClient {
    public static void main(String[] args) {
        LedgerService service = new LedgerService();
        service.add(new Account("A", 10000));
        service.add(new Account("B", 0));
        System.out.println(service.transfer("A", "B", 1000, "txn-1"));
    }
}
