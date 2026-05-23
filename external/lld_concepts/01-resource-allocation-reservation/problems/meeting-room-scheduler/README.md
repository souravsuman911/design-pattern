# Meeting Room Scheduler LLD

This document explains the low-level design for a meeting room scheduler similar to Google Calendar room booking, Outlook room scheduling, or an internal office room reservation system. The implementation is available in `MeetingRoomSchedulerClient.java`.

## Scope

The system allows users to search available meeting rooms, schedule meetings for a time slot, invite participants, cancel meetings, and view user meetings.

## Functional Requirements Handled

1. **Add / Manage Meeting Rooms**
   - Rooms have ID, name, floor, capacity, amenities, and status.
   - Only active rooms can be booked.

2. **Find Available Rooms**
   - Search rooms by time slot, capacity, and required amenities.
   - Only rooms without overlapping scheduled meetings are returned.

3. **Schedule Meeting**
   - Organizer can schedule a meeting in a selected room.
   - Meeting has title, organizer, room, time slot, and participants.
   - Same room cannot be booked for overlapping time slots.

4. **Auto Allocate Room**
   - System can choose a room using `RoomAllocationStrategy`.
   - Current strategy chooses the smallest room that satisfies the request.

5. **Cancel Meeting**
   - Only the organizer can cancel a meeting.
   - Cancelled meeting releases the room for that time slot.

6. **Query User Meetings**
   - User can view meetings where they are organizer or participant.

7. **Idempotent Scheduling**
   - Schedule API uses an idempotency key.
   - Retried requests with the same key return the same meeting.

8. **Notification**
   - Participants are notified when meeting is scheduled or cancelled.

## Non-Functional Requirements Handled

1. **Concurrency Safety**
   - Uses per-room `ReentrantLock`.
   - Different rooms can be booked concurrently.
   - Same room scheduling is protected from race conditions.

2. **Data Consistency**
   - Meeting status transition is controlled: `SCHEDULED -> CANCELLED`.
   - Overlapping meetings for the same room are rejected.
   - Cancelled meetings are ignored during availability checks.

3. **Scalability-Oriented Design**
   - Room locking is scoped per room, not global.
   - Search can be backed by DB indexes on room, capacity, amenities, and time.
   - Notification can be moved to an async queue.

4. **Fault Tolerance**
   - Idempotency protects against duplicate schedule calls.
   - Meeting status makes retry/cancel behavior predictable.

5. **Extensibility**
   - Allocation strategy is pluggable.
   - Can add recurring meetings, approval flow, room maintenance, and calendar sync.

## APIs

### Find Available Rooms

```http
POST /v1/rooms/search
```

Request:

```json
{
  "startTime": "2026-05-23T10:00:00",
  "endTime": "2026-05-23T11:00:00",
  "requiredCapacity": 6,
  "requiredAmenities": ["TV", "WHITEBOARD"]
}
```

Response:

```json
[
  {
    "roomId": "room-2",
    "name": "Apollo",
    "floor": "2",
    "capacity": 10,
    "amenities": ["TV", "WHITEBOARD", "PROJECTOR"]
  }
]
```

### Schedule Meeting

```http
POST /v1/meetings
```

Request:

```json
{
  "title": "Design Review",
  "organizerId": "user-1",
  "roomId": "room-1",
  "startTime": "2026-05-23T10:00:00",
  "endTime": "2026-05-23T11:00:00",
  "participantIds": ["user-2", "user-3"],
  "idempotencyKey": "schedule-user-1-room-1-20260523-1000"
}
```

Response:

```json
{
  "meetingId": "meeting-123",
  "status": "SCHEDULED",
  "roomId": "room-1"
}
```

### Cancel Meeting

```http
DELETE /v1/meetings/{meetingId}?userId={organizerId}
```

Response:

```json
{
  "meetingId": "meeting-123",
  "status": "CANCELLED"
}
```

### Get User Meetings

```http
GET /v1/users/{userId}/meetings
```

## Entities

### User

Represents employee or user.

Fields:
- `userId`
- `name`
- `email`

### MeetingRoom

Represents a bookable room.

