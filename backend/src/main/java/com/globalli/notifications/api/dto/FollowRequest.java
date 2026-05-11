package com.globalli.notifications.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

public record FollowRequest(@Positive long followerUserId, @Positive long followedUserId) {

  @AssertTrue(message = "followerUserId must differ from followedUserId")
  public boolean isDifferentUsers() {
    return followerUserId != followedUserId;
  }
}
