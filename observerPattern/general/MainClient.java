package internal.designPattern.observerPattern.general;

import java.util.ArrayList;
import java.util.List;

interface ISubject {
    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers();
}

interface IObserver {
    void update(int newState);
}

class ConcreteSubject implements ISubject{
    List<IObserver> observers;
    private int state;

    public ConcreteSubject(){
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(IObserver observer : observers){
            observer.update(state);
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyObservers();
    }
}

class ConcreteObserver implements IObserver{

    private String name;

    public ConcreteObserver(String name){
        this.name = name;
    }

    @Override
    public void update(int newState) {
        System.out.println("Observer " + name + " notified, updated state : " + newState);
    }
}

public class MainClient {

    public static void main(String[] args) {
        ConcreteSubject subject = new ConcreteSubject();
        subject.setState(0);

        IObserver observer1 = new ConcreteObserver("Observer-1");
        IObserver observer2 = new ConcreteObserver("Observer-2");
        IObserver observer3 = new ConcreteObserver("Observer-3");

        subject.registerObserver(observer1);
        subject.registerObserver(observer2);
        subject.registerObserver(observer3);

        subject.setState(1);

        subject.removeObserver(observer2);

        subject.setState(2);


    }
}
