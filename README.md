# Spring Boot @Transactional — Proof of Concept

10 demos covering all major `@Transactional` concepts in Spring Boot with an e-commerce domain (Order, Payment, Inventory, AuditLog).

**Tech Stack:** Java 21 · Spring Boot 3.x · Spring Data JPA · H2 In-Memory DB · Lombok · Maven

## Run

```bash
git clone https://github.com/gorkemkaragoz/transaction-demo.git
cd transaction-demo
mvn spring-boot:run
```

**H2 Console:** http://localhost:8080/h2-console → JDBC URL: `jdbc:h2:mem:transactiondemodb` · Username: `sa` · Password: *(empty)*

## Demos

| # | Concept | Endpoint | Key Takeaway |
|---|---------|----------|--------------|
| 1 | Basic @Transactional | `POST /api/orders/place` | Order + stock in same TX, rollback on failure |
| 2 | rollbackFor | `POST /api/orders/place-checked` | Checked exceptions don't rollback by default |
| 3 | Dirty Checking | `PUT /api/orders/{id}/confirm` | Managed entity auto-updates — no save() needed |
| 4 | readOnly | `GET /api/orders` | Disables dirty checking for read performance |
| 5 | timeout | `POST /api/orders/place-timeout` | Exceeding timeout triggers rollback |
| 6 | REQUIRES_NEW (Audit) | `POST /api/orders/place-with-audit` | Audit log survives parent rollback |
| 7 | Self-Invocation Trap | `POST /api/orders/self-invocation` | this.method() bypasses proxy |
| 8 | REQUIRED (Payment) | `POST /api/orders/place-with-payment` | Joins existing TX — rolls back together |
| 9 | REQUIRES_NEW (Payment) | `POST /api/orders/place-with-payment-new-tx` | Independent TX — commits separately |
| 10 | Cancel Order | `PUT /api/orders/{id}/cancel` | Atomic cancel + stock restore |

Console shows full transaction lifecycle (create, join, suspend, commit, rollback) via TRACE level logging.
