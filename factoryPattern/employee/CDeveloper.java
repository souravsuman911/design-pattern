package internal.designPattern.factoryPattern.employee;

import internal.designPattern.factoryPattern.Employee;

public class CDeveloper implements Employee {
    @Override
    public double getSalary() {
        System.out.print("C Developer Salary : ");
        return 7000;
    }
}
