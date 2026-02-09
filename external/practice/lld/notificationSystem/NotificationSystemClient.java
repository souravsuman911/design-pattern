package internal.designPattern.external.practice.lld.notificationSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

enum ChannelType {
    EMAIL, SMS, PUSH
}

enum NotificationType {
    TRANSACTIONAL, PROMOTIONAL
}

class NotificationTemplate {
    public static String resolveTemplate(String templateKey, int userId) {
        switch (templateKey) {
            case "AMOUNT_DEBITED":
                return "User " + userId + ": Amount has been debited";
            case "PROMO":
                return "User " + userId + ": New promotional offer available";
            default:
                return templateKey;
        }
    }
}

class Notification {
    private int id;
    private int userId; // userId, groupId
    private String message;
    private ChannelType channelType;
    private int priority;
    private long timeStamp; // in ms

    public Notification(int id, int userId, String message, ChannelType channelType, int priority, long timeStamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.channelType = channelType;
        this.priority = priority;
        this.timeStamp = timeStamp;
    }

    public ChannelType getChannelType(){
        return channelType;
    }

    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}

class NotificationManager {
    public List<ChannelType> getChannelPreferences(int userId){
        List<ChannelType> channelTypes = new ArrayList<>();
        // fetches channel preferences from db for the particular user
        // what types of notification user has enabled
        channelTypes.add(ChannelType.EMAIL);
        channelTypes.add((ChannelType.SMS));

        return channelTypes;
    }

    public void sendNotification(int notificationId, int userId, String templateKey, NotificationType notificationType, long timeStamp){
        String message = NotificationTemplate.resolveTemplate(templateKey, userId);
        int priority = notificationType.equals(NotificationType.TRANSACTIONAL) ? 1 : 0;

        for(ChannelType channelType : getChannelPreferences(userId)){
            Notification notification = new Notification(notificationId, userId, message, channelType, priority, timeStamp);
            NotificationQueue.push(notification);
        }
    }
}

//
class NotificationQueue {
    public static Queue<Notification> queue = new PriorityBlockingQueue<>(100, (n1, n2) -> {
        if(n1.getPriority() == n2.getPriority()){
            return Long.compare(n2.getTimeStamp(), n1.getTimeStamp());
        }

        return Integer.compare(n2.getPriority(), n1.getPriority());
    });

    public static void push(Notification notification){
        queue.offer(notification);
    }

    public static void remove(Notification notification){
        queue.remove();
    }
}

// can be scaled horizontally for higher load
class NotificationWorker {
    public void processNotification() {
        while(!NotificationQueue.queue.isEmpty()){
            Notification notification = NotificationQueue.queue.poll();
            ChannelHandler channelHandler = ChannelFactory.getHandler(notification.getChannelType());

            try {
                channelHandler.sendNotification(notification.getMessage());
                DeliveryLogRepository.save(new DeliveryLog(notification.hashCode(), notification.getChannelType(),"SUCCESS"));
            } catch (Exception e) {
                DeliveryLogRepository.save(new DeliveryLog(notification.hashCode(), notification.getChannelType(), "FAILED"));
            }
        }
    }
}

class ChannelDispatcher {
}

class ChannelFactory {
    public static ChannelHandler getHandler(ChannelType channelType){
        switch (channelType){
            case SMS: return new SmsHandler();
            case EMAIL: return new EmailHandler();
            case PUSH: return new PushHandler();
        }
        return null;
    }
}

interface ChannelHandler {
    void sendNotification(String message);
}

class SmsHandler implements ChannelHandler {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS notification : " + message);
    }
}

class EmailHandler implements ChannelHandler {
    @Override
    public void sendNotification(String message) {
        System.out.println("Email notification : " + message);
    }
}class PushHandler implements ChannelHandler {
    @Override
    public void sendNotification(String message) {
        System.out.println("Push notification : " + message);
    }
}

class DeliveryLog {

    private int notificationId;
    private ChannelType channelType;
    private String status; // SUCCESS / FAILED

    public DeliveryLog(int notificationId, ChannelType channelType, String status) {
        this.notificationId = notificationId;
        this.channelType = channelType;
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeliveryLog{" + "notificationId=" + notificationId + ", channelType=" + channelType + ", status='" + status + '\'' + '}';
    }
}

class DeliveryLogRepository {
    private static final List<DeliveryLog> logs = new ArrayList<>();

    public static void save(DeliveryLog log) {
        logs.add(log);
        System.out.println(log);
    }
}


class User {
    private int userId;
    private String email;
    private int phNo;

    public User(int userId, String email, int phNo) {
        this.userId = userId;
        this.email = email;
        this.phNo = phNo;
    }
}

class UserPreference {
    private String userId;
    private boolean emailEnabled;
    private boolean smsEnables;
    private boolean pushEnables;
    private boolean dndActivated;

    public UserPreference(String userId, boolean emailEnabled, boolean smsEnables, boolean pushEnables, boolean dndActivated) {
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.smsEnables = smsEnables;
        this.pushEnables = pushEnables;
        this.dndActivated = dndActivated;
    }
}

public class NotificationSystemClient {
    public static void main(String[] args) {
        NotificationManager manager = new NotificationManager();
        manager.sendNotification(1001, 1110011, "50% discount on hotel bookings", NotificationType.PROMOTIONAL, new Date().getTime());
        manager.sendNotification(1001, 1110012, "New credit card offer from SBI", NotificationType.PROMOTIONAL, new Date().getTime());
        manager.sendNotification(1002, 1110013, "Amount $50 has been debited", NotificationType.TRANSACTIONAL, new Date().getTime());

        NotificationWorker notificationWorker = new NotificationWorker();
        notificationWorker.processNotification();
        notificationWorker.processNotification();
        notificationWorker.processNotification();
    }
}
