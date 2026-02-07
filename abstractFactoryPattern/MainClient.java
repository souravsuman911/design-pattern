package internal.designPattern.abstractFactoryPattern;

interface Button {
    void paint();
}

interface Checkbox {
    void check();
}

class MacButton implements Button{
    @Override
    public void paint() {
        System.out.println("Mac button paints");
    }
}

class MacCheckbox implements Checkbox{
    @Override
    public void check() {
        System.out.println("Mac checkbox checks");
    }
}

class WindowsButton implements Button{
    @Override
    public void paint() {
        System.out.println("Windows button paints");
    }
}

class WindowsCheckbox implements Checkbox{
    @Override
    public void check() {
        System.out.println("Windows checkbox checks");
    }
}

interface GuiFactory {
    Button createButton();
    Checkbox createCheckbox();
}

class MacFactory implements GuiFactory{
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

class WindowsFactory implements GuiFactory{
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

public class MainClient {
    public static void main(String[] args) {
        
    }
}
