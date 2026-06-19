# Notification System LLD

This document explains the low-level design for a multi-channel notification system. The current implementation is available in `NotificationSystemClient.java`.

## Category

Notification and Pub-Sub Systems

## Related Concept Reference

This problem aligns most closely with the notification design shape documented in:

- `external/lld_concepts/06-notification-pubsub-systems/README.md`

The game-board simulation concept docs also mention notifications as a lightweight cross-cutting concern. That guidance is useful here because it reinforces a good separation:

- domain systems should emit events
- the notification system should own preference checks, queuing, prioritization, channel dispatch, and delivery logging

## Scope

The current implementation models a basic notification pipeline with:

- template resolution
- user channel preference lookup
- notification creation
- priority-based queueing
- channel-specific delivery handlers
- delivery log persistence in memory

It is an interview-friendly in-memory design, not a production-ready distributed notification platform yet.

## Functional Requirements Handled

1. Send notifications to a user through multiple channels.
2. Support channel types such as `EMAIL`, `SMS`, and `PUSH`.
3. Support notification types such as `TRANSACTIONAL` and `PROMOTIONAL`.
4. Render a user-facing message from a template key.
5. Respect channel preferences returned for the target user.
6. Queue notifications before delivery.
7. Process higher-priority notifications before lower-priority ones.
8. Record delivery success or failure.

## Functional Requirements Not Yet Fully Modeled

1. Subscribe and unsubscribe flows.
2. Per-channel retry with backoff.
3. Deduplication or idempotency keys.
4. Scheduling and delayed delivery.
5. Rate limiting by user, campaign, or channel.
6. Do-not-disturb time windows.
7. Topic-based fanout to groups or segments.
8. Dead-letter queue handling.
9. Template parameterization beyond simple key lookup.
10. Channel provider abstraction for external gateways.

## Non-Functional Requirements

1. **Extensibility**: New channels should be addable without changing calling code everywhere.
2. **Separation of concerns**: Message creation, queuing, dispatch, and logging should remain decoupled.
3. **Priority handling**: Important transactional events should be delivered before promotional ones.
4. **Scalability**: Worker processing should be horizontally scalable in a real deployment.
5. **Observability**: Delivery attempts should be logged for debugging and audit.
6. **Reliability**: Production systems should support retries, idempotency, and failure isolation.

## Current Class Diagram

```text
+--------------------------+
| NotificationManager      |
+--------------------------+
| getChannelPreferences()  |
| sendNotification()       |
+--------------------------+
             |
             v
+--------------------------+
| NotificationTemplate     |
+--------------------------+
| resolveTemplate()        |
+--------------------------+
             |
             v
+--------------------------+
| Notification             |
+--------------------------+
| id                       |
| userId                   |
| message                  |
| channelType              |
| priority                 |
| timeStamp                |
+--------------------------+

+--------------------------+
| NotificationQueue        |
+--------------------------+
| PriorityBlockingQueue    |
| push()                   |
| remove()                 |
+--------------------------+
             |
             v
+--------------------------+
| NotificationWorker       |
+--------------------------+
| processNotification()    |
+--------------------------+
             |
             v
+--------------------------+
| ChannelFactory           |
+--------------------------+
| getHandler()             |
+--------------------------+
       |        |        |
       v        v        v
   EmailHandler SmsHandler PushHandler

+--------------------------+
| DeliveryLogRepository    |
+--------------------------+
| save()                   |
+--------------------------+
```

## Main Entities and Responsibilities

### `Notification`

Represents one unit of delivery for one user on one channel.

Fields:
- `id`
- `userId`
- `message`
- `channelType`
- `priority`
- `timeStamp`

Note:
- A single business event may expand into multiple `Notification` objects, one per enabled channel.

### `NotificationManager`

Acts as the orchestration layer for notification creation.

Responsibilities:
- fetch user channel preferences
- resolve template content
- map notification type to priority
- create channel-specific notification instances
- enqueue notifications for delivery

### `NotificationTemplate`

Handles message generation from a template key.

Current behavior:
- resolves hardcoded keys such as `AMOUNT_DEBITED` and `PROMO`
- falls back to the raw template key if no template is registered

### `NotificationQueue`

Acts as the in-memory buffer between creation and delivery.

Current behavior:
- stores notifications in a `PriorityBlockingQueue`
- sorts by priority first
- sorts by timestamp for same-priority notifications

### `NotificationWorker`

Consumes queued notifications and dispatches them to channel handlers.

