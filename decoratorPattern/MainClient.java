package internal.designPattern.decoratorPattern;

interface Coffee {
    Double price();
    String description();
}

class SimpleCoffee implements Coffee{
    @Override
    public Double price() {
        return 50.0;
    }

    @Override
    public String description() {
        return "Simple Coffee";
    }
}

abstract class CoffeeDecorator implements Coffee{
    protected Coffee coffee;
    public CoffeeDecorator(Coffee coffee){
        this.coffee = coffee;
    }
}

class MilkDecorator extends CoffeeDecorator{
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public Double price() {
        return coffee.price() + 30;
    }

    @Override
    public String description() {
        return coffee.description() + ", with added Milk";
    }
}

class SugarDecorator extends CoffeeDecorator{
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public Double price() {
        return coffee.price() + 10;
    }

    @Override
    public String description() {
        return coffee.description() + ", with added Sugar";
    }
}

public class MainClient {

    public static void main(String[] args) {
        Coffee coffee1 = new SimpleCoffee();
        Coffee coffee2 = new SugarDecorator(new SimpleCoffee());
        Coffee coffee3 = new MilkDecorator(new SugarDecorator(new MilkDecorator(new SimpleCoffee())));

        System.out.println(coffee1.description() + " " + coffee1.price());
        System.out.println(coffee2.description() + " " + coffee2.price());
        System.out.println(coffee3.description() + " " + coffee3.price());
    }


}
