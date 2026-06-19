# Payment Gateway LLD

This document explains the low-level design for a payment gateway system. The current implementation is available in `PaymentGatewayMain.java`.

## Category

Financial and Ledger Systems

## Related Concept Reference

This problem aligns most closely with:

- `external/lld_concepts/10-financial-ledger-systems/README.md`

A payment gateway also overlaps with notification systems because payment state changes often trigger webhook callbacks, merchant alerts, customer receipts, and refund notifications. In this design:

- the payment gateway owns intent, session, transaction, and refund lifecycle
- downstream notification systems should consume payment events asynchronously

## Scope

The current implementation models a compact payment gateway flow with:

- merchant-initiated payment intent creation
- temporary checkout session creation
- tokenized payment method input
- payment processing
- transaction status lookup
- refund initiation
- webhook endpoint handling

It is an interview-friendly in-memory/service-only design, not a production-grade acquiring or settlement platform yet.

## Functional Requirements Handled

1. Create payment intents for a merchant and user.
2. Create short-lived checkout sessions tied to a payment intent.
3. Accept tokenized payment method details.
4. Process a checkout request into a transaction.
5. Track payment status using a payment status enum.
6. Support refund initiation.
7. Receive webhook events.
8. Expose API endpoints for the main payment lifecycle.

## Functional Requirements Not Yet Fully Modeled

1. Merchant authentication and API key validation.
2. Session validation and expiry enforcement during checkout.
3. Real card authorization and capture flow.
4. Partial capture support.
5. Idempotency for create, pay, refund, and webhook flows.
6. Failure reason codes and retry semantics.
7. Ledger entries for money movement.
8. Settlement to merchants and reconciliation reporting.
9. Chargeback and dispute lifecycle.
10. Webhook signature validation and retry delivery.
11. Multi-provider routing and fallback.
12. Currency conversion and FX handling.

## Non-Functional Requirements

1. **Correctness**: Payment status transitions must be controlled and auditable.
2. **Security**: Raw card data should never be stored directly; tokenization is required.
3. **Scalability**: The gateway should support high TPS and many merchants.
4. **Reliability**: Duplicate callbacks, retries, and partial failures must be handled safely.
5. **Observability**: Every payment attempt, refund, and webhook should be traceable.
6. **Extensibility**: New payment methods, providers, and risk checks should be easy to add.

## Current Class Diagram

```text
+------------------------+
| PaymentController      |
+------------------------+
| createIntent()         |
| createSession()        |
| checkout()             |
| getPayment()           |
| refund()               |
+------------------------+
            |
            v
+------------------------+
| PaymentService         |
+------------------------+
| createIntent()         |
| createSession()        |
| processPayment()       |
| refund()               |
+------------------------+

+------------------------+
| Merchant               |
+------------------------+
| id                     |
| apiKey                 |
| name                   |
+------------------------+

+------------------------+
| User                   |
+------------------------+
| id                     |
| name                   |
| email                  |
| phNo                   |
+------------------------+

+------------------------+
| PaymentIntent          |
+------------------------+
| id                     |
| merchantId             |
| userId                 |
| amount                 |
| currency               |
| status                 |
| creationTime           |
+------------------------+

+------------------------+
| PaymentSession         |
+------------------------+
| id                     |
| paymentIntentId        |
| expiresAt              |
+------------------------+

+------------------------+
| PaymentMethod          |
+------------------------+
| token                  |
| last4                  |
| brand                  |
+------------------------+

+------------------------+
| Transaction            |
+------------------------+
| id                     |
| paymentIntentId        |
| amount                 |
| currency               |
| status                 |
| creationTime           |
+------------------------+

+------------------------+
| WebhookController      |
+------------------------+
| handle()               |
+------------------------+

+------------------------+
| WebhookEvent           |
+------------------------+
| id                     |
| eventType              |
| payload                |
+------------------------+
```

## Main Entities and Responsibilities

### `Merchant`

Represents the business account initiating payments.

Fields:
- `id`
- `apiKey`
- `name`

### `User`

Represents the end customer making a payment.

Fields:
- `id`
- `name`
- `email`
- `phNo`

### `PaymentIntent`

