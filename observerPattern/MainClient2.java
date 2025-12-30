package internal.designPattern.observerPattern;

import java.util.ArrayList;
import java.util.List;

public class MainClient2 {

    //observer
    public static interface Observer{
        public void update(String message);
    }

    // Concrete observer
    public static class ParkingApp implements Observer{

        private String username;

        public ParkingApp(String username){
            this.username = username;
        }
        @Override
        public void update(String message) {
            System.out.println(username + " received notification " + message);
        }
    }

    // Subject
    public static class ParkingManager{
        List<Observer> observers = new ArrayList<>();
        public void addObserver(Observer observer){
            observers.add(observer);
        }

        public void removeObserver(Observer observer){
            observers.remove(observer);
        }

        public void notifyObservers(String message){
            for(Observer o: observers){
                o.update(message);
            }
        }

        //trigger when a spot is freed
        public void spotFreed(){
            notifyObservers("Spot is Free");
        }
    }

    public static void main(String[] args) {

        ParkingManager parkingManager = new ParkingManager();
        Observer observer1 = new ParkingApp("Sourav");
        Observer observer2 = new ParkingApp("Suman");

        parkingManager.addObserver(observer1);
        parkingManager.addObserver(observer2);

        parkingManager.spotFreed();

    }
}
