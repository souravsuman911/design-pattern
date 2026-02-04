package internal.designPattern.factoryPattern;

interface Employee {
    double getSalary();
}

class CDeveloper implements Employee {
    @Override
    public double getSalary() {
        System.out.print("C Developer Salary : ");
        return 7000;
    }
}

class JavaDeveloper implements Employee {
    @Override
    public double getSalary() {
        System.out.print("Java Developer Salary : ");
        return 5000;
    }
}

class PythonDeveloper implements Employee {
    @Override
    public double getSalary() {
        System.out.print("Python Developer Salary : ");
        return 4000;
    }
}

class EmployeeFactory {
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

enum EmployeeType {
    JAVA_DEVELOPER("JD"),
    PYTHON_DEVELOPER("PD"),
    C_DEVELOPER("CD");

    private final String description;
    EmployeeType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}



public class MainClient {
    public static void main(String[] args) {
        System.out.println(EmployeeFactory.getEmployee(EmployeeType.JAVA_DEVELOPER).getSalary());
        System.out.println(EmployeeFactory.getEmployee(EmployeeType.PYTHON_DEVELOPER).getSalary());
        System.out.println(EmployeeFactory.getEmployee(EmployeeType.C_DEVELOPER).getSalary());
    }
}
