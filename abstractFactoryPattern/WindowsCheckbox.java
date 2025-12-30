package internal.designPattern.abstractFactoryPattern;

public class WindowsCheckbox implements Checkbox{
    @Override
    public void check() {
        System.out.println("Windows checkbox checks");
    }
}
