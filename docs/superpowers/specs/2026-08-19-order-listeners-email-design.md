# Design — Corpo dos Listeners de Order (Email de Confirmação)

- **Data:** 2026-08-19
- **Branch:** `feat/order-listeners`
- **Módulo:** `ecommerce` (Spring Boot 4.1.0, Java 21)

## Objetivo

Dar corpo aos dois listeners RabbitMQ que reagem ao `OrderPaidEvent`:

- **`EmailNotificationListener`** — enviar um email real de "compra realizada com sucesso" ao cliente.
- **`InvoiceGenerationListener`** — stub logado (a geração real da nota fica para outra sessão).

## Fora de escopo (deferrals conscientes)

- Geração real da nota fiscal (PDF / S3 / tabela `invoice`).
- Idempotência / dedup de email — o retry pode reenviar o email num cenário raro (envio ok, mas a confirmação do ack falha). Limitação **conhecida e aceita** por ora.
- Template HTML / i18n — corpo em texto simples (`SimpleMailMessage`).
- AWS SES — usamos SMTP (Mailtrap) via `JavaMailSender`.

## Arquitetura / Fluxo

```
processPayment (@Transactional — sessão Hibernate aberta)
  ├─ customerEmail = order.getUser().getEmail()   // String resolvida com a sessão viva
  ├─ customerName  = order.getUser().getName()
  └─ eventPublisher.publishEvent(OrderPaidEvent{...})   // Spring ApplicationEvent
        │  @TransactionalEventListener(AFTER_COMMIT)
        ▼
  OrderEventListener → publisher.publish(event)   // encaminha o record para o RabbitMQ (exchange order.paid)
        │
        ├─ emailQueue   → EmailNotificationListener(event)   → EmailService.sendPurchaseConfirmation(dto)
        └─ invoiceQueue → InvoiceGenerationListener(event)   → log stub
```

**Por que resolver o email como `String` na linha do `processPayment`:** o `OrderPaidEvent` cruza duas fronteiras onde a sessão Hibernate já está fechada — o `OrderEventListener` (roda em `AFTER_COMMIT`) e a serialização JSON para o RabbitMQ. Mandar o `User` (proxy `LAZY`) nesses pontos dispararia `LazyInitializationException`. A query do cart já faz `LEFT JOIN FETCH c.user`, então `order.getUser().getEmail()` não gera SELECT extra.

## Componentes

### Modificados

| Arquivo | Mudança |
|---|---|
| `messaging/dto/OrderPaidEvent.java` | `record` ganha `String customerEmail` e `String customerName`. |
| `payment/service/PaymentService.java` | Na construção do evento, popula `customerEmail`/`customerName` via `order.getUser()`. |
| `messaging/publisher/OrderEventPublisher.java` | Assinatura simplificada para `publish(OrderPaidEvent event)` — encaminha o record inteiro (elimina 5 args posicionais). |
| `messaging/listener/OrderEventListener.java` | Corpo vira `publisher.publish(event)`. |
| `messaging/listener/EmailNotificationListener.java` | Recebe `OrderPaidEvent event`, mapeia para `PurchaseConfirmation`, delega ao `EmailService`. |
| `messaging/listener/InvoiceGenerationListener.java` | Recebe `OrderPaidEvent event`, apenas loga (stub). |

### Novos — módulo `email/`

Convenção coerente com o projeto: abstração de comportamento mora em pacote de propósito (igual `payment/gateway/PaymentGateway`), **não** em `entity`.

```
email/
 ├─ dto/
 │   └─ PurchaseConfirmation.java   // record(UUID orderId, String userName, String userEmail, BigDecimal totalAmount)
 └─ service/
     ├─ EmailService.java           // interface: void sendPurchaseConfirmation(PurchaseConfirmation confirmation)
     └─ SmtpEmailService.java       // impl: usa JavaMailSender + SimpleMailMessage
```

**Direção da dependência:** o listener (módulo `messaging`) mapeia `OrderPaidEvent` → `PurchaseConfirmation` **inline (sem classe mapper dedicada)** e chama o `EmailService`. O mapper não vira classe própria de propósito: se morasse em `email/mapper/`, precisaria importar `messaging.dto.OrderPaidEvent` e inverteria a seta. Inline no listener, a conversão fica do lado do chamador. Assim o módulo `email` **não** depende de `messaging` — ele recebe um DTO próprio, mantendo a fronteira limpa.

### Config

- **`pom.xml`** — adicionar `org.springframework.boot:spring-boot-starter-mail` (versão gerida pelo parent 4.1.0, sem `<version>` — confirmado no BOM `spring-boot-dependencies:4.1.0`).
- **`application.properties`** — adicionar, seguindo o padrão de env vars já usado:
  ```properties
  spring.mail.host=${SPRING_MAIL_HOST}
  spring.mail.port=${SPRING_MAIL_PORT}
  spring.mail.username=${SPRING_MAIL_USERNAME}
  spring.mail.password=${SPRING_MAIL_PASSWORD}
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  app.mail.from=${APP_MAIL_FROM}
  ```
- **Retry → DLQ** — **já configurado** pelo Lucas e verificado:
  ```properties
  spring.rabbitmq.listener.simple.retry.enabled=true
  spring.rabbitmq.listener.simple.retry.max-retries=3
  spring.rabbitmq.listener.simple.default-requeue-rejected=false
  ```
  `max-retries` é o nome correto no Boot 4.x (`max-attempts` foi deprecado com nível `error` desde 4.0.0). Sem esta config, uma exceção no listener causaria requeue infinito e a DLX nunca receberia a mensagem.

## Contrato dos componentes

- **`EmailService`** (interface) — *o que faz:* envia a confirmação de compra. *Como usar:* `sendPurchaseConfirmation(PurchaseConfirmation)`. *Depende de:* nada de infra (abstração pura).
- **`SmtpEmailServiceTest`** — *o que faz:* monta `SimpleMailMessage` (from = `app.mail.from`, to = `confirmation.userEmail()`, subject = "Compra realizada com sucesso", corpo com nome + nº do pedido + total) e envia via `JavaMailSender`. *Depende de:* `JavaMailSender`, `app.mail.from`.
- **`EmailNotificationListener`** — *o que faz:* consome a `emailQueue`, mapeia o evento e delega. *Depende de:* `EmailService`.

## Tratamento de erro

- Exceção no listener → retry (até 3) → `RejectAndDontRequeueRecoverer` → mensagem vai para a DLQ.
- **SMTP transitório** (fora do ar) → retentativas dão conta.
- **Falha permanente** (credencial errada, email inválido) → após as retentativas, DLQ para inspeção.
- **A confirmar empiricamente:** a semântica de `max-retries` no Boot 4.x — se `=3` significa 3 entregas totais ou 1 inicial + 3 retentativas (4 total). Observar no log ao testar o caminho de falha. Não bloqueia o design.

## Testes

- **Unit `SmtpEmailServiceTest`** — `JavaMailSender` mockado; verifica destinatário, remetente, assunto e que `send()` foi chamado com a mensagem correta.
- **Unit `EmailNotificationListener`** — `EmailService` mockado; verifica o mapeamento `OrderPaidEvent` → `PurchaseConfirmation` e a delegação.
- **Integração (opcional/depois)** — Testcontainers RabbitMQ + `spring-boot-starter-mail-test` (GreenMail). Fora do escopo desta rodada.

## Riscos / notas

- Semântica de `max-retries` (ver acima).
- Módulo `email` propositalmente sem `entity`/`repository` — só entra se um dia formos persistir histórico de emails (feature própria, com migration).