Fields:
- `roomId`
- `name`
- `floor`
- `capacity`
- `amenities`
- `status`

### TimeSlot

Immutable value object for meeting duration.

Fields:
- `startTime`
- `endTime`

Important method:
- `overlaps(TimeSlot other)`

Overlap condition:

```text
start1 < end2 AND start2 < end1
```

### Participant

Represents invited user.

Fields:
- `userId`
- `status`: `REQUIRED` or `OPTIONAL`

### Meeting

Represents scheduled room booking.

Fields:
- `meetingId`
- `title`
- `organizerId`
- `roomId`
- `timeSlot`
- `participants`
- `status`

## Status Models

### RoomStatus

```text
ACTIVE
INACTIVE
```

Only `ACTIVE` rooms can be booked.

### MeetingStatus

```text
SCHEDULED -> CANCELLED
```

Availability checks ignore `CANCELLED` meetings.

### ParticipantStatus

```text
REQUIRED
OPTIONAL
```

Can be extended with `ACCEPTED`, `DECLINED`, `TENTATIVE`.

## Main Flows

### Happy Path: Schedule Meeting

```text
User selects room + time
    -> System validates request
    -> System checks idempotency key
    -> System locks room
    -> System checks room is active
    -> System checks no overlapping scheduled meeting
    -> System creates meeting
    -> System stores idempotency key mapping
    -> System notifies participants
```

### Conflict Flow: Room Already Booked

```text
User requests room for 10:00-11:00
    -> Another scheduled meeting overlaps
    -> System rejects request with "Room is already booked"
```

### Cancellation Flow

```text
Organizer cancels meeting
    -> System locks room
    -> System changes meeting status to CANCELLED
    -> Room becomes available for that time slot
    -> Participants are notified
```

### Idempotency Retry Flow

```text
Client retries schedule API with same idempotency key
    -> System finds existing meeting
    -> System returns same meeting
    -> No duplicate meeting is created
```

## Concurrency Handling

### Problem

Two users may try to book the same room for overlapping slots at the same time.

Example:

```text
User A checks room-1 available for 10:00-11:00
User B checks room-1 available for 10:30-11:30
Both create meetings without locking
Result: overlapping room bookings
```

### Current LLD Solution

The code uses one lock per room:

```java
private final Map<String, ReentrantLock> roomLocks = new ConcurrentHashMap<>();
```

Scheduling flow:

```text
Get lock for roomId
    -> lock
    -> check active room
    -> check overlapping meeting
    -> save meeting
    -> unlock
```

### Why Per-Room Lock?

Per-room locking is better than a global lock.

```text
Booking room-1 should not block booking room-2.
```

It also keeps multi-user concurrency simple for interview implementation.

### Production Alternatives

1. **Database Transaction + Row Lock**
   - Lock room row or matching meeting rows during scheduling.

2. **Optimistic Locking**
   - Maintain version on room calendar or booking slot.
   - Retry if version changed.

3. **Unique Slot Table**
   - Store normalized time buckets if meetings are fixed-size.
   - Unique constraint prevents duplicate bucket booking.

4. **Exclusion Constraint**
   - Some databases can prevent overlapping time ranges for same room.

5. **Distributed Lock**
   - Use Redis lock on `roomId` when multiple app instances schedule meetings.

## HLD Notes

### Main Components

```text
Client
  -> API Gateway
  -> Room Service
  -> Meeting Service
  -> Notification Service
  -> Database
  -> Message Queue
```

### Component Responsibilities

#### Room Service

- Stores room metadata.
- Searches active rooms by capacity and amenities.
- Checks availability using meeting data.

#### Meeting Service

- Schedules and cancels meetings.
- Owns concurrency control.
- Applies idempotency.

#### Notification Service

- Notifies organizer and participants.
- Can send email, push, Slack, or calendar invites.

#### Calendar Sync Service

- Optional production component.
- Syncs with Google Calendar, Outlook, or internal calendar.

## Database Schema

### users

