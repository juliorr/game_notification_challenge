package com.globalli.notifications.push;

import java.time.Instant;
import java.util.UUID;

public record MockPushNotification(
    UUID id,
    long userId,
    String deviceToken,
    String title,
    String body,
    Instant sentAt,
    UUID sourceNotificationId) {}
