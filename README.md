```
# Spring Boot @Transactional — Proof of Concept

10 demos covering all major `@Transactional` concepts in Spring Boot with an e-commerce domain (Order, Payment, Inventory, AuditLog).

## Tech Stack

Java 21, Spring Boot 3.x, Spring Data JPA, H2 In-Memory Database, Lombok, Maven

## Run

```bash
git clone https://github.com/gorkemkaragoz/transaction-demo.git
cd transaction-demo
mvn spring-boot:run
```

H2 Console: `http://localhost:8080/h2-console` — JDBC URL: `jdbc:h2:mem:transactiondemodb`, Username: `sa`, Password: empty

## Demos

| #  | Concept                  | Endpoint                          | Key Takeaway                                              |
|----|--------------------------|-----------------------------------|-----------------------------------------------------------|
| 1  | Basic @Transactional     | `POST /api/orders/place`          | Order + stock in same TX, rollback on failure              |
| 2  | rollbackFor              | `POST /api/orders/place-checked`  | Checked exceptions don't rollback without rollbackFor      |
| 3  | Dirty Checking           | `PUT /api/orders/{id}/confirm`    | Managed entity auto-updates at commit — no save() needed   |
| 4  | readOnly                 | `GET /api/orders`                 | Disables dirty checking for read-only performance          |
| 5  | timeout                  | `POST /api/orders/place-timeout`  | Exceeding timeout rolls back the transaction               |
| 6  | REQUIRES_NEW (AuditLog)  | `POST /api/orders/place-with-audit` | Audit log survives parent rollback                       |
| 7  | Self-Invocation Trap     | `POST /api/orders/self-invocation`  | this.method() bypasses proxy — @Transactional ignored    |
| 8  | REQUIRED (Payment)       | `POST /api/orders/place-with-payment` | Joins existing TX — rolls back together                |
| 9  | REQUIRES_NEW (Payment)   | `POST /api/orders/place-with-payment-new-tx` | New TX — payment commits independently          |
| 10 | Cancel Order             | `PUT /api/orders/{id}/cancel`     | Atomic cancel: status change + stock restore in same TX    |

## Testing

Import `transaction-demo.postman_collection.json` into Postman. Run demos sequentially and check H2 Console after each request.

## Transaction Logging

Console shows full transaction lifecycle (create, join, suspend, commit, rollback) via `TRACE` level logging.
```