Represents the customer's intention to pay a merchant for a specific amount.

Fields:
- `id`
- `merchantId`
- `userId`
- `amount`
- `currency`
- `status`
- `creationTime`

Responsibilities:
- acts as the top-level payment object visible to merchant systems
- tracks lifecycle before and during processing

### `PaymentSession`

Represents a temporary checkout context.

Fields:
- `id`
- `paymentIntentId`
- `expiresAt`

Responsibilities:
- encapsulates a short-lived checkout window
- allows UI or hosted payment page style flows

### `PaymentMethod`

Represents the tokenized funding instrument.

Fields:
- `token`
- `last4`
- `brand`

Responsibilities:
- avoids direct storage of sensitive card details
- carries only PCI-safe references in this model

### `Transaction`

Represents the actual payment attempt or refund result.

Fields:
- `id`
- `paymentIntentId`
- `amount`
- `currency`
- `status`
- `creationTime`

Responsibilities:
- records the operational outcome of processing
- is the primary object used for downstream status tracking

### `WebhookEvent`

Represents async status updates pushed to or received from external systems.

Fields:
- `id`
- `eventType`
- `payload`

### `PaymentService`

Acts as the orchestration layer for the payment lifecycle.

Responsibilities:
- create payment intents
- create payment sessions
- process payment requests
- initiate refunds

### `PaymentController`

Exposes the external payment APIs.

### `WebhookController`

Receives webhook traffic and triggers downstream processing.

## Enums and State

### `PaymentStatus`

- `PENDING`
- `PROCESSING`
- `SUCCESS`
- `FAILED`
- `REFUNDED`

## Current End-to-End Flow

```text
Merchant creates PaymentIntent
    -> system stores amount, currency, merchant, user
    -> merchant creates PaymentSession
    -> customer enters card details on checkout
    -> gateway uses tokenized PaymentMethod
    -> PaymentService processes payment
    -> Transaction is created with final status
    -> merchant can fetch payment details
    -> merchant may initiate refund later
    -> webhook events can notify status changes asynchronously
```

## Recommended Status Transition Model

The current code directly simulates success, but a more complete state machine is:

```text
PENDING -> PROCESSING -> SUCCESS
PENDING -> PROCESSING -> FAILED
SUCCESS -> REFUNDED
```

If capture is split from authorization, a richer state model may include:

- `AUTHORIZED`
- `CAPTURED`
- `PARTIALLY_REFUNDED`
- `CANCELLED`
- `EXPIRED`

## Schema Design

### In-Memory Object Model

- `PaymentController`: entry point for API requests.
- `PaymentService`: business orchestration layer.
- `PaymentIntent`: payment lifecycle root.
- `PaymentSession`: temporary checkout session.
- `PaymentMethod`: tokenized payment instrument.
- `Transaction`: payment or refund result object.
- `WebhookController`: async event receiver.
- `WebhookEvent`: webhook payload carrier.

### Production Database Mapping

#### `merchants`

- `merchant_id` PK
- `name`
- `api_key_hash`
- `status`
- `webhook_url`
- `created_at`
- `updated_at`

#### `customers`

- `customer_id` PK
- `merchant_id` FK
- `name`
- `email`
- `phone_number`
- `created_at`
- `updated_at`

#### `payment_intents`

- `payment_intent_id` PK
- `merchant_id` FK
- `customer_id` FK
- `amount_minor`
- `currency`
- `status`
- `idempotency_key`
- `description`
- `created_at`
- `updated_at`

#### `payment_sessions`

- `session_id` PK
- `payment_intent_id` FK
- `expires_at`
- `status`
- `return_url`
- `cancel_url`
- `created_at`

#### `payment_methods`

- `payment_method_id` PK
- `customer_id` FK nullable
- `provider_token`
- `last4`
- `brand`
- `type`
- `created_at`

#### `transactions`

- `transaction_id` PK
- `payment_intent_id` FK
- `merchant_id` FK
- `payment_method_id` FK nullable
- `amount_minor`
- `currency`
- `status`
- `gateway_reference`
- `failure_code` nullable
- `failure_message` nullable
- `created_at`
- `updated_at`

