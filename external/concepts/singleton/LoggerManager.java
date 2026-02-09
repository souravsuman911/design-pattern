package internal.designPattern.external.concepts.singleton;

// Double check locking pattern
public class LoggerManager {
    private static volatile LoggerManager singletonObj;

    private LoggerManager(){
    }

    public static LoggerManager getInstance(){
        if(singletonObj == null){
            synchronized (LoggerManager.class){
                if(singletonObj == null){
                    singletonObj = new LoggerManager();
                }
            }
        }

        return singletonObj;
    }
}
class Client3 {
    public static void main(String[] args) {
        LoggerManager loggerManager = LoggerManager.getInstance();
    }
}
