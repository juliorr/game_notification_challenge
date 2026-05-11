package com.globalli.notifications.api;

import com.globalli.notifications.api.dto.UnreadCountResponse;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import com.globalli.notifications.messaging.DomainEventPublisher;
import com.globalli.notifications.service.NotificationQueryService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
public class NotificationsController {

  private final NotificationQueryService queryService;
  private final DomainEventPublisher eventPublisher;

  public NotificationsController(
      NotificationQueryService queryService, DomainEventPublisher eventPublisher) {
    this.queryService = queryService;
    this.eventPublisher = eventPublisher;
  }

  @GetMapping
  public List<Notification> get(
      @PathVariable long userId,
      @RequestParam(name = "cursor", required = false) Instant cursor,
      @RequestParam(name = "limit", required = false) Integer limit) {
    return queryService.findPage(userId, Optional.ofNullable(cursor), limit);
  }

  @GetMapping("/unread-count")
  public UnreadCountResponse unreadCount(@PathVariable long userId) {
    return new UnreadCountResponse(queryService.unreadCount(userId));
  }

  @PostMapping("/read")
  public ResponseEntity<Void> markAllRead(@PathVariable long userId) {
    eventPublisher.publish(new AllNotificationsRead(userId));
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/{notificationId}/read")
  public ResponseEntity<Void> markRead(
      @PathVariable long userId, @PathVariable UUID notificationId) {
    eventPublisher.publish(new NotificationRead(userId, notificationId));
    return ResponseEntity.accepted().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> clearAll(@PathVariable long userId) {
    eventPublisher.publish(new NotificationsCleared(userId));
    return ResponseEntity.accepted().build();
  }
}
