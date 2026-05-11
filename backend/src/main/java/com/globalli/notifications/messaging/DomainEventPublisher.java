package com.globalli.notifications.messaging;

import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.SocialEvent;
import com.globalli.notifications.events.UserActionEvent;

public interface DomainEventPublisher {

  void publish(GameEvent event);

  void publish(SocialEvent event);

  void publish(UserActionEvent event);
}