Responsibilities:
- poll pending notifications
- resolve correct channel handler
- attempt send
- write success or failure logs

### `ChannelFactory`

Creates the correct `ChannelHandler` implementation for a channel type.

### `ChannelHandler`

Interface for channel-specific sending logic.

Current implementations:
- `EmailHandler`
- `SmsHandler`
- `PushHandler`

### `DeliveryLog` and `DeliveryLogRepository`

Capture delivery outcomes for later analysis.

Current behavior:
- stores logs in memory
- prints the log after save

### `User` and `UserPreference`

Represent receiver profile data and preference configuration.

Current note:
- these models exist, but preference lookup is still stubbed in `NotificationManager`

## Enums and State

### `ChannelType`

- `EMAIL`
- `SMS`
- `PUSH`

### `NotificationType`

- `TRANSACTIONAL`
- `PROMOTIONAL`

## Current End-to-End Flow

```text
Client calls NotificationManager.sendNotification()
    -> resolve message from template key
    -> derive priority from notification type
    -> fetch enabled user channels
    -> create one Notification per channel
    -> enqueue into NotificationQueue
    -> NotificationWorker polls queue
    -> ChannelFactory selects handler
    -> handler sends message
    -> DeliveryLogRepository stores SUCCESS or FAILED
```

## Priority Design

The current code uses:

- `TRANSACTIONAL -> priority 1`
- `PROMOTIONAL -> priority 0`

This is a reasonable interview-level simplification because:

- OTP, payment, debit, and security alerts should usually preempt marketing traffic
- lower-priority promotional traffic can tolerate delay

In production, priority is often modeled with:

- more than two levels
- channel-specific SLAs
- campaign throttling
- scheduled windows for non-urgent traffic

## Schema Design

### In-Memory Object Model

- `NotificationManager`: builds notifications from request + preferences.
- `NotificationTemplate`: resolves message content.
- `Notification`: immutable delivery payload candidate.
- `NotificationQueue`: shared priority queue.
- `NotificationWorker`: queue consumer.
- `ChannelFactory`: maps channel type to handler.
- `ChannelHandler`: channel strategy interface.
- `DeliveryLogRepository`: persists delivery outcome.

### Production Database Mapping

#### `users`

- `user_id` PK
- `email`
- `phone_number`
- `push_token`
- `status`
- `created_at`
- `updated_at`

#### `user_preferences`

- `user_id` PK/FK
- `email_enabled`
- `sms_enabled`
- `push_enabled`
- `dnd_enabled`
- `preferred_language`
- `updated_at`

#### `notification_template`

- `template_id` PK
- `template_key` unique
- `channel_type`
- `subject_template` nullable
- `body_template`
- `template_version`
- `is_active`

#### `notification_request`

- `request_id` PK
- `event_type`
- `recipient_user_id`
- `notification_type`
- `template_key`
- `payload_json`
- `priority`
- `idempotency_key`
- `created_at`

#### `notification_delivery`

- `delivery_id` PK
- `request_id` FK
- `user_id` FK
- `channel_type`
- `rendered_message`
- `status`
- `attempt_count`
- `scheduled_at`
- `sent_at` nullable
- `failure_reason` nullable

#### `delivery_attempt`

- `attempt_id` PK
- `delivery_id` FK
- `attempt_number`
- `provider_response_code`
- `provider_response_message`
- `attempted_at`

#### `outbox_event`

- `event_id` PK
- `aggregate_type`
- `aggregate_id`
- `event_type`
- `payload_json`
- `publish_status`
- `created_at`

## Recommended APIs

### Send Notification

```http
POST /v1/notifications
```

Request:

```json
{
  "userId": 1110013,
  "templateKey": "AMOUNT_DEBITED",
  "notificationType": "TRANSACTIONAL",
  "payload": {
    "amount": 50,
    "currency": "USD"
  },
  "idempotencyKey": "txn-1002-user-1110013"
}
```

### Get Delivery Status

```http
GET /v1/notifications/{requestId}
```

### Update User Preferences

```http
PUT /v1/users/{userId}/notification-preferences
```

### Retry Failed Delivery

```http
POST /v1/notifications/{deliveryId}/retry
```

## HLD View

### Main Components

```text
Producer Service
  -> Notification API / Notification Service
  -> Template Service
  -> Preference Service
  -> Priority Queue / Message Broker
  -> Delivery Workers
  -> Channel Providers (email, SMS, push)
  -> Delivery Log Store / Analytics
```

