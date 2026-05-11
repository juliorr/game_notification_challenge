package com.globalli.notifications.api;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.NotificationCategory;
import com.globalli.notifications.service.UserPreferenceService;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class PreferencesController {

  private final UserPreferenceService userPreferenceService;

  public PreferencesController(UserPreferenceService userPreferenceService) {
    this.userPreferenceService = userPreferenceService;
  }

  @GetMapping
  public PreferencesResponse get(@PathVariable long userId) {
    return snapshot(userId);
  }

  @PutMapping
  public PreferencesResponse update(
      @PathVariable long userId, @RequestBody UpdatePreferencesRequest body) {
    if (body.notificationsEnabled() != null) {
      userPreferenceService.setNotificationsEnabled(userId, body.notificationsEnabled());
    }
    if (body.enabledCategories() != null) {
      userPreferenceService.replaceEnabledCategories(userId, body.enabledCategories());
    }
    if (body.enabledChannels() != null) {
      userPreferenceService.replaceEnabledChannels(userId, body.enabledChannels());
    }
    return snapshot(userId);
  }

  private PreferencesResponse snapshot(long userId) {
    return new PreferencesResponse(
        userPreferenceService.isNotificationsEnabled(userId),
        userPreferenceService.getEnabledCategories(userId),
        userPreferenceService.getEnabledChannels(userId));
  }

  public record UpdatePreferencesRequest(
      Boolean notificationsEnabled,
      Set<NotificationCategory> enabledCategories,
      Set<DeliveryChannel> enabledChannels) {}

  public record PreferencesResponse(
      boolean notificationsEnabled,
      Set<NotificationCategory> enabledCategories,
      Set<DeliveryChannel> enabledChannels) {}
}
