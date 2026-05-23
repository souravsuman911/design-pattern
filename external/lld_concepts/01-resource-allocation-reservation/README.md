# Resource Allocation and Reservation

## Problem Shape
Limited resources are booked, held, occupied, cancelled, or released.
Examples: parking spot, event seat, hotel room, meeting room, doctor slot.

## Core Model
- **Resource**: Thing being reserved. Example: `Seat A1`, `Spot P12`, `Room 203`.
- **Slot/TimeRange**: When resource is needed. Example: `10:00-11:00`.
- **Hold**: Temporary claim before payment. Example: seat held for `5 minutes`.
- **Booking**: Final or in-progress reservation.
- **Status**: `REQUESTED -> HELD -> CONFIRMED -> CANCELLED`, `HELD -> EXPIRED`.

## Deep Concepts With Compact Examples
- **Availability**: Check if resource is free. Example: `isSeatFree(showId, seatId)`.
- **Concurrency**: Two users must not confirm same resource. Example: only one user gets `Seat A1`.
- **Idempotency**: Retry should not duplicate booking. Example: same `requestId` returns same booking.
- **Expiry**: Unpaid holds auto-release. Example: release hold after `5 min`.
- **Allocation Strategy**: Choose resource automatically. Example: nearest parking spot or best available seat.
- **Pricing Strategy**: Price varies by resource/time. Example: premium seat costs more than normal seat.

## Concurrency Options
- **Unique Constraint**: Add DB constraint like `(showId, seatId)`. Best final safety net.
- **Pessimistic Lock**: Lock row while booking. Use for high-demand seats.
- **Optimistic Lock**: Store `version`; update only if version matches. Good for moderate conflicts.
- **Distributed Lock**: Lock key like `seat:A1` in Redis. Use when many app instances compete.
- **Hold With TTL**: Insert temporary hold with expiry. Best for payment flows.

## Interview Questions: Short Answers
- **Prevent double booking?** Lock or unique constraint during confirmation.
- **Payment succeeds, booking fails?** Retry idempotently or refund via compensation.
- **Payment fails?** Mark booking failed and release hold.
- **Expired holds?** Background job or TTL cleanup releases them.
- **Cancellation/refund?** Validate policy, change status, release resource, trigger refund.
- **Search availability?** Query free resources by time, type, capacity, and location.

## Implementation Checklist
- Define `Resource`, `Slot`, `Hold`, `Booking`.
- Add booking status transitions.
- Separate availability from confirmation.
- Add lock/unique constraint at confirmation.
- Add hold expiry cleanup.
- Make payment callback idempotent.
