package internal.designPattern.factoryPattern.employee;

import internal.designPattern.factoryPattern.Employee;

public class JavaDeveloper implements Employee {

    @Override
    public double getSalary() {
        System.out.print("Java Developer Salary : ");
        return 5000;
    }
}
