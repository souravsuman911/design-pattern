# Financial and Ledger Systems

## Problem Shape
Money is moved, tracked, settled, refunded, and audited.
Examples: Splitwise, wallet, banking, payment system, stock trading.

## Core Model
- **Account/Wallet**: Owns balance.
- **Money**: Amount + currency.
- **Transaction**: Business event. Example: transfer ₹500.
- **Ledger Entry**: Immutable debit/credit record.
- **Balance**: Derived or cached account total.

## Deep Concepts With Compact Examples
- **Double Entry**: Debit one account, credit another.
- **Immutability**: Never edit old ledger entry; create reversal.
- **Precision**: Store paisa/cents as integer, not float.
- **Atomicity**: Debit and credit must succeed together.
- **Idempotency**: Same payment callback should process once.
- **Settlement**: Reduce many debts to fewer payments.

## Money Safety Options
- **Cached Balance + Ledger**: Fast reads with audit trail.
- **Ledger-Derived Balance**: Slower but highly reliable.
- **Reversal Entry**: Correct mistake without deleting history.
- **Idempotency Key**: Prevent duplicate transfers.
- **Transactional Locking**: Lock accounts during transfer.

## Interview Questions: Short Answers
- **Store balances?** Ledger for audit, cached balance for speed.
- **Avoid float errors?** Use integer minor units or decimal.
- **Failed transfer?** Roll back transaction or create compensating reversal.
- **Reverse transaction?** Add opposite ledger entries.
- **Audit history?** Keep immutable ledger entries.

## Implementation Checklist
- Create `Money` value object.
- Add `Account`, `Transaction`, `LedgerEntry`.
- Use debit/credit entries.
- Make processing atomic.
- Add idempotency key.
- Never mutate ledger history.
