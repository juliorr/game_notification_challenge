package com.globalli.notifications.service;

import com.globalli.notifications.domain.NotificationType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {

  private static final String DEFAULT_RESOURCE = "notification-templates.properties";

  private enum TemplateSlot {
    MESSAGE("message."),
    EMAIL_SUBJECT("email.subject."),
    PUSH_TITLE("push.title.");

    private final String prefix;

    TemplateSlot(String prefix) {
      this.prefix = prefix;
    }

    String keyFor(NotificationType type) {
      return prefix + type.name();
    }
  }

  private final Properties templates;

  public NotificationTemplateService() {
    this(DEFAULT_RESOURCE);
  }

  NotificationTemplateService(String resourcePath) {
    this.templates = loadTemplates(resourcePath);
  }

  public String render(NotificationType type, TemplateContext context) {
    return interpolate(requireTemplate(TemplateSlot.MESSAGE, type), context.parameters());
  }

  public String emailSubject(NotificationType type) {
    return lookup(TemplateSlot.EMAIL_SUBJECT, type);
  }

  public String pushTitle(NotificationType type) {
    return lookup(TemplateSlot.PUSH_TITLE, type);
  }

  private String lookup(TemplateSlot slot, NotificationType type) {
    return templates.getProperty(slot.keyFor(type));
  }

  private String requireTemplate(TemplateSlot slot, NotificationType type) {
    String template = lookup(slot, type);
    if (template == null) {
      throw new IllegalStateException(
          "Missing notification template for key: %s".formatted(slot.keyFor(type)));
    }
    return template;
  }

  private static String interpolate(String template, Map<String, Object> parameters) {
    String result = template;
    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
      result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
    }
    return result;
  }

  private static Properties loadTemplates(String resourcePath) {
    Properties properties = new Properties();
    ClassPathResource resource = new ClassPathResource(resourcePath);
    try (InputStream stream = resource.getInputStream()) {
      properties.load(stream);
    } catch (IOException ex) {
      throw new IllegalStateException(
          "Unable to load notification templates: %s".formatted(resourcePath), ex);
    }
    return properties;
  }
}
