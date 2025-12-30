package internal.designPattern.singletonPattern.eagerloading;

public class Employee {

    private static final Employee emp = new Employee();

    private Employee(){

    }

    public Employee getInstance(){
        return emp;
    }

}
