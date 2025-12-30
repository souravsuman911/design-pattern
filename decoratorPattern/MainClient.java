package internal.designPattern.decoratorPattern;

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
