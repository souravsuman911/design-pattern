package internal.designPattern.factoryPattern;

import internal.designPattern.factoryPattern.employee.CDeveloper;
import internal.designPattern.factoryPattern.employee.JavaDeveloper;
import internal.designPattern.factoryPattern.employee.PythonDeveloper;

public class EmployeeFactory {
    public static Employee getEmployee(EmployeeType empType){
        String empTypeDesc = empType.getDescription();
        switch (empTypeDesc){
            case "JD" : return new JavaDeveloper();
            case "PD" : return new PythonDeveloper();
            case "CD" : return new CDeveloper();
            default: throw new IllegalArgumentException();
        }
    }

}
