package com.globalli.notifications.channel;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.Notification;

public interface NotificationChannel {

  DeliveryChannel channel();

  void send(Notification notification);
}
