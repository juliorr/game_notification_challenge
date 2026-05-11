package com.globalli.notifications.service;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.NotificationCategory;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceService {

  private final Map<Long, EnumSet<NotificationCategory>> enabledCategoriesByUser =
      new ConcurrentHashMap<>();
  private final Map<Long, EnumSet<DeliveryChannel>> enabledChannelsByUser =
      new ConcurrentHashMap<>();
  private final Map<Long, Boolean> notificationsEnabledByUser = new ConcurrentHashMap<>();

  public boolean isNotificationsEnabled(long userId) {
    return notificationsEnabledByUser.getOrDefault(userId, Boolean.TRUE);
  }

  public void setNotificationsEnabled(long userId, boolean enabled) {
    notificationsEnabledByUser.put(userId, enabled);
  }

  public boolean isCategoryEnabled(long userId, NotificationCategory category) {
    return contains(categoriesFor(userId), category);
  }

  public Set<NotificationCategory> getEnabledCategories(long userId) {
    return snapshot(categoriesFor(userId));
  }

  public void setCategoryEnabled(long userId, NotificationCategory category, boolean enabled) {
    toggle(categoriesFor(userId), category, enabled);
  }

  public void replaceEnabledCategories(long userId, Set<NotificationCategory> enabled) {
    replaceAll(categoriesFor(userId), enabled, NotificationCategory.class);
  }

  public boolean isChannelEnabled(long userId, DeliveryChannel channel) {
    return contains(channelsFor(userId), channel);
  }

  public Set<DeliveryChannel> getEnabledChannels(long userId) {
    return snapshot(channelsFor(userId));
  }

  public void setChannelEnabled(long userId, DeliveryChannel channel, boolean enabled) {
    toggle(channelsFor(userId), channel, enabled);
  }

  public void replaceEnabledChannels(long userId, Set<DeliveryChannel> enabled) {
    replaceAll(channelsFor(userId), enabled, DeliveryChannel.class);
  }

  private EnumSet<NotificationCategory> categoriesFor(long userId) {
    return enabledCategoriesByUser.computeIfAbsent(
        userId, id -> EnumSet.allOf(NotificationCategory.class));
  }

  private EnumSet<DeliveryChannel> channelsFor(long userId) {
    return enabledChannelsByUser.computeIfAbsent(
        userId, id -> EnumSet.allOf(DeliveryChannel.class));
  }

  private static <E extends Enum<E>> boolean contains(EnumSet<E> target, E value) {
    synchronized (target) {
      return target.contains(value);
    }
  }

  private static <E extends Enum<E>> EnumSet<E> snapshot(EnumSet<E> source) {
    synchronized (source) {
      return EnumSet.copyOf(source);
    }
  }

  private static <E extends Enum<E>> void toggle(EnumSet<E> target, E value, boolean enabled) {
    synchronized (target) {
      if (enabled) {
        target.add(value);
      } else {
        target.remove(value);
      }
    }
  }

  private static <E extends Enum<E>> void replaceAll(
      EnumSet<E> target, Set<E> enabled, Class<E> enumType) {
    synchronized (target) {
      target.clear();
      for (E value : EnumSet.allOf(enumType)) {
        if (enabled.contains(value)) {
          target.add(value);
        }
      }
    }
  }
}
