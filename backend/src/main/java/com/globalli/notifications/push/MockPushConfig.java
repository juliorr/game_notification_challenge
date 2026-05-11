package com.globalli.notifications.push;

import com.globalli.notifications.support.BoundedInMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockPushConfig {

  @Bean
  public BoundedInMemoryStore<MockPushNotification> mockPushStore() {
    return new BoundedInMemoryStore<>(MockPushNotification::userId);
  }
}
