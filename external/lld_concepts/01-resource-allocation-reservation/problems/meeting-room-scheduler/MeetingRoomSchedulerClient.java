import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

enum RoomStatus { ACTIVE, INACTIVE }
enum MeetingStatus { SCHEDULED, CANCELLED }
enum ParticipantStatus { REQUIRED, OPTIONAL }

final class TimeSlot {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    LocalDateTime getStartTime() {
        return startTime;
    }

    LocalDateTime getEndTime() {
        return endTime;
    }

    boolean overlaps(TimeSlot other) {
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    long durationInMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public String toString() {
        return startTime + " to " + endTime;
    }
}

class User {
    String userId;
    String name;
    String email;

    User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}

class MeetingRoom {
    String roomId;
    String name;
    String floor;
    int capacity;
    List<String> amenities;
    RoomStatus status = RoomStatus.ACTIVE;

    MeetingRoom(String roomId, String name, String floor, int capacity, List<String> amenities) {
        this.roomId = roomId;
        this.name = name;
        this.floor = floor;
        this.capacity = capacity;
        this.amenities = List.copyOf(amenities);
    }
}

class Participant {
    String userId;
    ParticipantStatus status;

    Participant(String userId, ParticipantStatus status) {
        this.userId = userId;
        this.status = status;
    }
}

class Meeting {
    String meetingId;
    String title;
    String organizerId;
    String roomId;
    TimeSlot timeSlot;
    List<Participant> participants;
    MeetingStatus status;
}

class ScheduleMeetingRequest {
    String title;
    String organizerId;
    String roomId;
    TimeSlot timeSlot;
    List<Participant> participants;
    String idempotencyKey;
}

class FindRoomsRequest {
    TimeSlot timeSlot;
    int requiredCapacity;
    List<String> requiredAmenities;
}

class MeetingController {
    private final MeetingService meetingService;

    MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    Meeting scheduleMeeting(ScheduleMeetingRequest request) {
        return meetingService.scheduleMeeting(request);
    }

    Meeting cancelMeeting(String meetingId, String userId) {
        return meetingService.cancelMeeting(meetingId, userId);
    }

    List<Meeting> getUserMeetings(String userId) {
        return meetingService.getUserMeetings(userId);
    }
}

class RoomController {
    private final RoomService roomService;

    RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    List<MeetingRoom> findAvailableRooms(FindRoomsRequest request) {
        return roomService.findAvailableRooms(request);
    }
}

interface RoomAllocationStrategy {
    MeetingRoom chooseRoom(List<MeetingRoom> availableRooms, FindRoomsRequest request);
}

class SmallestCapacityFitStrategy implements RoomAllocationStrategy {
    @Override
    public MeetingRoom chooseRoom(List<MeetingRoom> availableRooms, FindRoomsRequest request) {
        return availableRooms.stream()
                .min(Comparator.comparingInt(room -> room.capacity))
                .orElseThrow(() -> new IllegalStateException("No room available"));
    }
}

class RoomService {
    private final RoomRepository roomRepository;
    private final MeetingRepository meetingRepository;

    RoomService(RoomRepository roomRepository, MeetingRepository meetingRepository) {
        this.roomRepository = roomRepository;
        this.meetingRepository = meetingRepository;
    }

    List<MeetingRoom> findAvailableRooms(FindRoomsRequest request) {
        validateFindRoomsRequest(request);
        return roomRepository.getActiveRooms().stream()
                .filter(room -> room.capacity >= request.requiredCapacity)
                .filter(room -> room.amenities.containsAll(request.requiredAmenities))
                .filter(room -> meetingRepository.isRoomAvailable(room.roomId, request.timeSlot))
                .collect(Collectors.toList());
    }

    private void validateFindRoomsRequest(FindRoomsRequest request) {
        if (request == null || request.timeSlot == null) {
            throw new IllegalArgumentException("Time slot is required");
        }
        if (request.requiredCapacity <= 0) {
            throw new IllegalArgumentException("Required capacity must be positive");
        }
        if (request.requiredAmenities == null) {
            request.requiredAmenities = List.of();
        }
    }
}

class MeetingService {
    private final RoomRepository roomRepository;
    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;
    private final RoomAllocationStrategy allocationStrategy;
    private final Map<String, String> idempotencyKeyToMeetingId = new ConcurrentHashMap<>();

