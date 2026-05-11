package com.globalli.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.globalli.notifications.domain.NotificationType;
import org.junit.jupiter.api.Test;

class NotificationTemplateServiceTest {

  private final NotificationTemplateService service =
      new NotificationTemplateService("test-templates.properties");

  @Test
  void renderInterpolatesPlaceholders() {
    String rendered = service.render(NotificationType.LEVEL_UP, new TemplateContext.LevelUp(7));

    assertThat(rendered).isEqualTo("Welcome to level 7!");
  }

  @Test
  void emailSubjectReturnsConfiguredValue() {
    assertThat(service.emailSubject(NotificationType.FRIEND_REQUEST))
        .isEqualTo("You have a new friend request");
  }

  @Test
  void pushTitleReturnsConfiguredValue() {
    assertThat(service.pushTitle(NotificationType.FRIEND_REQUEST)).isEqualTo("New friend request");
  }

  @Test
  void emailSubjectReturnsNullForUnmappedType() {
    assertThat(service.emailSubject(NotificationType.LEVEL_UP)).isNull();
  }

  @Test
  void pushTitleReturnsNullForUnmappedType() {
    assertThat(service.pushTitle(NotificationType.LEVEL_UP)).isNull();
  }

  @Test
  void renderThrowsWhenMessageTemplateMissing() {
    assertThatThrownBy(
            () ->
                service.render(NotificationType.NEW_FOLLOWER, new TemplateContext.SocialActor(1L)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("message.NEW_FOLLOWER");
  }

  @Test
  void constructorThrowsWhenResourceCannotBeLoaded() {
    assertThatThrownBy(() -> new NotificationTemplateService("missing-templates.properties"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("missing-templates.properties");
  }

  @Test
  void defaultConstructorLoadsBundledTemplates() {
    NotificationTemplateService bundled = new NotificationTemplateService();

    String rendered = bundled.render(NotificationType.LEVEL_UP, new TemplateContext.LevelUp(3));

    assertThat(rendered).contains("3");
  }
}
