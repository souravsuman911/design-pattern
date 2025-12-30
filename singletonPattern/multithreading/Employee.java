package internal.designPattern.singletonPattern.multithreading;

public class Employee {

    private static volatile Employee emp;

    private Employee(){

    }

    // this will still not work as 2nd thread will create the object
    public Employee getInstance(){
        if(emp == null){
            synchronized (Employee.class){ // <- taken lock at this point by the 2nd thread
                emp = new Employee();
            }
        }
        return emp;
    }

    public Employee getInstance2(){
        if(emp == null){
            synchronized (Employee.class){
                if(emp == null){
                    emp = new Employee();
                }
            }
        }

        return emp;
    }

}