```sql
CREATE TABLE users (
    user_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### meeting_rooms

```sql
CREATE TABLE meeting_rooms (
    room_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    floor VARCHAR(32),
    capacity INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### room_amenities

```sql
CREATE TABLE room_amenities (
    room_id VARCHAR(64) NOT NULL,
    amenity VARCHAR(64) NOT NULL,
    PRIMARY KEY (room_id, amenity),
    FOREIGN KEY (room_id) REFERENCES meeting_rooms(room_id)
);
```

### meetings

```sql
CREATE TABLE meetings (
    meeting_id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    organizer_id VARCHAR(64) NOT NULL,
    room_id VARCHAR(64) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (organizer_id) REFERENCES users(user_id),
    FOREIGN KEY (room_id) REFERENCES meeting_rooms(room_id)
);
```

Indexes:

```sql
CREATE INDEX idx_meetings_room_time ON meetings(room_id, start_time, end_time, status);
CREATE INDEX idx_meetings_organizer ON meetings(organizer_id, status);
```

### meeting_participants

```sql
CREATE TABLE meeting_participants (
    meeting_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    participant_status VARCHAR(32) NOT NULL,
    PRIMARY KEY (meeting_id, user_id),
    FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### Important DB Constraints

1. `meetings.idempotency_key` should be unique.
2. `end_time` must be greater than `start_time`.
3. Availability check and meeting insert should run in one transaction.
4. Index on `(room_id, start_time, end_time, status)` helps conflict checks.
5. Production systems should prevent overlapping active meetings at DB level if possible.

## Design Patterns / Principles Used

1. **Service Layer Pattern**
   - `RoomService` and `MeetingService` separate business logic.

2. **Repository Pattern**
   - `RoomRepository` and `MeetingRepository` abstract storage.

3. **DTO Pattern**
   - `ScheduleMeetingRequest` and `FindRoomsRequest` separate input from domain objects.

4. **Strategy Pattern**
   - `RoomAllocationStrategy` supports different room selection algorithms.

5. **State Machine**
   - Meeting lifecycle is modeled with `MeetingStatus`.

6. **Idempotency Pattern**
   - Same schedule retry returns same meeting.

7. **Lock Striping by Room**
   - Per-room lock prevents overlapping bookings without blocking all rooms.

## Common Interview Questions and Short Answers

### 1. How do you prevent double booking of a room?

Use per-room lock in LLD. In production, use DB transaction, row lock, optimistic locking, distributed lock, or DB exclusion constraint.

### 2. How do you check overlapping meetings?

Two time slots overlap if `start1 < end2 && start2 < end1`.

### 3. Why lock per room instead of globally?

It prevents conflicts for the same room while allowing different rooms to be booked concurrently.

### 4. How does cancellation release the room?

Availability checks ignore meetings with `CANCELLED` status, so the room becomes available for that slot.

### 5. Why use idempotency key?

If client retries schedule API due to timeout, the system returns the same meeting instead of creating duplicates.

### 6. How would you support recurring meetings?

Create a recurrence rule and expand it into individual meeting instances, then validate conflicts for every instance in one transaction.

### 7. How would you support auto room allocation?

Search available rooms and pass them to a `RoomAllocationStrategy`, such as smallest-fit, nearest-floor, or equipment-priority.

### 8. How would you scale room search?

Use DB indexes for capacity/time/status, cache room metadata, and use search indexes if amenity/location filtering becomes complex.

### 9. What if notification fails?

Meeting remains scheduled. Notification should be retried asynchronously using a queue.

### 10. What if organizer schedules two meetings at the same time?

Add user calendar conflict validation before saving the meeting.

### 11. How do you handle room maintenance?

Mark room `INACTIVE` or create blocked calendar slots so it cannot be booked.

### 12. How would you support participant responses?

Extend participant status with `ACCEPTED`, `DECLINED`, and `TENTATIVE`.

## Limitations of Current In-Memory LLD

- Data is not persisted.
- Notification only prints to console.
- No recurring meeting support.
- No participant availability check.
- No organizer calendar conflict check.
- No DB-level overlap constraint.
- No async notification queue.

## Future Enhancements

- Recurring meetings.
- Participant availability checking.
- Calendar integration.
- Room approval workflow.
- Room maintenance blocks.
- Waitlist for preferred rooms.
- Async notification with retry.
- Audit log for schedule/cancel events.
