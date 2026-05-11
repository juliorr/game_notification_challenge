package com.globalli.notifications.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

public record FriendRequestBody(@Positive long fromUserId, @Positive long toUserId) {

  @AssertTrue(message = "fromUserId must differ from toUserId")
  public boolean isDifferentUsers() {
    return fromUserId != toUserId;
  }
}