### Recommended Responsibilities

#### Producer Service

- emits business events such as `ORDER_CONFIRMED`, `AMOUNT_DEBITED`, or `PROMO_CREATED`
- should not contain channel-specific send logic

#### Notification Service

- validates notification requests
- resolves recipients and templates
- expands one request into per-channel deliveries
- writes delivery records
- enqueues work asynchronously

#### Preference Service

- determines which channels are enabled
- applies DND rules
- may support topic-level opt-in or opt-out

#### Queue or Broker

- buffers work between producers and workers
- supports retries and dead-letter handling in production

#### Delivery Workers

- consume queued deliveries
- call external providers
- update status and retry counters

#### Channel Providers

- encapsulate provider-specific APIs such as SMTP, SMS gateways, or push SDKs

## Event-Driven Integration

A good notification system should typically consume domain events instead of being deeply embedded inside core business logic.

Example:

```text
Payment Service publishes AMOUNT_DEBITED
    -> Notification Service consumes event
    -> resolves template + preferences
    -> creates EMAIL and SMS deliveries
    -> workers send them asynchronously
```

This keeps:

- payment logic focused on money movement
- notification logic focused on channel fanout and delivery

## Concurrency and Scaling Notes

### Current Implementation

- queue is process-local
- worker drains queue in a single loop
- logs are stored in static in-memory list

This is good enough for an interview demo, but it has limits:

- data is lost on process restart
- multiple application instances cannot share the same queue safely
- provider failures have no retry policy
- there is no delivery idempotency

### Production Evolution

1. Replace in-memory queue with Kafka, RabbitMQ, SQS, or Redis streams.
2. Persist notification requests before enqueueing.
3. Add retry with exponential backoff.
4. Add dead-letter queue for exhausted failures.
5. Use idempotency key to prevent duplicate sends.
6. Partition workers by channel or priority if needed.

## Design Patterns Used

1. **Strategy Pattern**
   - `ChannelHandler` allows different delivery behavior for email, SMS, and push.

2. **Factory Pattern**
   - `ChannelFactory` creates the appropriate strategy based on `ChannelType`.

3. **Producer-Consumer Pattern**
   - `NotificationManager` produces work and `NotificationWorker` consumes it.

4. **Template Method / Template Resolution Idea**
   - `NotificationTemplate` centralizes content generation.

5. **Observer / Event-Driven Extension**
   - In a larger system, upstream services publish events and this module reacts to them.

## Gaps In The Current Code

1. `userId` preference lookup is stubbed and always returns email + SMS.
2. `UserPreference` is modeled but not actually used.
3. `NotificationQueue.remove(Notification notification)` ignores the input and removes head blindly.
4. `notification.hashCode()` is used as delivery log identifier, which is unstable as a business identifier.
5. `ChannelFactory.getHandler()` can return `null`, which would need defensive handling in production.
6. No retry, no backoff, and no dead-letter support exist.
7. No dedupe or idempotency protection exists.
8. Template rendering does not use structured payload data.
9. No rate limiting or DND checks are enforced.
10. Queue ordering for same priority favors newer timestamps first because of descending comparison; FIFO among equal priority may be preferable depending on requirements.

## Suggested Refactoring Plan

1. Introduce a `NotificationRequest` object distinct from per-channel `Notification`.
2. Replace hardcoded preference lookup with `UserPreferenceRepository`.
3. Add `notificationId` and `deliveryId` as stable identifiers.
4. Make `Notification` immutable and include status metadata outside the payload object.
5. Add `DeliveryStatus` enum instead of raw strings.
6. Split worker logic into dispatcher + retry policy + provider adapter.
7. Add persistence interfaces for notifications, preferences, and logs.
8. Add idempotency and retry policy before introducing external providers.

## Sample Interview Talking Points

1. Why use async processing for notifications?
   Because delivery can be slow or fail due to external providers, and user-facing business requests should not block on it.

2. Why split one request into multiple channel deliveries?
   Because each channel has different provider behavior, retry policy, and delivery status.

3. How do you prioritize critical notifications?
   By storing priority on the queued delivery and using separate worker or broker policies if traffic grows.

4. How do you avoid duplicate sends?
   Use an idempotency key per business event and recipient-channel combination.

5. How do you respect user preferences?
   Check preference and DND rules before enqueueing, not inside each provider.

## Status

Implementation present and runnable in interview/demo form.

Production-grade retry, persistence, idempotency, and provider integration are still pending.