#### `refunds`

- `refund_id` PK
- `transaction_id` FK
- `amount_minor`
- `currency`
- `status`
- `reason`
- `created_at`
- `updated_at`

#### `ledger_entries`

- `entry_id` PK
- `transaction_id` FK
- `account_id`
- `entry_type`
- `amount_minor`
- `currency`
- `created_at`

#### `webhook_events`

- `webhook_event_id` PK
- `merchant_id` FK
- `event_type`
- `payload_json`
- `delivery_status`
- `retry_count`
- `created_at`
- `delivered_at` nullable

#### `idempotency_keys`

- `idempotency_key` PK
- `scope`
- `resource_id`
- `response_hash`
- `created_at`

## Recommended APIs

### Create Payment Intent

```http
POST /v1/payment-intents
```

Request:

```json
{
  "merchantId": "merchant_123",
  "userId": "user_456",
  "amount": 1000.00,
  "currency": "INR",
  "idempotencyKey": "checkout-order-101"
}
```

### Create Checkout Session

```http
POST /v1/payment-sessions
```

Request:

```json
{
  "paymentIntentId": "pi_123",
  "returnUrl": "https://merchant.app/success",
  "cancelUrl": "https://merchant.app/cancel"
}
```

### Process Checkout

```http
POST /v1/checkout/{pay}?intentId=pi_123
```

Request:

```json
{
  "token": "tok_abc123",
  "last4": "4242",
  "brand": "VISA"
}
```

### Get Payment

```http
GET /v1/payments/{paymentId}
```

### Refund Payment

```http
POST /v1/refunds?transactionId=tx_123&amount=200.00
```

### Receive Webhook

```http
POST /v1/webhooks
```

## HLD View

### Main Components

```text
Merchant App / Checkout UI
    |
    v
API Gateway
    |
    v
Payment API Service
    -> Merchant Auth Service
    -> Payment Orchestrator
    -> Tokenization / Vault Service
    -> Risk / Fraud Service
    -> Provider Router / Acquirer Connector
    -> Transaction Store
    -> Ledger / Accounting Service
    -> Refund Service
    -> Webhook Dispatcher
    -> Notification Service
    -> Reconciliation and Settlement Service
```

### Component Responsibilities

#### API Gateway

- handles authentication, throttling, request tracing, and TLS termination
- routes merchant API calls to payment services

#### Merchant Auth Service

- validates merchant API credentials
- enforces merchant-level permissions and quotas

#### Payment API Service

- exposes create intent, create session, pay, fetch status, and refund APIs
- validates request format and idempotency headers

#### Payment Orchestrator

- manages the payment lifecycle
- coordinates session checks, risk checks, provider calls, and transaction persistence

#### Tokenization / Vault Service

- converts raw card data into PCI-safe tokens
- stores or references sensitive payment method data securely

#### Risk / Fraud Service

- applies velocity checks, blacklist rules, device risk, geo mismatch, and merchant-specific controls

#### Provider Router / Acquirer Connector

- chooses the right bank, processor, or PSP
- handles provider-specific request and response mapping
- supports fallback routing if a provider is degraded

#### Transaction Store

- persists intents, sessions, transactions, refunds, and status history

#### Ledger / Accounting Service

- records money movement using immutable accounting entries
- powers reconciliation and audit

#### Refund Service

- validates refundable amount
- initiates refund against original transaction
- tracks partial or full refund lifecycle

#### Webhook Dispatcher

- delivers payment status events to merchants
- signs payloads and retries failed deliveries

#### Notification Service

- sends customer receipts, merchant alerts, and refund notifications
- should be downstream of payment events, not embedded in core processing

#### Reconciliation and Settlement Service

- matches internal records with provider reports
- computes merchant payouts, fees, chargebacks, and settlement status

## HLD Data Flows

### Payment Intent Creation

```text
Merchant App
    -> API Gateway
    -> Payment API Service
    -> Merchant Auth validation
    -> create payment_intent
    -> persist idempotency key
    -> return paymentIntentId
```

### Checkout and Payment Processing

