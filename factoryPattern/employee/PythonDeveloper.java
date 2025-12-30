package internal.designPattern.factoryPattern.employee;

import internal.designPattern.factoryPattern.Employee;

public class PythonDeveloper implements Employee {

    @Override
    public double getSalary() {
        System.out.print("Python Developer Salary : ");
        return 4000;
    }
}
