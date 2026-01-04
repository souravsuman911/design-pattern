package internal.designPattern.observerPattern.youtube;

import java.util.ArrayList;
import java.util.List;

interface IChannel {
    void registerSubscriber(ISubscriber subscriber);
    void removeSubscriber(ISubscriber subscriber);
    void notifySubscriber();
}

interface ISubscriber {
    void update(int videoState);
}

class Channel implements IChannel {

    List<ISubscriber> subscribers;
    int videoStates;

    public Channel() {
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void registerSubscriber(ISubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscriber() {
        for(ISubscriber subscriber : subscribers){
            subscriber.update(videoStates);
        }
        System.out.println();
    }

    public int getVideoStates() {
        return videoStates;
    }

    public void setVideoStates(int videoStates) {
        this.videoStates = videoStates;
        notifySubscriber();
    }
}

class Subscriber implements ISubscriber {

    private String name;

    public Subscriber(String name){
        this.name = name;
    }

    @Override
    public void update(int videoState) {
        System.out.println("Hi " + getName() + ", new video uploaded for you : " + videoState);
    }

    public String getName(){
        return name;
    }
}

public class MainClient {
    public static void main(String[] args) {
        Channel fintechChannel = new Channel();
        fintechChannel.setVideoStates(0);

        ISubscriber subscriber1 = new Subscriber("Subscriber-1");
        ISubscriber subscriber2 = new Subscriber("Subscriber-2");
        ISubscriber subscriber3 = new Subscriber("Subscriber-3");

        fintechChannel.registerSubscriber(subscriber1);
        fintechChannel.registerSubscriber(subscriber2);
        fintechChannel.registerSubscriber(subscriber3);

        fintechChannel.setVideoStates(1);

        fintechChannel.removeSubscriber(subscriber2);

        fintechChannel.setVideoStates(2);
    }
}
