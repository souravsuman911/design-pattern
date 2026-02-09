package internal.designPattern.external.concepts.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

enum Department {
    IT('I'),
    DEVOPS('O'),
    DEV('D');

    private char code;

    Department(char code) {
        this.code = code;
    }

    public char getCode(){
        return code;
    }
}
class Employee {
    long id;
    String name;
    Double salary;
    Department dept;

    Employee(long id, String name, Double salary, Department dept){
        this.id =  id;
        this.name = name;
        this.salary = salary;
        this.dept = dept;
    }

    long getId(){
        return id;
    }

    Double getSalary(){
        return salary;
    }

    String getName() {
        return name;
    }

    Department getDept(){
        return dept;
    }

}
public class StreamPractice2 {

    public void printEmpName(List<Employee> employees){
        employees.forEach(e -> System.out.println(e.name));
    }

    public static void printDetails(List<Employee> employees){
        employees.forEach(e -> {
            System.out.print(e.id + " ");
            System.out.print(e.name + " ");
            System.out.print(e.salary + " ");
            System.out.println(e.dept);
        });
    }

    public static void printEmp(Employee e){
        System.out.print(e.id + " ");
        System.out.print(e.name + " ");
        System.out.print(e.salary + " ");
        System.out.println(e.dept);
    }

    public static void main(String[] args) {
        Employee e1 = new Employee(101, "Emp1", 10000.00, Department.IT);
        Employee e2 = new Employee(102, "Emp2", 12000.00, Department.DEVOPS);
        Employee e3 = new Employee(103, "Emp3", 11000.00, Department.DEV);
        Employee e4 = new Employee(104, "Emp4", 10000.00, Department.IT);
        Employee e5 = new Employee(105, "Emp5", 13000.00, Department.IT);
        Employee e6 = new Employee(106, "Emp6", 15000.00, Department.DEV);
        Employee e7 = new Employee(107, "Emp7", 10000.00, Department.IT);

        List<Employee> employees = Arrays.asList(e1, e2, e3, e4, e5, e6, e7);
        StreamPractice2.printDetails(employees);

        // From a list of employees, filter employees whose salary is greater than 10,000.
        List<String> eGreaterThan10K = employees.stream()
                .filter(e -> e.getSalary() > 10000)
                .map(Employee::getName)
                .collect(Collectors.toList());

        System.out.println(eGreaterThan10K);

        // Find the employee with the highest salary using streams.
        Optional<Employee> highestSalary = employees.stream()
                .max(Comparator.comparing(Employee::getSalary));

        highestSalary.ifPresent(StreamPractice2::printEmp);

        // Calculate the sum of all employee salaries.
        Double sumOfSalary = employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();

        System.out.println(sumOfSalary);

        // Group employees by department
        Map<Department, List<Employee>> groupedByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept));

        for(Map.Entry<Department, List<Employee>> entry : groupedByDept.entrySet()){
            System.out.print(entry.getKey() + " : ");
            for(Employee emp : entry.getValue()){
                System.out.print(emp.getName() + " ");
            }
            System.out.println();
        }

        // Count number of employees in each department
        Map<Department, Long> deptEmpCount = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()));

        System.out.println(deptEmpCount.get(Department.IT));
        System.out.println(deptEmpCount.get(Department.DEV));
        System.out.println(deptEmpCount.get(Department.DEVOPS));

        // Convert List<Employee> → Map<id, name>
        Map<Long, String> idMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getName));

        System.out.println(idMap.get(103L));





    }
}
