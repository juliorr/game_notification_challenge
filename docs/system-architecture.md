# System Architecture

Full-system architecture diagram for the notification system.

<div style="width: 1200px; box-sizing: border-box; position: relative; background: #f5f3ff; padding: 20px; border-radius: 8px; border: 1px solid #c7c2ea;">
  <style scoped>
    .arch-wrapper { display: flex; gap: 12px; }.arch-sidebar { width: 175px; flex-shrink: 0; }.arch-main { flex: 1; min-width: 0; }.arch-title { text-align: center; font-size: 22px; font-weight: bold; color: #312e81; margin-bottom: 4px; }.arch-subtitle { text-align: center; font-size: 12px; color: #4338ca; margin-bottom: 16px; }
    .arch-layer { margin: 8px 0; padding: 14px; border-radius: 6px; box-shadow: 0 2px 8px rgba(67, 56, 202, 0.06); }.arch-layer-title { font-size: 13px; font-weight: bold; margin-bottom: 10px; text-align: center; }
    .arch-grid { display: grid; gap: 8px; }.arch-grid-2 { grid-template-columns: repeat(2, 1fr); }.arch-grid-3 { grid-template-columns: repeat(3, 1fr); }.arch-grid-4 { grid-template-columns: repeat(4, 1fr); }.arch-grid-5 { grid-template-columns: repeat(5, 1fr); }.arch-grid-6 { grid-template-columns: repeat(6, 1fr); }
    .arch-box { border-radius: 5px; padding: 8px; text-align: center; font-size: 11px; font-weight: 600; line-height: 1.35; color: #312e81; background: rgba(255, 255, 255, 0.85); border: 1px solid #c7d2fe; }.arch-box.highlight { background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%); border: 2px solid #4f46e5; }.arch-box.tech { font-size: 10px; color: #4338ca; background: rgba(238, 242, 255, 0.8); }
    .arch-layer.external { background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%); border: 2px dashed #94a3b8; }.arch-layer.external .arch-layer-title { color: #64748b; }.arch-layer.user { background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%); border: 2px solid #3b82f6; }.arch-layer.user .arch-layer-title { color: #1e40af; }.arch-layer.application { background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%); border: 2px solid #4f46e5; }.arch-layer.application .arch-layer-title { color: #3730a3; }.arch-layer.ai { background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%); border: 2px solid #7c3aed; }.arch-layer.ai .arch-layer-title { color: #5b21b6; }.arch-layer.data { background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%); border: 2px solid #9333ea; }.arch-layer.data .arch-layer-title { color: #7e22ce; }.arch-layer.infra { background: linear-gradient(135deg, #fae8ff 0%, #f5d0fe 100%); border: 2px solid #a855f7; }.arch-layer.infra .arch-layer-title { color: #86198f; }
    .arch-sidebar-panel { border-radius: 6px; padding: 10px; background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%); border: 2px solid #a5b4fc; margin-bottom: 8px; box-shadow: 0 1px 3px rgba(67, 56, 202, 0.04); }.arch-sidebar-title { font-size: 12px; font-weight: bold; text-align: center; color: #312e81; margin-bottom: 6px; }.arch-sidebar-item { font-size: 10px; text-align: center; color: #3730a3; background: rgba(255, 255, 255, 0.8); padding: 5px; border-radius: 4px; margin: 3px 0; border: 1px solid #c7d2fe; }.arch-sidebar-item.metric { background: #e0e7ff; border: 1px solid #6366f1; color: #3730a3; font-weight: 600; }
  </style>
  <div class="arch-title">Notification System — Architecture</div>
  <div class="arch-subtitle">Real-time gaming notifications · Spring Boot · RabbitMQ · PostgreSQL · STOMP/WebSocket</div>
  <div class="arch-wrapper">
    <div class="arch-sidebar">
      <div class="arch-sidebar-panel"><div class="arch-sidebar-title">Build & Tooling</div><div class="arch-sidebar-item">Makefile targets</div><div class="arch-sidebar-item">Maven 3.9 (container)</div><div class="arch-sidebar-item">Node 22 (container)</div><div class="arch-sidebar-item">google-java-format</div><div class="arch-sidebar-item">ESLint + Prettier</div></div>
      <div class="arch-sidebar-panel"><div class="arch-sidebar-title">Tests</div><div class="arch-sidebar-item">JUnit 5</div><div class="arch-sidebar-item">Testcontainers</div><div class="arch-sidebar-item">Postgres + RabbitMQ</div><div class="arch-sidebar-item">dind runner</div></div>
    </div>
    <div class="arch-main">
      <div class="arch-layer user">
        <div class="arch-layer-title">Client Layer · Browser</div>
        <div class="arch-grid arch-grid-4"><div class="arch-box">React 18 + TS<br><small>Vite · React Router</small></div><div class="arch-box highlight">STOMP.js Client<br><small>WebSocket subscriber</small></div><div class="arch-box">Notifications UI<br><small>List · badge · prefs</small></div><div class="arch-box">Mailbox / Push tabs<br><small>Mock channel views</small></div></div>
      </div>
      <div class="arch-layer application">
        <div class="arch-layer-title">Application Layer · Spring Boot Backend (Java 21)</div>
        <div class="arch-grid arch-grid-4"><div class="arch-box">REST Controllers<br><small>/api/** · Spring MVC</small></div><div class="arch-box highlight">STOMP Broker<br><small>/ws → /topic/notifications/{userId}</small></div><div class="arch-box">SimulatorController<br><small>GameEngine · SocialSystem</small></div><div class="arch-box">UserPreferenceService<br><small>per-user channel toggles</small></div></div>
        <div class="arch-grid arch-grid-3" style="margin-top: 8px;"><div class="arch-box highlight">RabbitDomainEventPublisher<br><small>Spring AMQP · topic exchange</small></div><div class="arch-box highlight">NotificationEventListener<br><small>@RabbitListener · 3 queues</small></div><div class="arch-box highlight">NotificationDispatcher<br><small>Factory · Store · Channels</small></div></div>
      </div>
      <div class="arch-layer ai">
        <div class="arch-layer-title">Delivery Channels</div>
        <div class="arch-grid arch-grid-4"><div class="arch-box">InAppWebSocketChannel<br><small>STOMP push to browser</small></div><div class="arch-box">ReadStatusChannel<br><small>read/cleared state sync</small></div><div class="arch-box">EmailMockChannel<br><small>BoundedInMemoryStore</small></div><div class="arch-box">PushMockChannel<br><small>BoundedInMemoryStore</small></div></div>
      </div>
      <div class="arch-layer data">
        <div class="arch-layer-title">Messaging & Persistence</div>
        <div class="arch-grid arch-grid-4"><div class="arch-box tech">Exchange<br><small>notifications.events (topic)</small></div><div class="arch-box tech">Queues<br><small>dispatch.{game,social,user}.q</small></div><div class="arch-box tech">DLX + DLQ<br><small>x-delivery-limit = 5</small></div><div class="arch-box tech">Quorum Queues<br><small>RabbitMQ 3.13</small></div></div>
        <div class="arch-grid arch-grid-3" style="margin-top: 8px;"><div class="arch-box tech">JpaNotificationStore<br><small>Spring Data JPA · Hibernate</small></div><div class="arch-box tech">PostgreSQL 16<br><small>cursor pagination · MAX 100</small></div><div class="arch-box tech">InMemoryNotificationStore<br><small>fast tests only</small></div></div>
      </div>
      <div class="arch-layer infra">
        <div class="arch-layer-title">Infrastructure · docker-compose</div>
        <div class="arch-grid arch-grid-5"><div class="arch-box tech">notif-frontend<br><small>nginx :5173 → :80</small></div><div class="arch-box tech">notif-backend<br><small>Spring Boot :8080</small></div><div class="arch-box tech">notif-rabbitmq<br><small>:5672 / :15672</small></div><div class="arch-box tech">notif-postgres<br><small>:5432 · pgdata volume</small></div><div class="arch-box tech">Healthchecks<br><small>actuator · pg_isready</small></div></div>
      </div>
      <div class="arch-layer external">
        <div class="arch-layer-title">External / Mocked Integrations</div>
        <div class="arch-grid arch-grid-3"><div class="arch-box tech">SMTP (mocked)<br><small>EmailMockChannel</small></div><div class="arch-box tech">APNs / FCM (mocked)<br><small>PushMockChannel</small></div><div class="arch-box tech">REST mock endpoints<br><small>/api/mock-emails · /api/mock-push</small></div></div>
      </div>
    </div>
    <div class="arch-sidebar">
      <div class="arch-sidebar-panel"><div class="arch-sidebar-title">Domain Events</div><div class="arch-sidebar-item">game.player.leveled-up</div><div class="arch-sidebar-item">game.player.defeated-pvp</div><div class="arch-sidebar-item">game.item.acquired</div><div class="arch-sidebar-item">game.challenge.completed</div><div class="arch-sidebar-item">social.friend.request-sent</div><div class="arch-sidebar-item">social.friend.request-accepted</div><div class="arch-sidebar-item">social.follower.new</div><div class="arch-sidebar-item">user.notification.read</div><div class="arch-sidebar-item">user.notifications.all-read</div><div class="arch-sidebar-item">user.notifications.cleared</div></div>
      <div class="arch-sidebar-panel"><div class="arch-sidebar-title">Reliability</div><div class="arch-sidebar-item metric">DLX retry limit: 5</div><div class="arch-sidebar-item metric">Quorum queues</div><div class="arch-sidebar-item">Cursor paging</div><div class="arch-sidebar-item">Page size: 100 max</div></div>
    </div>
  </div>
</div>

## Reading guide

- **Client Layer** — React + TypeScript SPA served by nginx; subscribes to STOMP
  topics `/topic/notifications/{userId}` and reads paginated history via REST.
- **Application Layer** — Spring Boot backend. The dispatch pipeline is
  highlighted (publisher → listener → dispatcher) since it is the spine of the
  system.
- **Delivery Channels** — implementations of `NotificationChannel`. `InApp` and
  `ReadStatus` are real (STOMP); `Email` and `Push` are in-process mocks backed
  by `BoundedInMemoryStore<T>` (cap 500).
- **Messaging & Persistence** — RabbitMQ topic exchange with three category
  queues (`game.#`, `social.#`, `user.#`) plus DLX/DLQ; PostgreSQL via JPA for
  durable history.
- **Infrastructure** — every component runs as a container under
  `docker-compose.yml`. The host only needs `docker` and `make`.
- **External / Mocked** — no real SMTP or APNs/FCM is wired in; the side panels
  expose the mock stores via REST and dedicated frontend tabs.
