package internal.designPattern.factoryPattern;

public class MainClient {
    public static void main(String[] args) {
//        Employee javaDev = new JavaDeveloper();
//        System.out.println(javaDev.getSalary());
//
//        Employee pythonDev = new PythonDeveloper();
//        System.out.println(pythonDev.getSalary());

//        System.out.println("Enter employee type : ");
//        Scanner sc = new Scanner(System.in);
//        String empType = sc.next();

        System.out.println(EmployeeFactory.getEmployee(EmployeeType.JAVA_DEVELOPER).getSalary());
        System.out.println(EmployeeFactory.getEmployee(EmployeeType.PYTHON_DEVELOPER).getSalary());
        System.out.println(EmployeeFactory.getEmployee(EmployeeType.C_DEVELOPER).getSalary());
    }
}
