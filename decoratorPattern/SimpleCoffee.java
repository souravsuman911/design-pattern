package internal.designPattern.decoratorPattern;

public class SimpleCoffee implements Coffee{

    @Override
    public Double price() {
        return 50.0;
    }

    @Override
    public String description() {
        return "Simple Coffee";
    }
}
