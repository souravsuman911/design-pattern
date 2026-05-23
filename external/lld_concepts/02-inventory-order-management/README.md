# Inventory and Order Management

## Problem Shape
Products/items are selected, stock is checked, orders are created, payment happens, and fulfillment starts.
Examples: Amazon, food delivery, grocery, vending machine, warehouse.

## Core Model
- **Catalog**: What can be bought. Example: `iPhone 15`.
- **SKU/Item**: Sellable unit. Example: `IPHONE15-BLACK-128`.
- **Inventory**: Available quantity. Example: `stock = 3`.
- **Cart**: Temporary user selection.
- **Order**: Durable purchase record.
- **Status**: `CREATED -> PAID -> PACKED -> SHIPPED -> DELIVERED`.

## Deep Concepts With Compact Examples
- **Stock Reservation**: Temporarily block item during checkout. Example: reserve `1 phone` for `10 min`.
- **Atomic Stock Update**: Avoid selling last item twice. Example: `stock = stock - 1 where stock > 0`.
- **Price Snapshot**: Store order price. Example: order keeps `999`, even if product later becomes `1099`.
- **Partial Orders**: One item shipped, another cancelled.
- **Refund/Return**: Reverse payment and optionally restore stock.
- **Discount Strategy**: Apply coupon logic separately. Example: flat `100 off` or `10% off`.

## Inventory Safety Options
- **No Cart Reservation**: Stock reduces only on checkout. Simple but users may see out-of-stock later.
- **Reservation With TTL**: Reserve during checkout. Good for high-demand items.
- **Atomic Decrement**: One DB update prevents negative stock.
- **Stock Ledger**: Record every stock movement. Best for audit-heavy systems.
- **Warehouse Allocation**: Choose warehouse by distance/cost/stock.

## Interview Questions: Short Answers
- **When reduce inventory?** Usually at order/payment confirmation or reservation start.
- **Out of stock at checkout?** Fail gracefully and ask user to update cart.
- **Coupon handling?** Use discount strategies and validate rules before payment.
- **Cancel one item?** Track item-level status and refund that item.
- **Returns?** Create reverse transaction and update stock based on condition.

## Implementation Checklist
- Separate `Product` from `Inventory`.
- Add `Cart`, `Order`, `OrderItem`.
- Store price snapshot in order.
- Make stock update atomic.
- Add order state transitions.
- Keep discount/payment pluggable.
