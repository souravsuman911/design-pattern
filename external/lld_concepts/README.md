# LLD Concepts

This directory groups common Low Level Design problems by reusable design shape instead of by memorized interview question.

## How To Use This
- First identify the concept category for a problem.
- Read the category README to understand shared entities, lifecycle, concurrency, and patterns.
- Open the problem folder when you are ready to implement that specific variation.
- Keep implementations focused inside each problem folder later.

## Concept Map
- [Resource Allocation and Reservation](01-resource-allocation-reservation/README.md)
- [Inventory and Order Management](02-inventory-order-management/README.md)
- [Matching and Assignment Systems](03-matching-assignment-systems/README.md)
- [State Machine Heavy Systems](04-state-machine-heavy-systems/README.md)
- [Rate Limiting and Throttling](05-rate-limiting-throttling/README.md)
- [Notification and Pub-Sub Systems](06-notification-pubsub-systems/README.md)
- [Feed and Timeline Systems](07-feed-timeline-systems/README.md)
- [File Storage and Hierarchical Systems](08-file-storage-hierarchical-systems/README.md)
- [Game and Board Simulation Systems](09-game-board-simulation-systems/README.md)
- [Financial and Ledger Systems](10-financial-ledger-systems/README.md)
- [Access Control and Authorization](11-access-control-authorization/README.md)
- [Search Filtering and Ranking](12-search-filtering-ranking/README.md)

## Cross-Cutting Concepts
- **Concurrency**: seat booking, parking spot allocation, inventory checkout, wallet transfer, provider assignment.
- **State transitions**: booking, payment, order, ride, delivery, game, vending machine, ATM.
- **Expiry and timeout**: temporary holds, carts, OTPs, payment sessions, provider acceptance windows.
- **Pricing and policies**: parking fees, surge pricing, discounts, subscriptions, cancellation rules.
- **Search and ranking**: hotels, products, cabs, restaurants, jobs, events, libraries.
- **Notifications**: confirmation, cancellation, payment success, reminders, alerts, chat events.

## Recommended Study Order
1. Parking Lot
2. Event Booking System
3. Vending Machine
4. E-commerce Marketplace
5. Cab Booking System
6. Splitwise
7. Notification System
8. File System
9. Rate Limiter
10. Chess or Snake and Ladder
