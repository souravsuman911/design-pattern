package internal.designPattern.external.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

interface IEmployee {
    long getId();
    String getName();
    String getRole();
    double getSalary();

    default void getAllReports(int indent){
        throw new UnsupportedOperationException();
    }

    default void getDirectReports(int indent){
        throw new UnsupportedOperationException();
    }

    default boolean isManager(){
        return false;
    }
}

class Employee implements IEmployee{

    Long id;
    String name;
    String role;
    Double salary;

    public Employee(Long id, String name, String role, Double salary){
        this.id = id;
        this.name = name;
        this.role = role;
        this.salary = salary;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public double getSalary() {
        return salary;
    }

    @Override
    public void getAllReports(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + getName());
    }

    @Override
    public void getDirectReports(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + getName());
    }

    @Override
    public String toString(){
        return "{ " + id + ", " + name + ", " + role + ", " + salary + " }";
    }
}

class EmployeeManager implements IEmployee{

    Long id;
    String name;
    String role;
    Double salary;
    List<IEmployee> employees;

    public EmployeeManager(Long id, String name, String role, Double salary){
        this.id = id;
        this.name = name;
        this.role = role;
        this.salary = salary;
        employees = new ArrayList<>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public double getSalary() {
        return salary;
    }

    @Override
    public void getAllReports(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + getName());
        for(IEmployee employee : employees) {
            employee.getAllReports(indent + 4);
        }
    }

    @Override
    public void getDirectReports(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(getName());
        for(IEmployee employee : employees){
            System.out.println(indentSpace + employee.getName());
        }
    }

    @Override
    public boolean isManager(){
        return true;
    }

    public int getDirectReportCount(){
        return employees.size();
    }

    @Override
    public String toString(){
        return "{ " + id + ", " + name + ", " + role + ", " + salary + " }";
    }
}

public class EmployeeManagement {
    public IEmployee getFirstLineManager(IEmployee employee, List<String> names, Map<String, Boolean> isFound){

        if(names.contains(employee.getName())){
            return employee;
        }

        if(!employee.isManager()){
            return null;
        }

        EmployeeManager manager = (EmployeeManager) employee;
        for(IEmployee report : manager.employees){
            if(report.isManager()){
                IEmployee found = getFirstLineManager(report, names, isFound);
                if(found != null){
                    return found;
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {

    }
}
