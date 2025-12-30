package internal.designPattern.singletonPattern.lazyloading;

public class Employee {
    private static volatile Employee emp;

    private Employee(){
    }

    public Employee getInstance(){
        if(emp == null){
            emp = new Employee();
        }

        return emp;
    }

}
