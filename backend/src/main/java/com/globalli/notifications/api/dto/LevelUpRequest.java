package com.globalli.notifications.api.dto;

import jakarta.validation.constraints.Positive;

public record LevelUpRequest(@Positive long userId) {}
