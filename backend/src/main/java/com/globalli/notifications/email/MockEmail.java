package com.globalli.notifications.email;

import java.time.Instant;
import java.util.UUID;

public record MockEmail(
    UUID id,
    long userId,
    String to,
    String subject,
    String body,
    Instant sentAt,
    UUID sourceNotificationId) {}
