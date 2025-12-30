package internal.designPattern.abstractFactoryPattern;

public class WindowsButton implements Button{
    @Override
    public void paint() {
        System.out.println("Windows button paints");
    }
}
