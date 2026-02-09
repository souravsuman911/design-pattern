package internal.designPattern.external.concepts.singleton;

// lazy loading
public class EmployeeManager {
    private static volatile EmployeeManager singletonObj;

    private EmployeeManager(){
    }

    public static EmployeeManager getInstance(){
        if(singletonObj == null){
            singletonObj = new EmployeeManager();
        }
        return singletonObj;
    }
}

class Client{
    public static void main(String[] args) {
        EmployeeManager emp = EmployeeManager.getInstance();
    }
}
