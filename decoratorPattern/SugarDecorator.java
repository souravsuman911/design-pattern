package internal.designPattern.decoratorPattern;

public class SugarDecorator extends CoffeeDecorator{
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
