package internal.designPattern.external.practice.lld.bookMyShow;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

interface ITheatreCompany {
    void createScreen(Screen screen);
    void deleteScreen(int ScreenId);
}

class TheatreCompany implements ITheatreCompany {
    private int id;
    private String name;
    private List<Screen> screens;

    public TheatreCompany(String name){
        this.name = name;
        this.screens = new ArrayList<>();
    }

    @Override
    public void createScreen(Screen screen) {
        boolean exist = screens.stream().anyMatch(s -> s.getScreenId() == screen.getScreenId());

        if(exist){
            throw new IllegalArgumentException("Screen already exists with id : " + screen);
        }

        screens.add(screen);
    }

    @Override
    public void deleteScreen(int screenId) {
        boolean exist = screens.stream().anyMatch(s -> s.getScreenId() == screenId);
        
        if(!exist){
            throw new NoSuchElementException("Screen does not exists with screenId : " + screenId);
        }

        screens.removeIf(s -> s.getScreenId() == screenId);
    }
}

enum ScreenType {

    TWO_D_SCREEN('2'),
    THREE_D_SCREEN('3'),
    FOUR_D_SCREEN('4');

    private char code;

    ScreenType(char code) {
        this.code = code;
    }
    public char getCode(){
        return code;
    }
}

class Screen {

    private int screenId;
    private int capacity;
    private List<Seat> seatList;

    public Screen(int screenId, int capacity) {
        this.screenId = screenId;
        this.capacity = capacity;
        this.seatList = new ArrayList<>();
    }

    public void setDefaultSeatArrangement() {
        for(int i = 1; i <= 10; i ++){
            seatList.add(new Seat(i, SeatType.FRONT_ROW));
            seatList.add(new Seat(i + 10, SeatType.MIDDLE_ROW));
            seatList.add(new Seat(i + 20, SeatType.BACK_ROW));
            seatList.add(new Seat(i + 30, SeatType.RECLINERS));
        }
    }

    public int getScreenId() {
        return screenId;
    }
}

enum SeatType {

    FRONT_ROW('F'),
    MIDDLE_ROW('S'),
    BACK_ROW('B'),
    RECLINERS('R');

    private char code;

    SeatType(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
}

class Seat {

    private int seatNumber;
    private SeatType seatType;
    private boolean isBooked;

    public Seat(int seatNumber, SeatType seatType) {
         this.seatNumber = seatNumber;
         this.seatType = seatType;
         this.isBooked = false;
    }
}

class Ticket {
    private int ticketId;
    private int userId;
    private int theatreCompanyId;
    private int screenId;
    private int seatNumber;

    public Ticket(int ticketId, int userId, int theatreCompanyId, int screenId, int seatNumber) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.theatreCompanyId = theatreCompanyId;
        this.screenId = screenId;
        this.seatNumber = seatNumber;
    }
}

class User {
    private int id;
    private String name;
    private String email;
    private String phno;

    public User(int id, String name, String email, String phno) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phno = phno;
    }

    public void bookTicket(){

    }

    public void cancelTicket(){

    }
}

class BookTicket implements Runnable {


    @Override
    public void run() {

    }
}

class CancelTicker implements Runnable {

    @Override
    public void run() {

    }
}


public class BookMyShowClient {
    public static void main(String[] args) {

    }
}
