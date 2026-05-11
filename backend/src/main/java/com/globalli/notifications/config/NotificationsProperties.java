package com.globalli.notifications.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "notifications")
public record NotificationsProperties(
    @NotNull @Valid Pagination pagination, @NotNull @Valid Cors cors) {

  public record Pagination(@Min(1) int maxPageSize, @Min(1) int defaultPageSize) {}

  public record Cors(@NotEmpty List<String> allowedOrigins) {}
}
