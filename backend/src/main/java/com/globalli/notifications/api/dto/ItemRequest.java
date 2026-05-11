package com.globalli.notifications.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ItemRequest(@Positive long userId, @NotBlank String item) {}