```text
Customer Checkout
    -> create or load payment_session
    -> tokenize card details
    -> Payment Orchestrator validates session + intent
    -> Risk Service evaluates request
    -> Provider Router sends auth/capture request
    -> provider returns success or failure
    -> transaction persisted
    -> ledger entries created
    -> webhook event enqueued
    -> notification event published
```

### Refund Flow

```text
Merchant App
    -> Refund API
    -> validate original transaction and refundable balance
    -> send refund request to provider
    -> persist refund record
    -> add reversal or refund ledger entries
    -> publish refund webhook event
```

### Webhook Delivery Flow

```text
Payment event generated
    -> webhook dispatcher loads merchant webhook config
    -> signs payload
    -> sends HTTP callback
    -> marks delivered on success
    -> retries with backoff on failure
```

## Money and Ledger Notes

This is where the payment gateway should align with the financial systems concept doc.

Recommended production rules:

- store money in minor units such as paise or cents, not `double`
- record immutable transaction and ledger history
- use reversal entries instead of mutating historical money movement
- enforce idempotency for all externally retried APIs
- keep provider reference IDs for reconciliation

## Concurrency and Reliability Notes

### Current Implementation

- uses plain object creation without persistence
- simulates success directly in `processPayment()`
- has no duplicate protection
- does not enforce session expiry at checkout

This is fine for an interview-friendly skeleton, but it misses production safeguards.

### Production Evolution

1. Add idempotency keys on intent creation, payment processing, refunds, and webhook ingestion.
2. Persist all payment state changes.
3. Add transaction history and ledger entries.
4. Enforce checkout session expiry before processing.
5. Add provider timeout handling and retry policy.
6. Use outbox/eventing for webhook and notification fanout.
7. Add reconciliation jobs against provider settlement files.

## Design Patterns Used

1. **Service Layer Pattern**
   - `PaymentService` centralizes business orchestration.

2. **Controller Pattern**
   - `PaymentController` and `WebhookController` expose HTTP endpoints.

3. **State Machine Pattern**
   - `PaymentStatus` represents controlled lifecycle transitions.

4. **Factory / Strategy Extension Opportunity**
   - provider routing can later use strategy interfaces for different processors.

5. **Event-Driven Pattern**
   - webhook and notification fanout should run asynchronously from payment completion.

## Gaps In The Current Code

1. `createIntent()` call sites and method signature do not fully match the documented `userId` requirement.
2. Monetary values use `double`, which is unsafe for precise financial systems.
3. `processPayment()` hardcodes amount `100` instead of reading from the intent.
4. Session expiry is created but never validated during checkout.
5. `getPayment()` returns a synthetic success transaction instead of loading persisted data.
6. Refund flow does not validate refundable amount or original transaction state.
7. No persistence, repository, or database abstraction exists.
8. No idempotency handling exists.
9. No webhook signature verification or retry tracking exists.
10. Spring annotations and framework types are referenced, but imports and full wiring are not present in this standalone file.

## Suggested Refactoring Plan

1. Introduce repository interfaces for intents, sessions, transactions, refunds, and merchants.
2. Replace `double` with a `Money` value object or integer minor units.
3. Separate authorization, capture, refund, and webhook processing services.
4. Add idempotency middleware or service support.
5. Add provider adapter interfaces such as `PaymentProcessor`.
6. Add stable transaction and refund status history records.
7. Add ledger entry generation after success and refund flows.
8. Move webhook handling into verified, persisted, retryable event processing.

## Sample Interview Talking Points

1. Why do we need both `PaymentIntent` and `Transaction`?
   The intent represents the business request to pay, while the transaction represents an execution attempt against a provider.

2. Why tokenize cards?
   It reduces PCI exposure by storing only safe references instead of raw card details.

3. Why should money not use `double`?
   Floating-point precision can create incorrect balances and reconciliation errors.

4. How do you prevent duplicate charges?
   Use idempotency keys and provider reference mapping on every externally retried payment request.

5. Why separate payment processing from webhook delivery?
   Because merchant callbacks are slower and less reliable than core transaction processing, and they should not block payment completion.

## Status

Implementation present as a compact demo flow with API-oriented class structure.

Production-grade persistence, idempotency, ledger safety, provider routing, and webhook reliability are still pending.
