package internal.designPattern.external.concepts.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

// Example to keep Singleton non-breakable using reflection
public class StoreManager {
    private static volatile StoreManager singletonObj;
    private static boolean isCreated = false; // helps in preventing reflection-based breaking (not foolproof)

    private StoreManager(){
        // helps in preventing reflection-based breaking (not foolproof)
        if(isCreated){
            throw new RuntimeException("Use getInstance method");
        }
        isCreated = true;
    }

    public static StoreManager getInstance(){
        if(singletonObj == null){
            synchronized (StoreManager.class){
                if(singletonObj == null){
                    singletonObj = new StoreManager();
                }
            }
        }

        return singletonObj;
    }
}

class Client4 {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        StoreManager storeManager1 = StoreManager.getInstance();
        Constructor<StoreManager> constructor = StoreManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        StoreManager storeManager4 = constructor.newInstance(); // will throw RuntimeException defined in constructor

        // helps in breaking singleton using extreme reflection practice
        Field[] declaredFields =  StoreManager.class.getDeclaredFields();
        for(Field field : declaredFields){
            if(field.getName().equals("isCreated")){
                field.setAccessible(true);
                field.set(storeManager1, false);
            }
        }
        StoreManager storeManager2 = constructor.newInstance();
        StoreManager storeManager3 = StoreManager.getInstance();

        System.out.println(storeManager1 == storeManager2); // false
        System.out.println(storeManager1 == storeManager3); // true

    }
}
