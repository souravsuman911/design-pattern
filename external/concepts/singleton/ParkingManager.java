package internal.designPattern.external.concepts.singleton;

public class ParkingManager {
    private static final ParkingManager singletonObj = new ParkingManager();

    private ParkingManager(){
    }

    public static ParkingManager getInstanceMethod(){
        return singletonObj;
    }
}

class Client1 {
    public static void main(String[] args) {
        ParkingManager parkingManager = ParkingManager.getInstanceMethod();
    }
}
