package internal.designPattern.external.lld.paymentGateway;
/*
1. Functional Requirements

Feature 1: Initiate payment requests from merchants/clients
Feature 2: Create temporary checkout sessions with payment details
Feature 3: Secure PCI-DSS compliant card data handling via tokenization
Feature 4: Transaction status tracking (pending, processing, success, failed)
Feature 5: Support for partial payments and returns/refunds
Feature 6: Webhook notifications for payment status updates

2. Non-Functional Requirements

Scale
Transaction Volume - 10,000+ TPS (Transactions Per Second)
Merchants - Millions of merchant accounts
Daily Transactions - Hundreds of millions per day

3. Core Entity

Entity 1: Merchant/Clients - Business accounts initiating payments with API keys
Entity 2: Customer/Users - End users making payments
Entity 3: PaymentIntent - User's intention to pay with status tracking
Entity 4: PaymentSession - Temporary checkout context with expiry
Entity 5: Transaction - Core payment record with amount, status, timestamps
Entity 6: PaymentMethod - Card details, tokenized references, billing info
Entity 7: WebhookEvent - Async notifications for status changes


4. API Designing

Payment Operations
POST /v1/payment-intents - Create payment intent with amount, currency, merchant details
POST /v1/payment-sessions - Create checkout session with cart details and return URLs
POST /v1/checkout/{pay} - Process checkout with payment method and session ID
GET /v1/payments/{payment_id} - Retrieve payment status and details

Management Operations
POST /v1/refunds - Initiate refund for completed payment
POST /v1/webhooks - Register webhook endpoint for notifications
GET /v1/reconciliation/report - Retrieve settlement and reconciliation reports

 */
enum PaymentStatus {
    PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED
}

class Merchant {
    String id;
    String apiKey;
    String name;
}

class User {
    String id;
    String name;
    String email;
    int phNo;
}

class PaymentIntent {
    String id;
    String merchantId;
    String userId;
    double amount;
    String currency;
    PaymentStatus status;
    Date creationTime;
}

class PaymentSession {
    String id;
    String paymentIntentId;
    long expiresAt;
}

class Transaction {
    String id;
    String paymentIntentId;
    double amount;
    String currency;
    PaymentStatus status;
    Date creationTime;
}

class PaymentMethod {
    String token;   // PCI-safe token
    String last4;
    String brand;
}

class WebhookEvent {
    String id;
    String eventType;
    String payload;
}

@RestController
@RequestMapping("/v1")
class PaymentController {

    private final PaymentService service;

    PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/payment-intents")
    public PaymentIntent createIntent(@RequestBody Map<String, Object> req) {
        return service.createIntent(
                (double) req.get("amount"),
                (String) req.get("currency"),
                (String) req.get("merchantId"),
                (String) req.get("userId")
        );
    }

    @PostMapping("/payment-sessions")
    public PaymentSession createSession(@RequestBody Map<String, String> req) {
        return service.createSession(req.get("paymentIntentId"));
    }

    @PostMapping("/checkout/{pay}")
    public Transaction checkout(@RequestBody PaymentMethod method,
                                @RequestParam String intentId) {
        return service.processPayment(intentId, method);
    }

    @GetMapping("/payments/{id}")
    public Transaction getPayment(@PathVariable String id) {
        Transaction tx = new Transaction();
        tx.id = id;
        tx.status = PaymentStatus.SUCCESS;
        return tx;
    }

    @PostMapping("/refunds")
    public Transaction refund(@RequestParam String transactionId,
                              @RequestParam double amount) {
        return service.refund(transactionId, amount);
    }
}

@RestController
@RequestMapping("/v1/webhooks")
class WebhookController {

    @PostMapping
    public void handle(@RequestBody WebhookEvent event) {
        System.out.println("Webhook received: " + event.eventType);
    }
}

@Service
class PaymentService {

    public PaymentIntent createIntent(double amount, String currency, String merchantId) {
        PaymentIntent pi = new PaymentIntent();
        pi.id = UUID.randomUUID().toString();
        pi.amount = amount;
        pi.currency = currency;
        pi.merchantId = merchantId;
        pi.status = PaymentStatus.PENDING;
        return pi;
    }

    public PaymentSession createSession(String intentId) {
        PaymentSession session = new PaymentSession();
        session.id = UUID.randomUUID().toString();
        session.paymentIntentId = intentId;
        session.expiresAt = System.currentTimeMillis() + 15 * 60_000;
        return session;
    }

    public Transaction processPayment(String intentId, PaymentMethod method) {
        Transaction tx = new Transaction();
        tx.id = UUID.randomUUID().toString();
        tx.paymentIntentId = intentId;
        tx.amount = 100; // simplified
        tx.status = PaymentStatus.SUCCESS; // simulate gateway success
        return tx;
    }

    public Transaction refund(String transactionId, double amount) {
        Transaction tx = new Transaction();
        tx.id = transactionId;
        tx.status = PaymentStatus.REFUNDED;
        return tx;
    }
}


public class PaymentGatewayMain {
    public static void main(String[] args) {

        PaymentService paymentService = new PaymentService();

        // 1️⃣ Merchant initiates payment intent
        PaymentIntent intent = paymentService.createIntent(
                1000.00,
                "INR",
                "merchant_123"
        );
        System.out.println("PaymentIntent created: " + intent.id);

        // 2️⃣ Create checkout session
        PaymentSession session = paymentService.createSession(intent.id);
        System.out.println("Checkout session created: " + session.id);

        // 3️⃣ Customer enters card → tokenized by gateway
        PaymentMethod method = new PaymentMethod();
        method.token = "tok_" + UUID.randomUUID();
        method.last4 = "4242";
        method.brand = "VISA";

        // 4️⃣ Process checkout
        Transaction transaction = paymentService.processPayment(intent.id, method);
        System.out.println("Payment status: " + transaction.status);

        // 5️⃣ Fetch payment status
        System.out.println("Fetching payment details...");
        System.out.println("Transaction ID: " + transaction.id);
        System.out.println("Status: " + transaction.status);

        // 6️⃣ Refund (partial)
        Transaction refund = paymentService.refund(transaction.id, 200.00);
        System.out.println("Refund status: " + refund.status);
    }

}
