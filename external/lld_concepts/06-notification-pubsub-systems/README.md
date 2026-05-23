# Notification and Pub-Sub Systems

## Problem Shape
Events/messages are sent to subscribers or users through channels.
Examples: notification system, pub-sub, stock alert, YouTube subscription alert.

## Core Model
- **Publisher**: Produces event. Example: order service.
- **Topic/Event Type**: Category. Example: `ORDER_PAID`.
- **Subscriber**: Interested consumer/user.
- **Message**: Payload to deliver.
- **Channel**: Email, SMS, push, webhook, in-app.

## Deep Concepts With Compact Examples
- **Fanout**: One event to many users. Example: creator uploads video -> notify subscribers.
- **Retry**: Failed SMS retried with backoff.
- **Deduplication**: Same event should not notify twice.
- **Priority**: OTP before marketing email.
- **Preferences**: User disables SMS but allows email.
- **Template Rendering**: `Hi {name}, order {id} confirmed`.

## Delivery Options
- **Sync Send**: Simple but slow for user request.
- **Async Queue**: Best common design.
- **At-Most-Once**: May lose messages, no duplicates.
- **At-Least-Once**: May duplicate, needs idempotency.
- **Channel Strategy**: Separate email/SMS/push sender.

## Interview Questions: Short Answers
- **Subscribe/unsubscribe?** Maintain subscription table.
- **Multiple channels?** Use channel strategy/factory.
- **Retry failed sends?** Queue retry with backoff and max attempts.
- **Avoid duplicates?** Use notification idempotency key.
- **Preferences?** Check user preferences before enqueue/send.

## Implementation Checklist
- Define `Message`, `Topic`, `Subscriber`.
- Separate publish from delivery.
- Add channel strategies.
- Store delivery status.
- Add retry and dedupe.
- Respect user preferences.
