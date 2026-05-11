package com.globalli.notifications.api;

import java.time.Instant;
import java.util.List;

public record ApiError(
    String code, String message, Instant timestamp, List<FieldViolation> errors) {

  public static ApiError of(String code, String message) {
    return new ApiError(code, message, Instant.now(), List.of());
  }

  public static ApiError of(String code, String message, List<FieldViolation> errors) {
    return new ApiError(code, message, Instant.now(), errors);
  }

  public record FieldViolation(String field, String message) {}
}
