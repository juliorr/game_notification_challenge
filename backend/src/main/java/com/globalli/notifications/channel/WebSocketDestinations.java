package com.globalli.notifications.channel;

public final class WebSocketDestinations {

  public static final String TOPIC_PREFIX = "/topic/notifications/";
  private static final String READ_SUFFIX = "/read";
  private static final String READ_ALL_SUFFIX = "/read-all";
  private static final String CLEARED_SUFFIX = "/cleared";

  private WebSocketDestinations() {}

  public static String forUser(long userId) {
    return TOPIC_PREFIX + userId;
  }

  public static String readForUser(long userId) {
    return TOPIC_PREFIX + userId + READ_SUFFIX;
  }

  public static String readAllForUser(long userId) {
    return TOPIC_PREFIX + userId + READ_ALL_SUFFIX;
  }

  public static String clearedForUser(long userId) {
    return TOPIC_PREFIX + userId + CLEARED_SUFFIX;
  }
}
