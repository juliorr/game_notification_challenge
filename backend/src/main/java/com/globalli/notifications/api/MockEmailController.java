package com.globalli.notifications.api;

import com.globalli.notifications.email.MockEmail;
import com.globalli.notifications.email.MockEmailStore;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock-emails")
public class MockEmailController {

  private final MockEmailStore mockEmailStore;

  public MockEmailController(MockEmailStore mockEmailStore) {
    this.mockEmailStore = mockEmailStore;
  }

  @GetMapping
  public List<MockEmail> list(@RequestParam(name = "userId", required = false) Long userId) {
    if (userId == null) {
      return mockEmailStore.findAll();
    }
    return mockEmailStore.findByUserId(userId);
  }

  @DeleteMapping
  public ResponseEntity<Void> clear() {
    mockEmailStore.clear();
    return ResponseEntity.noContent().build();
  }
}
