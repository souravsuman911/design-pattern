import java.util.*;import java.util.concurrent.*;import java.util.concurrent.locks.*;
enum Status{CREATED,ACTIVE,ASSIGNED,COMPLETED,CANCELLED,FAILED}
class DomainEntity{String id=UUID.randomUUID().toString();String name;Status status=Status.CREATED;DomainEntity(String name){this.name=name;}}
interface SelectionStrategy{DomainEntity select(Collection<DomainEntity> items);}
class FirstAvailableStrategy implements SelectionStrategy{public DomainEntity select(Collection<DomainEntity> items){return items.stream().filter(i->i.status==Status.ACTIVE).findFirst().orElseThrow();}}
class LldService{Map<String,DomainEntity> store=new ConcurrentHashMap<>();ReentrantLock lock=new ReentrantLock();SelectionStrategy strategy=new FirstAvailableStrategy();void add(DomainEntity e){e.status=Status.ACTIVE;store.put(e.id,e);}DomainEntity assign(){lock.lock();try{DomainEntity e=strategy.select(store.values());e.status=Status.ASSIGNED;return e;}finally{lock.unlock();}}void complete(String id){lock.lock();try{store.get(id).status=Status.COMPLETED;}finally{lock.unlock();}}}
public class CabSearchClient{public static void main(String[]args){LldService service=new LldService();service.add(new DomainEntity("Cab Search"));DomainEntity entity=service.assign();service.complete(entity.id);System.out.println(entity.name+" -> "+entity.status);}}