    MeetingService(RoomRepository roomRepository,
                   MeetingRepository meetingRepository,
                   NotificationService notificationService,
                   RoomAllocationStrategy allocationStrategy) {
        this.roomRepository = roomRepository;
        this.meetingRepository = meetingRepository;
        this.notificationService = notificationService;
        this.allocationStrategy = allocationStrategy;
    }

    Meeting scheduleMeeting(ScheduleMeetingRequest request) {
        validateScheduleRequest(request);
        Meeting existingMeeting = findIdempotentMeeting(request.idempotencyKey);
        if (existingMeeting != null) {
            return existingMeeting;
        }

        ReentrantLock lock = roomRepository.lockForRoom(request.roomId);
        lock.lock();
        try {
            existingMeeting = findIdempotentMeeting(request.idempotencyKey);
            if (existingMeeting != null) {
                return existingMeeting;
            }
            MeetingRoom room = roomRepository.getActiveRoom(request.roomId);
            if (!meetingRepository.isRoomAvailable(room.roomId, request.timeSlot)) {
                throw new IllegalStateException("Room is already booked for this time slot");
            }

            Meeting meeting = new Meeting();
            meeting.meetingId = UUID.randomUUID().toString();
            meeting.title = request.title;
            meeting.organizerId = request.organizerId;
            meeting.roomId = request.roomId;
            meeting.timeSlot = request.timeSlot;
            meeting.participants = List.copyOf(request.participants);
            meeting.status = MeetingStatus.SCHEDULED;

            meetingRepository.save(meeting);
            idempotencyKeyToMeetingId.put(request.idempotencyKey, meeting.meetingId);
            notificationService.notifyParticipants(meeting, "Meeting scheduled");
            return meeting;
        } finally {
            lock.unlock();
        }
    }

    Meeting scheduleWithAutoAllocation(String title,
                                       String organizerId,
                                       FindRoomsRequest findRoomsRequest,
                                       List<Participant> participants,
                                       String idempotencyKey) {
        RoomService roomService = new RoomService(roomRepository, meetingRepository);
        List<MeetingRoom> availableRooms = roomService.findAvailableRooms(findRoomsRequest);
        MeetingRoom selectedRoom = allocationStrategy.chooseRoom(availableRooms, findRoomsRequest);

        ScheduleMeetingRequest request = new ScheduleMeetingRequest();
        request.title = title;
        request.organizerId = organizerId;
        request.roomId = selectedRoom.roomId;
        request.timeSlot = findRoomsRequest.timeSlot;
        request.participants = participants;
        request.idempotencyKey = idempotencyKey;
        return scheduleMeeting(request);
    }

    Meeting cancelMeeting(String meetingId, String userId) {
        Meeting meeting = meetingRepository.getMeeting(meetingId);
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting not found");
        }
        if (!Objects.equals(meeting.organizerId, userId)) {
            throw new IllegalStateException("Only organizer can cancel meeting");
        }

        ReentrantLock lock = roomRepository.lockForRoom(meeting.roomId);
        lock.lock();
        try {
            if (meeting.status == MeetingStatus.SCHEDULED) {
                meeting.status = MeetingStatus.CANCELLED;
                meetingRepository.save(meeting);
                notificationService.notifyParticipants(meeting, "Meeting cancelled");
            }
            return meeting;
        } finally {
            lock.unlock();
        }
    }

    List<Meeting> getUserMeetings(String userId) {
        return meetingRepository.getMeetingsForUser(userId);
    }

    private Meeting findIdempotentMeeting(String idempotencyKey) {
        String meetingId = idempotencyKeyToMeetingId.get(idempotencyKey);
        return meetingId == null ? null : meetingRepository.getMeeting(meetingId);
    }

