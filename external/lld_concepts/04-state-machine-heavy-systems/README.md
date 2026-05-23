# State Machine Heavy Systems

## Problem Shape
The object behavior depends mainly on current state and allowed transitions.
Examples: ATM, vending machine, elevator, traffic signal, order, booking.

## Core Model
- **State**: Current phase. Example: `HAS_MONEY`.
- **Event/Action**: Trigger. Example: `selectItem()`.
- **Transition**: Valid state movement. Example: `HAS_MONEY -> ITEM_SELECTED`.
- **Guard**: Condition before transition. Example: item must be in stock.
- **Side Effect**: Action after transition. Example: dispense item.

## Deep Concepts With Compact Examples
- **Invalid Transition**: Block wrong action. Example: cannot dispense before payment.
- **State Pattern**: Each state handles actions differently.
- **Transition Table**: Map `(state, event) -> nextState`.
- **Side Effects**: Send notification only after successful transition.
- **Idempotency**: Same event twice should not corrupt state.

## Modeling Options
- **Enum Only**: Good when behavior is simple.
- **State Classes**: Good when each state has unique behavior.
- **Transition Table**: Good for many states/events.
- **Workflow Engine Style**: Good for configurable business flows.

## Interview Questions: Short Answers
- **What states exist?** List lifecycle phases clearly.
- **Valid transitions?** Define state diagram/table.
- **Invalid action?** Throw domain error or ignore safely.
- **Where side effects?** After transition succeeds.
- **How test?** Test every allowed and blocked transition.

## Implementation Checklist
- Draw state diagram first.
- Store current state on entity.
- Validate transition before mutation.
- Keep side effects separate.
- Persist state as enum/string.
- Add transition tests.
