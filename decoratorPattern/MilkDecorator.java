package internal.designPattern.decoratorPattern;

public class MilkDecorator extends CoffeeDecorator{
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