    private void validateScheduleRequest(ScheduleMeetingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        }
        if (request.title == null || request.title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (request.organizerId == null || request.organizerId.isBlank()) {
            throw new IllegalArgumentException("Organizer is required");
        }
        if (request.roomId == null || request.roomId.isBlank()) {
            throw new IllegalArgumentException("Room is required");
        }
        if (request.timeSlot == null) {
            throw new IllegalArgumentException("Time slot is required");
        }
        if (request.participants == null) {
            request.participants = List.of();
        }
        if (request.idempotencyKey == null || request.idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }
    }
}

class RoomRepository {
    private final Map<String, MeetingRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> roomLocks = new ConcurrentHashMap<>();

    void addRoom(MeetingRoom room) {
        rooms.put(room.roomId, room);
    }

    MeetingRoom getActiveRoom(String roomId) {
        MeetingRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        if (room.status != RoomStatus.ACTIVE) {
            throw new IllegalStateException("Room is inactive");
        }
        return room;
    }

    List<MeetingRoom> getActiveRooms() {
        return rooms.values().stream()
                .filter(room -> room.status == RoomStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    ReentrantLock lockForRoom(String roomId) {
        return roomLocks.computeIfAbsent(roomId, ignored -> new ReentrantLock());
    }
}

class MeetingRepository {
    private final Map<String, Meeting> meetings = new ConcurrentHashMap<>();

    void save(Meeting meeting) {
        meetings.put(meeting.meetingId, meeting);
    }

    Meeting getMeeting(String meetingId) {
        return meetings.get(meetingId);
    }

    boolean isRoomAvailable(String roomId, TimeSlot requestedSlot) {
        return meetings.values().stream()
                .filter(meeting -> Objects.equals(meeting.roomId, roomId))
                .filter(meeting -> meeting.status == MeetingStatus.SCHEDULED)
                .noneMatch(meeting -> meeting.timeSlot.overlaps(requestedSlot));
    }

    List<Meeting> getMeetingsForUser(String userId) {
        return meetings.values().stream()
                .filter(meeting -> meeting.status == MeetingStatus.SCHEDULED)
                .filter(meeting -> Objects.equals(meeting.organizerId, userId)
                        || meeting.participants.stream().anyMatch(participant -> Objects.equals(participant.userId, userId)))
                .collect(Collectors.toList());
    }
}

class NotificationService {
    void notifyParticipants(Meeting meeting, String message) {
        List<String> userIds = new ArrayList<>();
        userIds.add(meeting.organizerId);
        meeting.participants.forEach(participant -> userIds.add(participant.userId));
        System.out.println(message + " for users: " + userIds);
    }
}

public class MeetingRoomSchedulerClient {
    public static void main(String[] args) {
        RoomRepository roomRepository = new RoomRepository();
        MeetingRepository meetingRepository = new MeetingRepository();

        roomRepository.addRoom(new MeetingRoom("room-1", "Orion", "1", 4, List.of("TV", "WHITEBOARD")));
        roomRepository.addRoom(new MeetingRoom("room-2", "Apollo", "2", 10, List.of("TV", "WHITEBOARD", "PROJECTOR")));

        MeetingService meetingService = new MeetingService(
                roomRepository,
                meetingRepository,
                new NotificationService(),
                new SmallestCapacityFitStrategy()
        );
        MeetingController meetingController = new MeetingController(meetingService);

        ScheduleMeetingRequest request = new ScheduleMeetingRequest();
        request.title = "Design Review";
        request.organizerId = "user-1";
        request.roomId = "room-1";
        request.timeSlot = new TimeSlot(
                LocalDateTime.of(2026, 5, 23, 10, 0),
                LocalDateTime.of(2026, 5, 23, 11, 0)
        );
        request.participants = List.of(
                new Participant("user-2", ParticipantStatus.REQUIRED),
                new Participant("user-3", ParticipantStatus.OPTIONAL)
        );
        request.idempotencyKey = "schedule-user-1-room-1-20260523-1000";

        Meeting meeting = meetingController.scheduleMeeting(request);
        System.out.println("Meeting status: " + meeting.status);
        System.out.println("Meeting room: " + meeting.roomId);
        System.out.println("Meeting slot: " + meeting.timeSlot);
    }
}
