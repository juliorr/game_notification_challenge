package com.globalli.notifications.api;

import com.globalli.notifications.push.MockPushNotification;
import com.globalli.notifications.support.BoundedInMemoryStore;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock-push")
public class MockPushController {

  private final BoundedInMemoryStore<MockPushNotification> mockPushStore;

  public MockPushController(BoundedInMemoryStore<MockPushNotification> mockPushStore) {
    this.mockPushStore = mockPushStore;
  }

  @GetMapping
  public List<MockPushNotification> list(
      @RequestParam(name = "userId", required = false) Long userId) {
    if (userId == null) {
      return mockPushStore.findAll();
    }
    return mockPushStore.findByUserId(userId);
  }

  @DeleteMapping
  public ResponseEntity<Void> clear() {
    mockPushStore.clear();
    return ResponseEntity.noContent().build();
  }
}
