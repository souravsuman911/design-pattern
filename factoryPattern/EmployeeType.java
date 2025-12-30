package internal.designPattern.factoryPattern;

public enum EmployeeType {
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
