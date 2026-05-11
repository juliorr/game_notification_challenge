package com.globalli.notifications.email;

import com.globalli.notifications.support.BoundedInMemoryStore;
import org.springframework.stereotype.Component;

@Component
public class MockEmailStore extends BoundedInMemoryStore<MockEmail> {

  public MockEmailStore() {
    super(MockEmail::userId);
  }
}
