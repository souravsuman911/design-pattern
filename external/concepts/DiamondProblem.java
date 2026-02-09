package internal.designPattern.external.concepts;

interface A {
    default void hello(){
        System.out.println("Hello A");
    }
}

interface B extends A {
    @Override
    default void hello(){
        System.out.println("Hello B");
    }
}

interface C extends A {
    @Override
    default void hello(){
        System.out.println("Hello C");
    }
}

class D implements  B, C {
    @Override
    public void hello() {
        B.super.hello();
    }
}

public class DiamondProblem {
}
